package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.block.FlintBlock;
import wilpam.tweaks.content.ModBlocks;

import java.util.concurrent.CompletableFuture;

public final class ModBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        builder(FlintBlock.FLINT_METALS)
                .add(key(Blocks.IRON_BLOCK), key(Blocks.RAW_IRON_BLOCK), key(Blocks.IRON_BARS), key(Blocks.IRON_DOOR),
                        key(Blocks.IRON_TRAPDOOR), key(Blocks.CAULDRON), key(Blocks.IRON_CHAIN), key(Blocks.HOPPER),
                        key(Blocks.IRON_ORE), key(Blocks.DEEPSLATE_IRON_ORE), key(Blocks.ANVIL), key(Blocks.CHIPPED_ANVIL),
                        key(Blocks.DAMAGED_ANVIL));

        builder(BlockTags.MINEABLE_WITH_PICKAXE).add(key(ModBlocks.FLINT_BLOCK));
    }

    private static ResourceKey<Block> key(Block block) {
        //noinspection deprecation
        return block.builtInRegistryHolder().key();
    }

    @Override
    public @NonNull String getName() {
        return "Mod Block Tags";
    }
}
