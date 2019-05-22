package net.minecraft.resources.event.events;

import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.resources.event.Event;

public class UpdateEntityToSpectatorEvent extends Event {

	private final EntityTrackerEntry e;
	private final EntityPlayerMP p;

	public UpdateEntityToSpectatorEvent(EntityTrackerEntry e, EntityPlayerMP p) {
		this.e = e;
		this.p = p;
	}

	public EntityPlayerMP getPlayer() {
		return p;
	}

	public EntityTrackerEntry getTrackerEntry() {
		return e;
	}

}
