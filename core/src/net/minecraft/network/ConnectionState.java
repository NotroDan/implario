package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.logging.Log;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.LogManager;

import java.util.Map;

public enum ConnectionState {
	HANDSHAKING(-1) {
		{
			this.registerPacket(true, C00Handshake.class);
		}
	},
	PLAY(0) {
		{
			this.registerPacket(false, S00PacketKeepAlive.class);
			this.registerPacket(false, S01PacketJoinGame.class);
			this.registerPacket(false, S02PacketChat.class);
			this.registerPacket(false, S03PacketTimeUpdate.class);
			this.registerPacket(false, S04PacketEntityEquipment.class);
			this.registerPacket(false, S05PacketSpawnPosition.class);
			this.registerPacket(false, S06PacketUpdateHealth.class);
			this.registerPacket(false, S07PacketRespawn.class);
			this.registerPacket(false, S08PacketPlayerPosLook.class);
			this.registerPacket(false, S09PacketHeldItemChange.class);
			this.registerPacket(false, S0APacketUseBed.class);
			this.registerPacket(false, S0BPacketAnimation.class);
			this.registerPacket(false, S0CPacketSpawnPlayer.class);
			this.registerPacket(false, S0DPacketCollectItem.class);
			this.registerPacket(false, S0EPacketSpawnObject.class);
			this.registerPacket(false, S0FPacketSpawnMob.class);
			this.registerPacket(false, S10PacketSpawnPainting.class);
			this.registerPacket(false, S11PacketSpawnExperienceOrb.class);
			this.registerPacket(false, S12PacketEntityVelocity.class);
			this.registerPacket(false, S13PacketDestroyEntities.class);
			this.registerPacket(false, S14PacketEntity.class);
			this.registerPacket(false, S14PacketEntity.S15PacketEntityRelMove.class);
			this.registerPacket(false, S14PacketEntity.S16PacketEntityLook.class);
			this.registerPacket(false, S14PacketEntity.S17PacketEntityLookMove.class);
			this.registerPacket(false, S18PacketEntityTeleport.class);
			this.registerPacket(false, S19PacketEntityHeadLook.class);
			this.registerPacket(false, S19PacketEntityStatus.class);
			this.registerPacket(false, S1BPacketEntityAttach.class); // ToDo: Кастомные пакеты
			this.registerPacket(false, S1CPacketEntityMetadata.class);
			this.registerPacket(false, S1DPacketEntityEffect.class);
			this.registerPacket(false, S1EPacketRemoveEntityEffect.class);
			this.registerPacket(false, S1FPacketSetExperience.class);
			this.registerPacket(false, S20PacketEntityProperties.class);
			this.registerPacket(false, S21PacketChunkData.class);
			this.registerPacket(false, S22PacketMultiBlockChange.class);
			this.registerPacket(false, S23PacketBlockChange.class);
			this.registerPacket(false, S24PacketBlockAction.class);
			this.registerPacket(false, S25PacketBlockBreakAnim.class);
			this.registerPacket(false, S26PacketMapChunkBulk.class);
			this.registerPacket(false, S27PacketExplosion.class);
			this.registerPacket(false, S28PacketEffect.class);
			this.registerPacket(false, S29PacketSoundEffect.class);
			this.registerPacket(false, S2APacketParticles.class);
			this.registerPacket(false, S2BPacketChangeGameState.class);
			this.registerPacket(false, S2CPacketSpawnGlobalEntity.class);
			this.registerPacket(false, S2DPacketOpenWindow.class);
			this.registerPacket(false, S2EPacketCloseWindow.class);
			this.registerPacket(false, S2FPacketSetSlot.class);
			this.registerPacket(false, S30PacketWindowItems.class);
			this.registerPacket(false, S31PacketWindowProperty.class);
			this.registerPacket(false, S32PacketConfirmTransaction.class);
			this.registerPacket(false, S33PacketUpdateSign.class);
			this.registerPacket(false, S34PacketMaps.class);
			this.registerPacket(false, S35PacketUpdateTileEntity.class);
			this.registerPacket(false, S36PacketSignEditorOpen.class);
			this.registerPacket(false, S37PacketStatistics.class);
			this.registerPacket(false, S38PacketPlayerListItem.class);
			this.registerPacket(false, S39PacketPlayerAbilities.class);
			this.registerPacket(false, S3APacketTabComplete.class);
			this.registerPacket(false, S3BPacketScoreboardObjective.class);
			this.registerPacket(false, S3CPacketUpdateScore.class);
			this.registerPacket(false, S3DPacketDisplayScoreboard.class);
			this.registerPacket(false, S3EPacketTeams.class);
			this.registerPacket(false, S3FPacketCustomPayload.class);
			this.registerPacket(false, S40PacketDisconnect.class);
			this.registerPacket(false, S41PacketServerDifficulty.class);
			this.registerPacket(false, S42PacketCombatEvent.class);
			this.registerPacket(false, S43PacketCamera.class);
			this.registerPacket(false, S44PacketWorldBorder.class);
			this.registerPacket(false, S45PacketTitle.class);
			this.registerPacket(false, S46PacketSetCompressionLevel.class);
			this.registerPacket(false, S47PacketPlayerListHeaderFooter.class);
			this.registerPacket(false, S48PacketResourcePackSend.class);
			this.registerPacket(false, S49PacketUpdateEntityNBT.class);
			this.registerPacket(true, C00PacketKeepAlive.class);
			this.registerPacket(true, C01PacketChatMessage.class);
			this.registerPacket(true, C02PacketUseEntity.class);
			this.registerPacket(true, C03PacketPlayer.class);
			this.registerPacket(true, C03PacketPlayer.C04PacketPlayerPosition.class);
			this.registerPacket(true, C03PacketPlayer.C05PacketPlayerLook.class);
			this.registerPacket(true, C03PacketPlayer.C06PacketPlayerPosLook.class);
			this.registerPacket(true, C07PacketPlayerDigging.class);
			this.registerPacket(true, C08PacketPlayerBlockPlacement.class);
			this.registerPacket(true, C09PacketHeldItemChange.class);
			this.registerPacket(true, C0APacketAnimation.class);
			this.registerPacket(true, C0BPacketEntityAction.class);
			this.registerPacket(true, C0CPacketInput.class);
			this.registerPacket(true, C0DPacketCloseWindow.class);
			this.registerPacket(true, C0EPacketClickWindow.class);
			this.registerPacket(true, C0FPacketConfirmTransaction.class);
			this.registerPacket(true, C10PacketCreativeInventoryAction.class);
			this.registerPacket(true, C11PacketEnchantItem.class);
			this.registerPacket(true, C12PacketUpdateSign.class);
			this.registerPacket(true, C13PacketPlayerAbilities.class);
			this.registerPacket(true, C14PacketTabComplete.class);
			this.registerPacket(true, C15PacketClientSettings.class);
			this.registerPacket(true, C16PacketClientStatus.class);
			this.registerPacket(true, C17PacketCustomPayload.class);
			this.registerPacket(true, C18PacketSpectate.class);
			this.registerPacket(true, C19PacketResourcePackStatus.class);
		}
	},
	STATUS(1) {
		{
			this.registerPacket(true, C00PacketServerQuery.class);
			this.registerPacket(false, S00PacketServerInfo.class);
			this.registerPacket(true, C01PacketPing.class);
			this.registerPacket(false, S01PacketPong.class);
		}
	},
	LOGIN(2) {
		{
			this.registerPacket(false, S00PacketDisconnect.class);
			this.registerPacket(false, S01PacketEncryptionRequest.class);
			this.registerPacket(false, S02PacketLoginSuccess.class);
			this.registerPacket(false, S03PacketEnableCompression.class);
			this.registerPacket(true, C00PacketLoginStart.class);
			this.registerPacket(true, C01PacketEncryptionResponse.class);
		}
	};

