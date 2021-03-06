package enemeez.simplefarming.block;

import enemeez.simplefarming.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GrapeBlock extends BushBlock {

	public GrapeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		if (world.getBlockState(pos.up()).getBlock() == ModBlocks.grape_leaves)
			return true;

		return false;
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof LivingEntity) {
			entityIn.setMotionMultiplier(state, new Vec3d((double) 0.8F, 0.75D, (double) 0.8F));
		}
	}

	@Override
	public boolean causesSuffocation(BlockState state, IBlockReader reader, BlockPos pos) {
		return false;
	}

}
