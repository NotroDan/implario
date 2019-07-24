package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.Logger;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.List;

public class MessageSerialization {


	public static class Prepender extends MessageToByteEncoder<ByteBuf> {

		protected void encode(ChannelHandlerContext chc, ByteBuf from, ByteBuf to) {
			int i = from.readableBytes();
			int j = PacketBuffer.getVarIntSize(i);
			if (j > 3) throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
			PacketBuffer packetbuffer = new PacketBuffer(to);
			packetbuffer.ensureWritable(j + i);
			packetbuffer.writeVarIntToBuffer(i);
			packetbuffer.writeBytes(from, from.readerIndex(), i);
		}

	}

	public static class Encoder extends MessageToByteEncoder<Packet> {

		private static final Logger logger = Logger.getInstance();
		private final EnumPacketDirection direction;

		public Encoder(EnumPacketDirection direction) {
			this.direction = direction;
		}

		protected void encode(ChannelHandlerContext chc, Packet packet, ByteBuf buf) throws Exception {
			Integer integer = chc.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, packet);
			if (integer == null) throw new IOException("Can\'t serialize unregistered packet");

			PacketBuffer packetbuffer = new PacketBuffer(buf);
			packetbuffer.writeVarIntToBuffer(integer);

			try {
				packet.writePacketData(packetbuffer);
			} catch (Throwable throwable) {
				logger.error((Object) throwable);
			}
		}

	}

	public static class Splitter extends ByteToMessageDecoder {

		protected void decode(ChannelHandlerContext chc, ByteBuf buf, List<Object> list) {
			buf.markReaderIndex();
			byte[] abyte = new byte[3];

			for (int i = 0; i < abyte.length; ++i) {
				if (!buf.isReadable()) {
					buf.resetReaderIndex();
					return;
				}

				abyte[i] = buf.readByte();

				if (abyte[i] >= 0) {
					PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(abyte));

					try {
						int j = packetbuffer.readVarIntFromBuffer();

						if (buf.readableBytes() >= j) {
							list.add(buf.readBytes(j));
							return;
						}

						buf.resetReaderIndex();
					} finally {
						packetbuffer.release();
					}

					return;
				}
			}

			throw new CorruptedFrameException("length wider than 21-bit");
		}

	}

	public static class Decoder extends ByteToMessageDecoder {

		private static final Logger logger = Logger.getInstance();
		private final EnumPacketDirection direction;

		public Decoder(EnumPacketDirection direction) {
			this.direction = direction;
		}

		protected void decode(ChannelHandlerContext chc, ByteBuf buf, List<Object> list) throws Exception {
			if (buf.readableBytes() != 0) {
				PacketBuffer packetbuffer = new PacketBuffer(buf);
				int i = packetbuffer.readVarIntFromBuffer();
				Packet packet = chc.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, i);

				if (packet == null) {
					throw new IOException("Bad packet id " + i);
				}
				packet.readPacketData(packetbuffer);

				if (packetbuffer.readableBytes() > 0) {
					throw new IOException("Packet " + chc.channel().attr(
							NetworkManager.attrKeyConnectionState).get().getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
				}
				list.add(packet);
			}
		}

	}

}
