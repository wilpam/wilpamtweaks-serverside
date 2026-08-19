package wilpam.tweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wilpam.tweaks.content.ModRecipes;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    // Vanilla only returns the crafting remainder (e.g. an empty bucket) for the
    // fuel slot (see consumeFuel), not for the cooked ingredient. For recipes that
    // opt in via ModRecipes.RETURNS_REMAINDER, restore the ingredient's remainder
    // into the input slot once it is fully consumed.
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void wilpamTweaks$returnCraftingRemainder(NonNullList<ItemStack> items, ItemStack input,
                                                             ItemStack result, Operation<Void> original,
                                                             @Local RecipeHolder<?> recipe) {
        // The input's crafting remainder must be read before burn() consumes it, since
        // once the stack is empty its item resolves to AIR (no remainder).
        ItemStackTemplate remainder = ModRecipes.RETURNS_REMAINDER.contains(recipe.id())
                ? input.getItem().getCraftingRemainder()
                : null;

        original.call(items, input, result);

        if (remainder != null && remainder.item().value() != Items.AIR && input.isEmpty()) {
            items.set(0, remainder.create());
        }
    }
}