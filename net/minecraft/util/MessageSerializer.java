package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.client.Logger;
import net.minecraft.network.*;

import java.io.IOException;

public class MessageSerializer extends MessageToByteEncoder<Packet> {

	private static final Logger logger = Logger.getInstance();
	private final EnumPacketDirection direction;

	public MessageSerializer(EnumPacketDirection direction) {
		this.direction = direction;
	}

	protected void encode(ChannelHandlerContext p_encode_1_, Packet p_encode_2_, ByteBuf p_encode_3_) throws Exception {
		Integer integer = p_encode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, p_encode_2_);

		if (integer == null) {
			throw new IOException("Can\'t serialize unregistered packet");
		} else {
			PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
			packetbuffer.writeVarIntToBuffer(integer);

			try {
				p_encode_2_.writePacketData(packetbuffer);
			} catch (Throwable throwable) {
				logger.error((Object) throwable);
			}
		}
	}

}
