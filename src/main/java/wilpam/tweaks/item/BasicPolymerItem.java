package wilpam.tweaks.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class BasicPolymerItem extends Item implements PolymerItem {
    private final Identifier modelId;
    private final Item baseItem;

    public BasicPolymerItem(Properties settings, Identifier modelId, Item base) {
        super(settings);
        this.modelId = modelId;
        this.baseItem = base;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return baseItem;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack itemStack, PacketContext context, HolderLookup.Provider lookup) {
        return this.modelId;
    }
}