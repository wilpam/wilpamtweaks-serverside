package wilpam.tweaks.mixin;

import wilpam.tweaks.mixin_helpers.CraftingInputMixinInterface;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "HEAD"), cancellable = true)
    private static void slotChangedCraftingGrid(AbstractContainerMenu abstractContainerMenu,
                                                ServerLevel serverLevel,
                                                Player player,
                                                CraftingContainer craftingContainer,
                                                ResultContainer resultContainer,
                                                @Nullable RecipeHolder<CraftingRecipe> recipeHolder,
                                                CallbackInfo info) {
        CraftingInput craftingInput = craftingContainer.asCraftInput();
        ((CraftingInputMixinInterface) craftingInput).setPlayerName(player.getPlainTextName());
        ServerPlayer serverPlayer = (ServerPlayer)player;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<RecipeHolder<CraftingRecipe>> optional = serverLevel.getServer()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, craftingInput, serverLevel, recipeHolder);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeHolder2 = (RecipeHolder<CraftingRecipe>)optional.get();
            CraftingRecipe craftingRecipe = recipeHolder2.value();
            if (resultContainer.setRecipeUsed(serverPlayer, recipeHolder2)) {
                ItemStack itemStack2 = craftingRecipe.assemble(craftingInput);
                if (itemStack2.isItemEnabled(serverLevel.enabledFeatures())) {
                    itemStack = itemStack2;
                }
            }
        }

        resultContainer.setItem(0, itemStack);
        abstractContainerMenu.setRemoteSlot(0, itemStack);
        serverPlayer.connection
                .send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, abstractContainerMenu.incrementStateId(), 0, itemStack));
        info.cancel();
    }
}
