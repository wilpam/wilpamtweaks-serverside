package wilpam.tweaks.content;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.Item;
import wilpam.tweaks.block.FlintBlock;
import wilpam.tweaks.block.FlintBlockItem;

public final class ModBlocks {
    public static final String ID = "wilpam_tweaks";

    public static final Identifier FLINT_BLOCK_ID = Identifier.fromNamespaceAndPath(ID, "flint_block");

    public static final FlintBlock FLINT_BLOCK = new FlintBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)
                    .setId(ResourceKey.create(Registries.BLOCK, FLINT_BLOCK_ID)),
            Identifier.fromNamespaceAndPath(ID, "block/flint_block"));

    public static final FlintBlockItem FLINT_BLOCK_ITEM = new FlintBlockItem(
            FLINT_BLOCK,
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, FLINT_BLOCK_ID)),
            Identifier.fromNamespaceAndPath(ID, "-/item/flint_block"));

    private ModBlocks() {
    }

    public static void register() {
        Registry.register((Registry<? super Block>) (Registry<?>) BuiltInRegistries.BLOCK, FLINT_BLOCK_ID, FLINT_BLOCK);
        Registry.register((Registry<? super Item>) (Registry<?>) BuiltInRegistries.ITEM, FLINT_BLOCK_ID, FLINT_BLOCK_ITEM);
    }
}