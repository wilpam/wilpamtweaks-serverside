package wilpam.tweaks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface PistonMovedListener {
    void onMoved(Level level, BlockPos pos, BlockState movedState, Direction direction, boolean extending);
}
