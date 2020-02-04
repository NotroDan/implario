package net.minecraft.network.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.util.MinecraftCore;

@Data
@EqualsAndHashCode (callSuper = false)
public class Connection extends SimpleChannelInboundHandler<Instance> {

	private final MinecraftCore core;

	private final int protocolId;

	private final Channel channel;

	public void send(Object packet) {

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Instance instance) throws Exception {
		core.queue(instance);
	}



}
