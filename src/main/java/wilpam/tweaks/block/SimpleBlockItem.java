package wilpam.tweaks.block;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class SimpleBlockItem extends BlockItem implements PolymerItem {
    private final Identifier modelId;
    private final Item fallbackItem;

    public SimpleBlockItem(Block block, Properties settings, Item fallbackItem, Identifier modelId) {
        super(block, settings);
        this.fallbackItem = fallbackItem;
        this.modelId = modelId;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return fallbackItem;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack itemStack, PacketContext context, HolderLookup.Provider lookup) {
        return this.modelId;
    }
}