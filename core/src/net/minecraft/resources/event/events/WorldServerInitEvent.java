package net.minecraft.resources.event.events;

import net.minecraft.resources.event.Event;
import net.minecraft.world.WorldServer;

public class WorldServerInitEvent extends Event {

	private final WorldServer worldServer;

	public WorldServerInitEvent(WorldServer worldServer) {
		this.worldServer = worldServer;
	}

	public WorldServer getWorldServer() {
		return worldServer;
	}

}
