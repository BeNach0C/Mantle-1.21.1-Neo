package slimeknights.mantle.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.Map;

/** Fluid where up is down and down is up */
public abstract class InvertedFluid extends BaseFlowingFluid {
  protected InvertedFluid(Properties properties) {
    super(properties);
  }

  @Override
  public Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState fluid) {
    return super.getFlow(level, pos, fluid);
  }

  @Override
  protected boolean isSolidFace(BlockGetter level, BlockPos neighbor, Direction side) {
    BlockState block = level.getBlockState(neighbor);
    FluidState fluid = level.getFluidState(neighbor);
    return !fluid.getType().isSame(this) && (side == Direction.DOWN || !(block.getBlock() instanceof IceBlock) && block.isFaceSturdy(level, neighbor, side));
  }

  @Override
  protected void spread(Level level, BlockPos pos, FluidState fluid) {
    // Dummy
  }

  @Override
  protected FluidState getNewLiquid(Level level, BlockPos pos, BlockState block) {
    return Fluids.EMPTY.defaultFluidState();
  }

  public static class Flowing extends InvertedFluid {
    public Flowing(Properties properties) {
      super(properties);
      this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
      super.createFluidStateDefinition(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getAmount(FluidState state) {
      return state.getValue(LEVEL);
    }

    @Override
    public boolean isSource(FluidState state) {
      return false;
    }
  }

  public static class Source extends InvertedFluid {
    public Source(Properties properties) {
      super(properties);
    }

    @Override
    public int getAmount(FluidState state) {
      return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
      return true;
    }
  }
}

