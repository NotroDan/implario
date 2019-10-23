package net.minecraft.resources.event;

import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.resources.event.events.entity.EntityDeathEvent;
import net.minecraft.resources.event.events.entity.EntityKilledEntityEvent;
import net.minecraft.resources.event.events.player.*;
import net.minecraft.resources.event.events.world.WorldServerInitEvent;
import net.minecraft.resources.event.events.world.WorldTickEvent;

public class ServerEvents {
	public static EventManager<EntityDeathEvent> entityDeath = new EventManager<>();
	public static EventManager<EntityKilledEntityEvent> entityKill = new EventManager<>();
	public static EventManager<ProjectileHitEvent> projectileHit = new EventManager<>();
	public static EventManager<BlockDropEvent> blockDrop = new EventManager<>();

	public static EventManager<PlayerMoveEvent> playerMove = new EventManager<>();
	public static EventManager<PlayerUpdateEvent> playerUpdate = new EventManager<>();
	public static EventManager<PlayerTickEvent> playerTick = new EventManager<>();
	public static EventManager<PlayerFallEvent> playerFall = new EventManager<>();
	public static EventManager<PlayerLeaveDisconnect> playerDisconnect = new EventManager<>();
	public static EventManager<PlayerJumpEvent> playerJump = new EventManager<>();
	public static EventManager<PlayerItemDropEvent> playerItemDrop = new EventManager<>();
	public static EventManager<PlayerDeathEvent> playerDeath = new EventManager<>();
	public static EventManager<PlayerSleepEvent> playerSleep = new EventManager<>();
	public static EventManager<PlayerActionEvent> playerAction = new EventManager<>();
	public static EventManager<TrackerUpdateEvent> trackerUpdate = new EventManager<>();
	public static EventManager<PlayerInteractEvent> playerInteract = new EventManager<>();
	public static EventManager<PlayerRespawnEvent> playerRespawn = new EventManager<>();
	public static EventManager<PlayerTeleportPearlEvent> playerTeleportPearl = new EventManager<>();
	public static EventManager<PlayerMountMoveEvent> playerMountMove = new EventManager<>();
	public static EventManager<ItemInteractForEntityEvent> playerItemInteract = new EventManager<>();
	public static EventManager<PlayerFenceClickedEvent> playerFenceClicked = new EventManager<>();
	public static EventManager<PlayerBlockBreakEvent> playerBlockBreak = new EventManager<>();
	public static EventManager<PlayerBlockPlaceEvent> playerBlockPlace = new EventManager<>();
	public static EventManager<PlayerChatMessageEvent> playerChatMessage = new EventManager<>();
	public static EventManager<PlayerTeleportEvent> playerTeleport = new EventManager<>();
	public static EventManager<PlayerJoinEvent> playerJoin = new EventManager<>();
	public static EventManager<PlayerFoodChangeEvent> playerFoodChange = new EventManager<>();

	public static EventManager<WorldServerInitEvent> worldInit = new EventManager<>();
	public static EventManager<WorldTickEvent> worldTick = new EventManager<>();
}
