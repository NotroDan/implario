package optifine;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.Iterator;

public class BlockPosM extends BlockPos {
	private int mx;
	private int my;
	private int mz;
	private int level;
	private BlockPosM[] facings;
	private boolean needsUpdate;

	public BlockPosM(int mx, int my, int mz) {
		this(mx, my, mz, 0);
	}

	public BlockPosM(double mx, double my, double mz) {
		this(MathHelper.floor_double(mx), MathHelper.floor_double(my), MathHelper.floor_double(mz));
	}

	public BlockPosM(int mx, int my, int mz, int level) {
		super(0, 0, 0);
		this.mx = mx;
		this.my = my;
		this.mz = mz;
		this.level = level;
	}

	@Override
	public int getX() {
		return mx;
	}

	@Override
	public int getY() {
		return my;
	}

	@Override
	public int getZ() {
		return mz;
	}

	public void setXyz(int mx, int my, int mz) {
		this.mx = mx;
		this.my = my;
		this.mz = mz;
		this.needsUpdate = true;
	}

	public void setXyz(double mx, double my, double mz) {
		setXyz(MathHelper.floor_double(mx), MathHelper.floor_double(my), MathHelper.floor_double(mz));
	}

	@Override
	public BlockPos offset(EnumFacing facing) {
		if (level <= 0)
			return super.offset(facing, 1);
		if (facings == null)
			facings = new BlockPosM[EnumFacing.VALUES.length];

		if (needsUpdate)
			update();

		int i = facing.getIndex();
		BlockPosM blockposm = facings[i];

		if (blockposm == null) {
			int x = mx + facing.getFrontOffsetX();
			int y = my + facing.getFrontOffsetY();
			int z = mz + facing.getFrontOffsetZ();
			blockposm = new BlockPosM(x, y, z, level - 1);
			facings[i] = blockposm;
		}

		return blockposm;
	}

	@Override
	public BlockPos offset(EnumFacing facing, int n) {
		return n == 1 ? offset(facing) : super.offset(facing, n);
	}

	private void update() {
		for (int i = 0; i < 6; ++i) {
			BlockPosM blockposm = facings[i];

			if (blockposm != null) {
				EnumFacing enumfacing = EnumFacing.VALUES[i];
				int x = mx + enumfacing.getFrontOffsetX();
				int y = my + enumfacing.getFrontOffsetY();
				int z = mz + enumfacing.getFrontOffsetZ();
				blockposm.setXyz(x, y, z);
			}
		}

		needsUpdate = false;
	}
	
	public static Iterable getAllInBoxMutable(BlockPos onePos, BlockPos twoPos) {
		final BlockPos blockpos = new BlockPos(Math.min(onePos.getX(), twoPos.getX()), Math.min(onePos.getY(), twoPos.getY()),
				Math.min(onePos.getZ(), twoPos.getZ()));
		final BlockPos blockpos1 = new BlockPos(Math.max(onePos.getX(), twoPos.getX()), Math.max(onePos.getY(), twoPos.getY()),
				Math.max(onePos.getZ(), twoPos.getZ()));
		return new Iterable() {
			public Iterator iterator() {
				return new AbstractIterator() {
					private BlockPosM theBlockPosM = null;

					protected BlockPosM computeNext0() {
						if (theBlockPosM == null) {
							theBlockPosM = new BlockPosM(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 3);
							return theBlockPosM;
						}
						if (this.theBlockPosM.equals(blockpos1)) {
							return (BlockPosM) endOfData();
						}
						int x = theBlockPosM.getX();
						int y = theBlockPosM.getY();
						int z = theBlockPosM.getZ();

						if (x < blockpos1.getX())
							++x;
						else if (y < blockpos1.getY()) {
							x = blockpos.getX();
							++y;
						} else if (z < blockpos1.getZ()) {
							x = blockpos.getX();
							y = blockpos.getY();
							++z;
						}

						theBlockPosM.setXyz(x, y, z);
						return theBlockPosM;
					}

					protected Object computeNext() {
						return computeNext0();
					}
				};
			}
		};
	}

	public BlockPos getImmutable() {
		return new BlockPos(this.getX(), this.getY(), this.getZ());
	}
}
