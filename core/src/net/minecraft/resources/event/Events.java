package net.minecraft.resources.event;

import net.minecraft.resources.event.events.MountMoveEvent;
import net.minecraft.resources.event.events.player.*;

public class Events {

	public static EventManager<PlayerMoveEvent>    eventPlayerMove    = new EventManager<>();
	public static EventManager<PlayerItemUseEvent> eventPlayerItemUse = new EventManager<>();
	public static EventManager<PlayerUpdateEvent>  eventPlayerUpdate  = new EventManager<>();
	public static EventManager<MountMoveEvent>     eventMountMove     = new EventManager<>();
	public static EventManager<PlayerTickEvent>    eventPlayerTick    = new EventManager<>();
	public static EventManager<PlayerFallEvent>    eventPlayerFall    = new EventManager<>();

}
