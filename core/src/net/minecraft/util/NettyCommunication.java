package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.logging.Log;
import net.minecraft.network.protocol.IProtocol;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.List;

public class NettyCommunication {


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

		private static final Log logger = Log.MAIN;
		private final boolean isClientSided;

		public Encoder(boolean isClientSided) {
			this.isClientSided = isClientSided;
		}

		protected void encode(ChannelHandlerContext chc, Packet packet, ByteBuf buf) throws Exception {
			int id = chc.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketID(isClientSided, packet);

			if (id == -1) throw new IOException("Can't serialize unregistered packet");

			PacketBuffer packetbuffer = new PacketBuffer(buf);
			packetbuffer.writeVarIntToBuffer(id);

			try {
				packet.writePacketData(packetbuffer);
			} catch (Throwable throwable) {
				logger.error("Error on write packet", throwable);
			}
		}

	}

	public static class Splitter extends ByteToMessageDecoder {

		protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) {
			buf.markReaderIndex();
			byte[] varint = new byte[3];

			for (int i = 0; i < varint.length; ++i) {
				if (!buf.isReadable()) {
					buf.resetReaderIndex();
					return;
				}

				varint[i] = buf.readByte();

				// Все отдельные байты в VarInt кроме последнего интерпретируются как отрицательные числа
				if (varint[i] >= 0) {
					PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(varint));

					try {
						int packetLength = packetbuffer.readVarIntFromBuffer();

						if (buf.readableBytes() >= packetLength) {
							list.add(buf.readBytes(packetLength));
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

		private final boolean isClientSide;

		public Decoder(boolean isClientSide) {
			this.isClientSide = isClientSide;
		}

		protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> buffer) throws Exception {
			if (buf.readableBytes() != 0) {
				PacketBuffer packetbuffer = new PacketBuffer(buf);
				int i = packetbuffer.readVarIntFromBuffer();
				IProtocol state = ctx.channel().attr(NetworkManager.attrKeyConnectionState).get();
				Packet packet = state.getPacket(isClientSide, i);

				if (packet == null) {
					throw new IOException("Bad packet id " + i);
				}
				packet.readPacketData(packetbuffer);

				if (packetbuffer.readableBytes() > 0) {
					throw new IOException("Packet " + state + "/" + i + " (" + packet.getClass().getSimpleName() + ") was " +
							"larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
				}
				buffer.add(packet);
			}
		}

	}

}
