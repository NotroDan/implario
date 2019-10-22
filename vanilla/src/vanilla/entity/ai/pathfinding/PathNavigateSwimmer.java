package vanilla.entity.ai.pathfinding;

import vanilla.entity.VanillaEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3d;
import net.minecraft.world.World;
import vanilla.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {

	public PathNavigateSwimmer(VanillaEntity entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}

	protected PathFinder getPathFinder() {
		return new PathFinder(new SwimNodeProcessor());
	}

	/**
	 * If on ground or swimming and can swim
	 */
	protected boolean canNavigate() {
		return this.isInLiquid();
	}

	protected Vec3d getEntityPosition() {
		return new Vec3d(this.theEntity.posX, this.theEntity.posY + (double) this.theEntity.height * 0.5D, this.theEntity.posZ);
	}

	protected void pathFollow() {
		Vec3d vec3D = this.getEntityPosition();
		float f = this.theEntity.width * this.theEntity.width;
		int i = 6;

		if (vec3D.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double) f) {
			this.currentPath.incrementPathIndex();
		}

		for (int j = Math.min(this.currentPath.getCurrentPathIndex() + i, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
			Vec3d vec31D = this.currentPath.getVectorFromIndex(this.theEntity, j);

			if (vec31D.squareDistanceTo(vec3D) <= 36.0D && this.isDirectPathBetweenPoints(vec3D, vec31D, 0, 0, 0)) {
				this.currentPath.setCurrentPathIndex(j);
				break;
			}
		}

		this.checkForStuck(vec3D);
	}

	/**
	 * Trims path data from the end to the first sun covered block
	 */
	protected void removeSunnyPath() {
		super.removeSunnyPath();
	}

	/**
	 * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
	 * pos1, pos2, entityXSize, entityYSize, entityZSize
	 */
	protected boolean isDirectPathBetweenPoints(Vec3d posVec31D, Vec3d posVec32D, int sizeX, int sizeY, int sizeZ) {
		MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(posVec31D, new Vec3d(posVec32D.xCoord, posVec32D.yCoord + (double) this.theEntity.height * 0.5D, posVec32D.zCoord), false,
				true, false);
		return movingobjectposition == null || movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
	}

}
