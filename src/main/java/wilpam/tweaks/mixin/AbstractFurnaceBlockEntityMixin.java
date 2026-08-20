package wilpam.tweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wilpam.tweaks.content.ModFuelSpeeds;
import wilpam.tweaks.content.ModRecipes;
import wilpam.tweaks.recipe.LootCookingRecipe;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Shadow
    private int cookingTimer;

    @Shadow
    private int cookingTotalTime;

    // The speed multiplier of the fuel.
    @Unique
    private double wilpamTweaks$speed = 1.0;

// The recipe's unscaled cooking time.
    @Unique
    private int wilpamTweaks$baseCookTotalTime = 0;

    // Per-furnace seed for loot-cooking recipes. Each recipe derives a stable,
    // deterministic roll from this seed (see LootCookingRecipe.rollResult(seed)),
    // and the seed only advances when a cook completes, so tampering with the
    // input (or switching between several random recipes) can never re-roll.
    @Unique
    private long wilpamTweaks$lootSeed = 0L;

    // Vanilla only returns the crafting remainder (e.g. an empty bucket) for the
    // fuel slot (see consumeFuel), not for the cooked ingredient. For recipes that
    // opt in via ModRecipes.RETURNS_REMAINDER, restore the ingredient's remainder
    // into the input slot once it is fully consumed.
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void wilpamTweaks$returnCraftingRemainder(NonNullList<ItemStack> items, ItemStack input,
                                                             ItemStack result, Operation<Void> original,
                                                             @Local(name = "recipe") RecipeHolder<?> recipe,
                                                             @Local(argsOnly = true, name = "entity") AbstractFurnaceBlockEntity entity) {
        // The input's crafting remainder must be read before burn() consumes it, since
        // once the stack is empty its item resolves to AIR (no remainder).
        ItemStackTemplate remainder = ModRecipes.RETURNS_REMAINDER.contains(recipe.id())
                ? input.getItem().getCraftingRemainder()
                : null;

        original.call(items, input, result);

        if (remainder != null && remainder.item().value() != Items.AIR && input.isEmpty()) {
            items.set(0, remainder.create());
        }

        // A cook has just finished, so the loot seed advances and the next cook
        // gets a fresh (deterministic) roll.
        if (recipe.value() instanceof LootCookingRecipe) {
            AbstractFurnaceBlockEntityMixin self = wilpamTweaks$self(entity);
            self.wilpamTweaks$lootSeed += 1L;
        }
    }

    // Loot-cooking recipes give a stable result for the whole of a cook. The roll
    // is derived deterministically from a per-furnace seed combined with the recipe
    // identity, so it can't be re-rolled by tampering with the input or by
    // switching between multiple random recipes; the seed only advances when a
    // cook completes (see burn above).
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/AbstractCookingRecipe;assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack wilpamTweaks$cacheLootRoll(AbstractCookingRecipe recipe, SingleRecipeInput input,
                                                        Operation<ItemStack> original,
                                                        @Local(argsOnly = true, name = "entity") AbstractFurnaceBlockEntity entity) {
        if (recipe instanceof LootCookingRecipe lootRecipe) {
            return lootRecipe.rollResult(wilpamTweaks$self(entity).wilpamTweaks$lootSeed);
        }
        return original.call(recipe, input);
    }

    // A fuel only starts burning once consumeFuel runs, so that is the
    // point where the speed multiplier should take effect. Reading the fuel's speed
    // here (before it is consumed) keeps the multiplier for the fuel's whole burn.
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;consumeFuel(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void wilpamTweaks$updateSpeedOnFuelUse(NonNullList<ItemStack> items, ItemStack fuel,
                                                          Operation<Void> original,
                                                          @Local(argsOnly = true, name = "entity") AbstractFurnaceBlockEntity entity) {
        wilpamTweaks$setSpeed(entity, ModFuelSpeeds.speedOf(fuel));
        original.call(items, fuel);
    }

    // Furnace progress ticks at a fixed +1 per server tick, so the speed multiplier
    // divides it by the fuel's rate instead
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/AbstractCookingRecipe;cookingTime()I"))
    private static int wilpamTweaks$scaleCookingTotalTime(AbstractCookingRecipe recipe, Operation<Integer> original,
                                                          @Local(argsOnly = true, name = "entity") AbstractFurnaceBlockEntity entity) {
        AbstractFurnaceBlockEntityMixin self = wilpamTweaks$self(entity);
        int vanillaTime = original.call(recipe);
        self.wilpamTweaks$baseCookTotalTime = vanillaTime;
        return scaledTotalTime(vanillaTime, self.wilpamTweaks$speed);
    }

    // Same as previous
    @ModifyReturnValue(method = "getTotalCookTime", at = @At("RETURN"))
    private static int wilpamTweaks$scaleInitialCookTotalTime(int original,
                                                              @Local(argsOnly = true, name = "entity") AbstractFurnaceBlockEntity entity) {
        AbstractFurnaceBlockEntityMixin self = wilpamTweaks$self(entity);
        self.wilpamTweaks$baseCookTotalTime = original;
        return scaledTotalTime(original, self.wilpamTweaks$speed);
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void wilpamTweaks$saveSpeed(ValueOutput output, CallbackInfo ci) {
        output.putDouble("wilpam_tweaks_speed", this.wilpamTweaks$speed);
        output.putInt("wilpam_tweaks_base_cook_time", this.wilpamTweaks$baseCookTotalTime);
    }

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    private void wilpamTweaks$loadSpeed(ValueInput input, CallbackInfo ci) {
        this.wilpamTweaks$speed = input.getDoubleOr("wilpam_tweaks_speed", 1.0);
        this.wilpamTweaks$baseCookTotalTime = input.getIntOr("wilpam_tweaks_base_cook_time", 0);
    }

    @Unique
    private static AbstractFurnaceBlockEntityMixin wilpamTweaks$self(AbstractFurnaceBlockEntity entity) {
        return (AbstractFurnaceBlockEntityMixin) (Object) entity;
    }

    @Unique
    private static void wilpamTweaks$setSpeed(AbstractFurnaceBlockEntity entity, double newSpeed) {
        AbstractFurnaceBlockEntityMixin self = wilpamTweaks$self(entity);
        if (newSpeed == self.wilpamTweaks$speed) {
            return;
        }
        if (self.wilpamTweaks$baseCookTotalTime > 0 && self.cookingTotalTime > 0) {
            int newTotal = scaledTotalTime(self.wilpamTweaks$baseCookTotalTime, newSpeed);
            if (self.cookingTimer > 0) {
                self.cookingTimer = Math.min(newTotal,
                        (int) Math.round((double) self.cookingTimer * newTotal / self.cookingTotalTime));
            }
            self.cookingTotalTime = newTotal;
        }
        self.wilpamTweaks$speed = newSpeed;
    }

    @Unique
    private static int scaledTotalTime(int cookTime, double speed) {
        if (speed <= 0.0) {
            speed = 1.0;
        }
        return Math.max(1, (int) Math.ceil(cookTime / speed));
    }
}
