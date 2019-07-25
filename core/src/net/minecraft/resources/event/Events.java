package net.minecraft.resources.event;

import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.resources.event.events.player.*;

public enum Events {
	;

	public static EventManager<MountMoveEvent> eventMountMove = new EventManager<>();
	public static EventManager<EntityDeathEvent> eventEntityDeath = new EventManager<>();
	public static EventManager<ProjectileHitEvent> eventProjectileHit = new EventManager<>();
	public static EventManager<BlockDropEvent> eventBlockDrop = new EventManager<>();

	public static EventManager<PlayerMoveEvent> eventPlayerMove = new EventManager<>();
	public static EventManager<PlayerUpdateEvent> eventPlayerUpdate = new EventManager<>();
	public static EventManager<PlayerTickEvent> eventPlayerTick = new EventManager<>();
	public static EventManager<PlayerFallEvent> eventPlayerFall = new EventManager<>();
	public static EventManager<PlayerLeaveDisconnect> eventPlayerDisconnect = new EventManager<>();
	public static EventManager<PlayerJumpEvent> eventPlayerJump = new EventManager<>();
	public static EventManager<PlayerItemDropEvent> eventPlayerItemDrop = new EventManager<>();
	public static EventManager<PlayerDeathEvent> eventPlayerDeath = new EventManager<>();
	public static EventManager<PlayerSleepEvent> eventPlayerSleep = new EventManager<>();
	public static EventManager<PlayerActionEvent> eventPlayerAction = new EventManager<>();
	public static EventManager<TrackerUpdateEvent> eventTrackerUpdate = new EventManager<>();
	public static EventManager<PlayerEnderPearlEvent> eventPlayerEnderPearl = new EventManager<>();
	public static EventManager<PlayerInteractEvent> eventPlayerInteract = new EventManager<>();


}