	private static final Map<Class<? extends Packet>, ConnectionState> STATES_BY_CLASS = Maps.newHashMap();
	private static boolean initialized = false;

	@Getter
	private final int id;
	private final BiMap<Integer, Class<? extends Packet>> clientPackets = HashBiMap.create();
	private final BiMap<Integer, Class<? extends Packet>> serverPackets = HashBiMap.create();

	ConnectionState(int protocolId) {
		this.id = protocolId;
	}

	public void registerPacket(boolean isClientPacket, Class<? extends Packet> packetClass) {
		BiMap<Integer, Class<? extends Packet>> map = isClientPacket ? clientPackets : serverPackets;

		if (map.containsValue(packetClass)) {
			Integer existingId = map.inverse().get(packetClass);
			String msg = (isClientPacket ? "Client-side" : "Server-side") + " packet " + packetClass + " is already known to ID " + existingId;
			Log.MAIN.error(msg);
			throw new IllegalArgumentException(msg);

		}
		if(initialized) STATES_BY_CLASS.put(packetClass, this);
		map.put(map.size(), packetClass);
	}

	public Integer getPacketId(boolean isClientPacket, Packet packet) {
		return (isClientPacket ? clientPackets : serverPackets).inverse().get(packet.getClass());
	}

	public Packet getPacket(boolean isClientPacket, int packetId) throws IllegalAccessException, InstantiationException {
		Class<? extends Packet> packetClass = (isClientPacket ? clientPackets : serverPackets).get(packetId);
		return packetClass == null ? null : packetClass.newInstance();
	}

	public static ConnectionState getById(int stateId) {
		int id = stateId + 1;
		return id < 0 || values().length <= id ? null : values()[id];
	}

	public static ConnectionState getFromPacket(Packet packetIn) {
		return STATES_BY_CLASS.get(packetIn.getClass());
	}

	private static void init(ConnectionState state, boolean isClient) {
		for (Class<? extends Packet> packet : (isClient ? state.clientPackets : state.serverPackets).values()) {
			if (STATES_BY_CLASS.containsKey(packet) && STATES_BY_CLASS.get(packet) != state) {
				throw new Error("Packet " + packet + " is already assigned to protocol " + STATES_BY_CLASS.get(packet) + " - can\'t reassign to " + state);
			}

			// Контрольная проверка пакета - попытка создания экземпляра.
			try {
				packet.newInstance();
			} catch (Throwable ex) {
				throw new Error("Packet " + packet + " fails instantiation checks! " + packet);
			}

			STATES_BY_CLASS.put(packet, state);
		}
	}
	static {
		for (ConnectionState state : values()) {
			init(state, true);
			init(state, false);
		}
		initialized = true;
	}
}
