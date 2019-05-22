package net.minecraft.resources.event.events;

import net.minecraft.resources.event.Event;
import net.minecraft.world.WorldServer;

public class WorldTickEvent extends Event {

	private final WorldServer world;

	public WorldTickEvent(WorldServer worldServer) {
		this.world = worldServer;
	}

	public WorldServer getWorld() {
		return world;
	}

}
