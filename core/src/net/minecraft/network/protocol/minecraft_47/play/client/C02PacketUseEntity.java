package net.minecraft.network.protocol.minecraft_47.play.client;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayServer;
import net.minecraft.util.Vec3d;
import net.minecraft.world.World;

public class C02PacketUseEntity implements Packet<INetHandlerPlayServer> {

	private int entityId;
	private C02PacketUseEntity.Action action;
	private Vec3d hitVec;

	public C02PacketUseEntity() {
	}

	public C02PacketUseEntity(Entity entity, C02PacketUseEntity.Action action) {
		this.entityId = entity.getEntityId();
		this.action = action;
	}

	public C02PacketUseEntity(Entity entity, Vec3d hitVec) {
		this(entity, C02PacketUseEntity.Action.INTERACT_AT);
		this.hitVec = hitVec;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readVarIntFromBuffer();
		this.action = (C02PacketUseEntity.Action) buf.readEnumValue(C02PacketUseEntity.Action.class);

		if (this.action == C02PacketUseEntity.Action.INTERACT_AT) {
			this.hitVec = new Vec3d((double) buf.readFloat(), (double) buf.readFloat(), (double) buf.readFloat());
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityId);
		buf.writeEnumValue(this.action);

		if (this.action == C02PacketUseEntity.Action.INTERACT_AT) {
			buf.writeFloat((float) this.hitVec.xCoord);
			buf.writeFloat((float) this.hitVec.yCoord);
			buf.writeFloat((float) this.hitVec.zCoord);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayServer handler) {
		handler.processUseEntity(this);
	}

	public Entity getEntityFromWorld(World worldIn) {
		return worldIn.getEntityByID(this.entityId);
	}

	public C02PacketUseEntity.Action getAction() {
		return this.action;
	}

	public Vec3d getHitVec() {
		return this.hitVec;
	}

	public static enum Action {
		INTERACT,
		ATTACK,
		INTERACT_AT;
	}

}
