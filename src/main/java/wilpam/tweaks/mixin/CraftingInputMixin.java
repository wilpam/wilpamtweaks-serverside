package wilpam.tweaks.mixin;

import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import wilpam.tweaks.mixin_helpers.CraftingInputMixinInterface;

@Mixin(CraftingInput.class)
public class CraftingInputMixin implements CraftingInputMixinInterface {
    @Unique
    public String playerName;

    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    @Override
    public String getPlayerName() {
        return playerName;
    }
}
