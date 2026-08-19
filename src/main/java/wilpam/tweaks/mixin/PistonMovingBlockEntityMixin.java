package wilpam.tweaks.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wilpam.tweaks.block.PistonMovedListener;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {

    // Fires after a piston movement finishes and the moved block has been placed.
    @Inject(method = "tick", at = @At("TAIL"))
    private static void wilpamTweaks$onMoveFinished(Level level, BlockPos pos, BlockState state,
                                                    PistonMovingBlockEntity movingBlock, CallbackInfo ci) {
        if (level.isClientSide()) {
            return;
        }

        BlockState movedState = movingBlock.getMovedState();
        if (!(movedState.getBlock() instanceof PistonMovedListener pistonMovedListener)) {
            return;
        }

        if (movingBlock.isExtending()) {
            return;
        }

        pistonMovedListener.onMoved(level, pos, movedState, movingBlock.getDirection(), false);
    }
}