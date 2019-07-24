package net.minecraft.network.bot;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraft.util.IChatComponent;

import javax.crypto.SecretKey;
import java.net.SocketAddress;

public class BotNetworkManager extends NetworkManager {

	private final BotHandlerPlayClient handler;

	public BotNetworkManager(EnumPacketDirection packetDirection) {
		super(packetDirection);
		handler = new BotHandlerPlayClient();
	}

	@Override
	public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
		super.channelActive(p_channelActive_1_);
	}

	@Override
	public void setConnectionState(EnumConnectionState newState) {
		super.setConnectionState(newState);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
		super.exceptionCaught(ctx, t);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, Packet packet) throws Exception {
		super.channelRead0(context, packet);
	}

	@Override
	public void sendPacket(Packet packet) {
		packet.processPacket(handler);
	}

	@Override
	public void processReceivedPackets() {
		super.processReceivedPackets();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return super.getRemoteAddress();
	}

	@Override
	public void closeChannel(IChatComponent message) {
		super.closeChannel(message);
	}

	@Override
	public boolean isLocalChannel() {
		return super.isLocalChannel();
	}

	@Override
	public void enableEncryption(SecretKey key) {
		super.enableEncryption(key);
	}

	@Override
	public boolean getIsencrypted() {
		return super.getIsencrypted();
	}

	@Override
	public boolean isChannelOpen() {
		return super.isChannelOpen();
	}

	@Override
	public boolean hasNoChannel() {
		return super.hasNoChannel();
	}

	@Override
	public INetHandler getNetHandler() {
		return super.getNetHandler();
	}

	@Override
	public void setNetHandler(INetHandler handler) {

	}

	@Override
	public void disableAutoRead() {
		//		super.disableAutoRead();
	}

	@Override
	public void setCompressionTreshold(int treshold) {
		//		super.setCompressionTreshold(treshold);
	}

	@Override
	public void checkDisconnected() {
		//todo
	}


}
