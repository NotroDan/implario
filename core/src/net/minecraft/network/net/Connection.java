package net.minecraft.network.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Data
@EqualsAndHashCode (callSuper = false)
public class Connection extends SimpleChannelInboundHandler<Packet> {

	private final int protocolId;

	private final Channel channel;

	public void send(Object packet) {

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
		awaitingPackets.offer(packet);
	}



}
