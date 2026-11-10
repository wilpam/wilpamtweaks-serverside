package wilpam.tweaks.mixin;

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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import wilpam.tweaks.content.ModRecipes;
import wilpam.tweaks.recipe.LootCookingRecipe;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

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

    @Unique
    private static AbstractFurnaceBlockEntityMixin wilpamTweaks$self(AbstractFurnaceBlockEntity entity) {
        return (AbstractFurnaceBlockEntityMixin) (Object) entity;
    }
}
