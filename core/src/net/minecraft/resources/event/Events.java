package net.minecraft.resources.event;

import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.player.*;

public class Events {

	public static EventManager<MountMoveEvent>        eventMountMove        = new EventManager<>();
	public static EventManager<EntityDeathEvent>      eventEntityDeath      = new EventManager<>();

	public static EventManager<PlayerMoveEvent>       eventPlayerMove       = new EventManager<>();
	public static EventManager<PlayerItemUseEvent>    eventPlayerItemUse    = new EventManager<>();
	public static EventManager<PlayerUpdateEvent>     eventPlayerUpdate     = new EventManager<>();
	public static EventManager<PlayerTickEvent>       eventPlayerTick       = new EventManager<>();
	public static EventManager<PlayerFallEvent>       eventPlayerFall       = new EventManager<>();
	public static EventManager<PlayerLeaveDisconnect> eventPlayerDisconnect = new EventManager<>();
	public static EventManager<PlayerJumpEvent>       eventPlayerJump       = new EventManager<>();
	public static EventManager<PlayerItemDropEvent>   eventPlayerItemDrop   = new EventManager<>();
	public static EventManager<PlayerDeathEvent>      eventPlayerDeath      = new EventManager<>();

}
