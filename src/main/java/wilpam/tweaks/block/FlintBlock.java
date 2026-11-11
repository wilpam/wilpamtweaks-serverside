package wilpam.tweaks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FlintBlock extends SimpleBlock implements PistonMovedListener {
    // Blocks that can produce a spark when flint is moved against them
    public static final TagKey<Block> FLINT_METALS = TagKey.create(Registries.BLOCK,
            Identifier.fromNamespaceAndPath("wilpam_tweaks", "flint_metal"));

    public FlintBlock(Properties settings, Identifier modelId) {
        super(settings, modelId);
    }

    public static boolean canBlockCreateSpark(BlockState state, Level level, BlockPos pos, Direction face) {
        return state.is(FLINT_METALS) && state.isFaceSturdy(level, pos, face);
    }

    @Override
    public void onMoved(Level level, BlockPos pos, BlockState movedState, Direction direction, boolean extending) {
        if (extending || level.isClientSide()) {
            return;
        }

        BlockPos firePos = pos.relative(direction);
        if (!level.getBlockState(firePos).isAir()) {
            return;
        }

        for (Direction ironDir : Direction.values()) {
            if (ironDir.getAxis() == direction.getAxis()) {
                continue;
            }

            BlockPos ironPos = firePos.relative(ironDir);
            BlockState facingState = level.getBlockState(ironPos);
            if (canBlockCreateSpark(facingState, level, ironPos, ironDir.getOpposite())) {
                ignitePosition(level, firePos, false);
                return;
            }
        }
    }

    private static void ignitePosition(Level level, BlockPos pos, boolean always) {
        BlockState fireState = BaseFireBlock.getState(level, pos);
        if (level.getBlockState(pos).isAir() && (always || BaseFireBlock.canBePlacedAt(level, pos, Direction.UP))) {
            level.setBlockAndUpdate(pos, fireState);
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        }
    }
}
