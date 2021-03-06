package enemeez.simplefarming.block.growable;

import java.util.Random;

import enemeez.simplefarming.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class OpuntiaBlock extends BushBlock implements IGrowable {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

	// X Axis hitbox
	private static final VoxelShape TOP_MIDDLE_X = Block.makeCuboidShape(4.0D, 10.0D, 7.0D, 12.0D, 12.0D, 9.0D);
	private static final VoxelShape MIDDLE_X = Block.makeCuboidShape(2.0D, 4.0D, 7.0D, 14.0D, 10.0D, 9.0D);
	private static final VoxelShape BOT_MIDDLE_X = Block.makeCuboidShape(4.0D, 2.0D, 7.0D, 12.0D, 4.0D, 9.0D);
	private static final VoxelShape BOT_X = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D);
	private static final VoxelShape FRUIT_1_X = Block.makeCuboidShape(12.0D, 10.0D, 7.0D, 14.0D, 12.0D, 9.0D);
	private static final VoxelShape FRUIT_2_X = Block.makeCuboidShape(10.0D, 12.0D, 7.0D, 12.0D, 14.0D, 9.0D);
	private static final VoxelShape FRUIT_3_X = Block.makeCuboidShape(7.0D, 12.0D, 7.0D, 9.0D, 14.0D, 9.0D);
	private static final VoxelShape FRUIT_4_X = Block.makeCuboidShape(4.0D, 12.0D, 7.0D, 6.0D, 14.0D, 9.0D);
	private static final VoxelShape FRUIT_5_X = Block.makeCuboidShape(2.0D, 10.0D, 7.0D, 4.0D, 12.0D, 9.0D);
	private static final VoxelShape UNRIPE_X = VoxelShapes.or(TOP_MIDDLE_X, BOT_MIDDLE_X, MIDDLE_X, BOT_X);
	private static final VoxelShape RIPE_X = VoxelShapes.or(TOP_MIDDLE_X, BOT_MIDDLE_X, MIDDLE_X, BOT_X, FRUIT_1_X,
			FRUIT_2_X, FRUIT_3_X, FRUIT_4_X, FRUIT_5_X);

	// Z Axis hitbox
	private static final VoxelShape TOP_MIDDLE_Z = Block.makeCuboidShape(7.0D, 10.0D, 4.0D, 9.0D, 12.0D, 12.0D);
	private static final VoxelShape MIDDLE_Z = Block.makeCuboidShape(7.0D, 4.0D, 2.0D, 9.0D, 10.0D, 14.0D);
	private static final VoxelShape BOT_MIDDLE_Z = Block.makeCuboidShape(7.0D, 2.0D, 4.0D, 9.0D, 4.0D, 12.0D);
	private static final VoxelShape BOT_Z = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D); // good
	private static final VoxelShape FRUIT_1_Z = Block.makeCuboidShape(7.0D, 10.0D, 12.0D, 9.0D, 12.0D, 14.0D);
	private static final VoxelShape FRUIT_2_Z = Block.makeCuboidShape(7.0D, 12.0D, 10.0D, 9.0D, 14.0D, 12.0D);
	private static final VoxelShape FRUIT_3_Z = Block.makeCuboidShape(7.0D, 12.0D, 7.0D, 9.0D, 14.0D, 9.0D);
	private static final VoxelShape FRUIT_4_Z = Block.makeCuboidShape(7.0D, 12.0D, 4.0D, 9.0D, 14.0D, 6.0D);
	private static final VoxelShape FRUIT_5_Z = Block.makeCuboidShape(7.0D, 10.0D, 2.0D, 9.0D, 12.0D, 4.0D);
	private static final VoxelShape UNRIPE_Z = VoxelShapes.or(TOP_MIDDLE_Z, BOT_MIDDLE_Z, MIDDLE_Z, BOT_Z);
	private static final VoxelShape RIPE_Z = VoxelShapes.or(TOP_MIDDLE_Z, BOT_MIDDLE_Z, MIDDLE_Z, BOT_Z, FRUIT_1_Z,
			FRUIT_2_Z, FRUIT_3_Z, FRUIT_4_Z, FRUIT_5_Z);

	public OpuntiaBlock(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction direction = state.get(FACING);
		return direction.getAxis() == Direction.Axis.X ? isMaxAge(state) ? RIPE_X : UNRIPE_X
				: isMaxAge(state) ? RIPE_Z : UNRIPE_Z;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return getShape(state, worldIn, pos, context);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE).add(FACING);
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(ModItems.cactus_crop);
	}

	public boolean isMaxAge(BlockState state) {
		return state.get(AGE) == 3;
	}

	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	// method
	@Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		super.tick(state, worldIn, pos, random);
		int i = state.get(AGE);
		if (i < 3 && random.nextInt(5) == 0 && worldIn.getLightSubtracted(pos.up(), 0) >= 9) {
			worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
		}

	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return net.minecraftforge.common.PlantType.Desert;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).getBlock().isIn(BlockTags.SAND);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (isMaxAge(state))
			entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
	}

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return state.get(AGE) < 3;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	// Grow method
	@Override
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		int i = Math.min(3, state.get(AGE) + 1);
		worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i)), 2);
	}

}