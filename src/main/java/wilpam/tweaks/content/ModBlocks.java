package wilpam.tweaks.content;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.Item;
import wilpam.tweaks.block.FlintBlock;
import wilpam.tweaks.block.SimpleBlockItem;

public final class ModBlocks {
    public static final String ID = "wilpam_tweaks";

    public static final Identifier FLINT_BLOCK_ID = Identifier.fromNamespaceAndPath(ID, "flint_block");

    public static final FlintBlock FLINT_BLOCK = new FlintBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(1.0F, 6.0F)
                    .setId(ResourceKey.create(Registries.BLOCK, FLINT_BLOCK_ID)),
            Identifier.fromNamespaceAndPath(ID, "block/flint_block"));

    public static final SimpleBlockItem FLINT_BLOCK_ITEM = new SimpleBlockItem(
            FLINT_BLOCK,
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, FLINT_BLOCK_ID)),
            Items.TUFF,
            Identifier.fromNamespaceAndPath(ID, "-/item/flint_block"));

    private ModBlocks() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, FLINT_BLOCK_ID, FLINT_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, FLINT_BLOCK_ID, FLINT_BLOCK_ITEM);
    }
}