package vanilla.worldedit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;

import java.util.HashMap;
import java.util.Map;

public class WorldEdit extends Datapack {

	private static Domain domain = new Domain("worldedit");

	public WorldEdit() {
		super(domain);
	}

	public static final Map<EntityPlayer, Selection> map = new HashMap<>();

	@Override
	public void preinit() {
	}

	@Override
	public void init() {
		registrar.regCommand(new CommandSet());
	}

	@Override
	protected void unload() {

	}

}
