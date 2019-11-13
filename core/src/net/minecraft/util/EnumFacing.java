package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumFacing implements IStringSerializable {
	DOWN(1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
	UP(0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
	NORTH(3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
	SOUTH(2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
	WEST( 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
	EAST( 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

	/**
	 * Index of the opposite Facing in the VALUES array
	 */
	private final int opposite;

	/**
	 * Ordering index for the HORIZONTALS field (S-W-N-E)
	 */
	private final int horizontalIndex;
	private final String name;
	private final EnumFacing.Axis axis;
	private final EnumFacing.AxisDirection axisDirection;

	/**
	 * Normalized Vector that points in the direction of this Facing
	 */
	private final Vec3i directionVec;

	/**
	 * All facings in D-U-N-S-W-E order
	 */
	public static final EnumFacing[] VALUES = values();

	/**
	 * All Facings with horizontal axis in order S-W-N-E
	 */
	public static final EnumFacing[] HORIZONTALS = new EnumFacing[4];
	private static final Map<String, EnumFacing> NAME_LOOKUP = new HashMap<>(6);


	EnumFacing(int opposite, int horizontalIndex, String name, EnumFacing.AxisDirection axisDirection, EnumFacing.Axis axis, Vec3i directionVec) {
		this.horizontalIndex = horizontalIndex;
		this.opposite = opposite;
		this.name = name;
		this.axis = axis;
		this.axisDirection = axisDirection;
		this.directionVec = directionVec;
	}

	/**
	 * Была убрана переменная из-за идентичности с ordinal()
	 * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
	 */
	public int getIndex() {
		return ordinal();
	}

	/**
	 * Get the index of this horizontal facing (0-3). The order is S-W-N-E
	 */
	public int getHorizontalIndex() {
		return horizontalIndex;
	}

	/**
	 * Get the AxisDirection of this Facing.
	 */
	public EnumFacing.AxisDirection getAxisDirection() {
		return axisDirection;
	}

	/**
	 * Get the opposite Facing (e.g. DOWN => UP)
	 */
	public EnumFacing getOpposite() {
		return VALUES[opposite];
	}

	/**
	 * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
	 * returns this facing without rotating.
	 */
	public EnumFacing rotateAround(EnumFacing.Axis axis) {
		switch (EnumFacing.EnumFacing$1.field_179515_a[axis.ordinal()]) {
			case 1:
				if (this != WEST && this != EAST) {
					return this.rotateX();
				}

				return this;

			case 2:
				if (this != UP && this != DOWN) {
					return this.rotateY();
				}

				return this;

			case 3:
				if (this != NORTH && this != SOUTH) {
					return this.rotateZ();
				}

				return this;

			default:
				throw new IllegalStateException("Unable to get CW facing for axis " + axis);
		}
	}

	/**
	 * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
	 */
	public EnumFacing rotateY() {
		switch (EnumFacing.EnumFacing$1.field_179513_b[this.ordinal()]) {
			case 1:
				return EAST;

			case 2:
				return SOUTH;

			case 3:
				return WEST;

			case 4:
				return NORTH;

			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	/**
	 * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
	 */
	private EnumFacing rotateX() {
		switch (EnumFacing.EnumFacing$1.field_179513_b[this.ordinal()]) {
			case 1:
				return DOWN;

			case 2:
			case 4:
			default:
				throw new IllegalStateException("Unable to get X-rotated facing of " + this);

			case 3:
				return UP;

			case 5:
				return NORTH;

			case 6:
				return SOUTH;
		}
	}

	/**
	 * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
	 */
	private EnumFacing rotateZ() {
		switch (EnumFacing.EnumFacing$1.field_179513_b[this.ordinal()]) {
			case 2:
				return DOWN;

			case 3:
			default:
				throw new IllegalStateException("Unable to get Z-rotated facing of " + this);

			case 4:
				return UP;

			case 5:
				return EAST;

			case 6:
				return WEST;
		}
	}

	/**
	 * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
	 */
	public EnumFacing rotateYCCW() {
		switch (EnumFacing.EnumFacing$1.field_179513_b[this.ordinal()]) {
			case 1:
				return WEST;

			case 2:
				return NORTH;

			case 3:
				return EAST;

			case 4:
				return SOUTH;

			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	/**
	 * Returns a offset that addresses the block in front of this facing.
	 */
	public int getFrontOffsetX() {
		return axis == EnumFacing.Axis.X ? axisDirection.getOffset() : 0;
	}

	public int getFrontOffsetY() {
		return axis == EnumFacing.Axis.Y ? axisDirection.getOffset() : 0;
	}

	/**
	 * Returns a offset that addresses the block in front of this facing.
	 */
	public int getFrontOffsetZ() {
		return axis == EnumFacing.Axis.Z ? axisDirection.getOffset() : 0;
	}

	/**
	 * Same as getName, but does not override the method from Enum.
	 */
	public String getName2() {
		return name;
	}

	public EnumFacing.Axis getAxis() {
		return axis;
	}

	/**
	 * Get the facing specified by the given name
	 */
	public static EnumFacing byName(String name) {
		return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
	}

	/**
	 * Get a Facing by it's index (0-5). The order is D-U-N-S-W-E. Named getFront for legacy reasons.
	 */
	public static EnumFacing getFront(int index) {
		return VALUES[MathHelper.abs_int(index % VALUES.length)];
	}

	/**
	 * Get a Facing by it's horizontal index (0-3). The order is S-W-N-E.
	 */
	public static EnumFacing getHorizontal(int p_176731_0_) {
		return HORIZONTALS[MathHelper.abs_int(p_176731_0_ % HORIZONTALS.length)];
	}

	/**
	 * Get the Facing corresponding to the given angle (0-360). An angle of 0 is SOUTH, an angle of 90 would be WEST.
	 */
	public static EnumFacing fromAngle(double angle) {
		return getHorizontal(MathHelper.floor_double(angle / 90.0D + 0.5D) & 3);
	}

	/**
	 * Choose a random Facing using the given Random
	 */
	public static EnumFacing random(Random rand) {
		return values()[rand.nextInt(values().length)];
	}

	public static EnumFacing getFacingFromVector(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
		EnumFacing enumfacing = NORTH;
		float f = Float.MIN_VALUE;

		for (EnumFacing enumfacing1 : values()) {
			float f1 = p_176737_0_ * (float) enumfacing1.directionVec.getX() + p_176737_1_ * (float) enumfacing1.directionVec.getY() + p_176737_2_ * (float) enumfacing1.directionVec.getZ();

			if (f1 > f) {
				f = f1;
				enumfacing = enumfacing1;
			}
		}

		return enumfacing;
	}

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public static EnumFacing func_181076_a(EnumFacing.AxisDirection p_181076_0_, EnumFacing.Axis p_181076_1_) {
		for (EnumFacing enumfacing : values()) {
			if (enumfacing.getAxisDirection() == p_181076_0_ && enumfacing.getAxis() == p_181076_1_) {
				return enumfacing;
			}
		}

		throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
	}

	/**
	 * Get a normalized Vector that points in the direction of this Facing.
	 */
	public Vec3i getDirectionVec() {
		return this.directionVec;
	}

	static {
		for (EnumFacing enumfacing : values()) {
			if (enumfacing.getAxis().isHorizontal())
				HORIZONTALS[enumfacing.horizontalIndex] = enumfacing;

			NAME_LOOKUP.put(enumfacing.getName2().toLowerCase(), enumfacing);
		}
	}

	static final class EnumFacing$1 {

		static final int[] field_179515_a;
		static final int[] field_179513_b;


		static {
			field_179513_b = new int[EnumFacing.values().length];

			try {
				field_179513_b[EnumFacing.NORTH.ordinal()] = 1;
			} catch (NoSuchFieldError var9) {
			}

			try {
				field_179513_b[EnumFacing.EAST.ordinal()] = 2;
			} catch (NoSuchFieldError var8) {
			}

			try {
				field_179513_b[EnumFacing.SOUTH.ordinal()] = 3;
			} catch (NoSuchFieldError var7) {
			}

			try {
				field_179513_b[EnumFacing.WEST.ordinal()] = 4;
			} catch (NoSuchFieldError var6) {
			}

			try {
				field_179513_b[EnumFacing.UP.ordinal()] = 5;
			} catch (NoSuchFieldError var5) {
			}

			try {
				field_179513_b[EnumFacing.DOWN.ordinal()] = 6;
			} catch (NoSuchFieldError var4) {
			}

			field_179515_a = new int[EnumFacing.Axis.values().length];

			try {
				field_179515_a[EnumFacing.Axis.X.ordinal()] = 1;
			} catch (NoSuchFieldError var3) {
			}

			try {
				field_179515_a[EnumFacing.Axis.Y.ordinal()] = 2;
			} catch (NoSuchFieldError var2) {
			}

			try {
				field_179515_a[EnumFacing.Axis.Z.ordinal()] = 3;
			} catch (NoSuchFieldError var1) {
			}
		}
	}

	public enum Axis implements Predicate, IStringSerializable {
		X("x", EnumFacing.Plane.HORIZONTAL),
		Y("y", EnumFacing.Plane.VERTICAL),
		Z("z", EnumFacing.Plane.HORIZONTAL);

		private static final Map NAME_LOOKUP = Maps.newHashMap();
		private final String name;
		private final EnumFacing.Plane plane;


		Axis(String name, EnumFacing.Plane plane) {
			this.name = name;
			this.plane = plane;
		}

		public static EnumFacing.Axis byName(String name) {
			return name == null ? null : (EnumFacing.Axis) NAME_LOOKUP.get(name.toLowerCase());
		}

		public String getName2() {
			return this.name;
		}

		public boolean isVertical() {
			return this.plane == EnumFacing.Plane.VERTICAL;
		}

		public boolean isHorizontal() {
			return this.plane == EnumFacing.Plane.HORIZONTAL;
		}

		public String toString() {
			return this.name;
		}

		public boolean apply(EnumFacing p_apply_1_) {
			return p_apply_1_ != null && p_apply_1_.getAxis() == this;
		}

		public EnumFacing.Plane getPlane() {
			return this.plane;
		}

		public String getName() {
			return this.name;
		}

		public boolean apply(Object p_apply_1_) {
			return this.apply((EnumFacing) p_apply_1_);
		}

		static {
			for (EnumFacing.Axis enumfacing$axis : values()) {
				NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(), enumfacing$axis);
			}
		}
	}

	public enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		@Getter
		private final int offset;
		private final String description;


		AxisDirection(int offset, String description) {
			this.offset = offset;
			this.description = description;
		}

		@Override
		public String toString() {
			return this.description;
		}
	}

	public enum Plane implements Predicate, Iterable<EnumFacing> {
		HORIZONTAL{
			@Override
			public EnumFacing[] facings() {
				return new EnumFacing[] {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
			}
		},
		VERTICAL{
			@Override
			public EnumFacing[] facings() {
				return new EnumFacing[] {EnumFacing.UP, EnumFacing.DOWN};
			}
		};

		public EnumFacing[] facings() {
			throw new Error("Someone\'s been tampering with the universe!");
		}

		public EnumFacing random(Random rand) {
			EnumFacing[] aenumfacing = facings();
			return aenumfacing[rand.nextInt(aenumfacing.length)];
		}

		@Override
		@Nonnull
		public Iterator<EnumFacing> iterator() {
			return Iterators.forArray(facings());
		}

		@Override
		public boolean apply(Object object) {
			if(!(object instanceof EnumFacing))return false;
			EnumFacing facing = (EnumFacing)object;
			return facing != null && facing.getAxis().getPlane() == this;
		}
	}
}
