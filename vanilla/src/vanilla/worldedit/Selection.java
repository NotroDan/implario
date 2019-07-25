package vanilla.worldedit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@AllArgsConstructor
public class Selection {

	@Getter
	@Setter
	private BlockPos one, two;

	public Selection(BlockPos one) {
		this(one, null);
	}

	public void set(World world, IBlockState set) {
		int maxX = Math.max(one.getX(), two.getX());
		int maxY = Math.max(one.getY(), two.getY());
		int maxZ = Math.max(one.getZ(), two.getZ());
		for (int x = Math.min(one.getX(), two.getX()); x < maxX; x++)
			for (int y = Math.min(one.getY(), two.getY()); y < maxY; y++)
				for (int z = Math.min(one.getZ(), two.getZ()); z < maxZ; z++)
					world.setBlockState(new BlockPos(x, y, z), set);
	}

}
