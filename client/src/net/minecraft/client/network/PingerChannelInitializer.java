package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

/**
 * Пингер очень старых серверов (Beta 1.8 и раньше)
 */
public class PingerChannelInitializer extends ChannelInitializer<Channel> {
	
	static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
	private final ServerAddress serveraddress;
	private final ServerData server;
	
	public PingerChannelInitializer(ServerData server, ServerAddress serveraddress) {
		this.server = server;
		this.serveraddress = serveraddress;
	}
	
	
	protected void initChannel(Channel c) {
		try {
			c.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
		} catch (ChannelException ignored) {}
		
		c.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
			public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
				super.channelActive(p_channelActive_1_);
				ByteBuf bytebuf = Unpooled.buffer();
				
				try {
					bytebuf.writeByte(254);
					bytebuf.writeByte(1);
					bytebuf.writeByte(250);
					char[] achar = "MC|PingHost".toCharArray();
					bytebuf.writeShort(achar.length);
					
					for (char c0 : achar) {
						bytebuf.writeChar(c0);
					}
					
					bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
					bytebuf.writeByte(127);
					achar = serveraddress.getIP().toCharArray();
					bytebuf.writeShort(achar.length);
					
					for (char c1 : achar) {
						bytebuf.writeChar(c1);
					}
					
					bytebuf.writeInt(serveraddress.getPort());
					p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
				} finally {
					bytebuf.release();
				}
			}
			
			protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
				short short1 = buf.readUnsignedByte();
				
				if (short1 == 255) {
					String s = new String(buf.readBytes(buf.readShort() * 2).array(), Charsets.UTF_16BE);
					String[] astring = Iterables.toArray(PING_RESPONSE_SPLITTER.split(s), String.class);
					
					if ("\u00a71".equals(astring[0])) {
						int i = MathHelper.parseIntWithDefault(astring[1], 0);
						String s1 = astring[2];
						String s2 = astring[3];
						int j = MathHelper.parseIntWithDefault(astring[4], -1);
						int k = MathHelper.parseIntWithDefault(astring[5], -1);
						server.version = -1;
						server.gameVersion = s1;
						server.serverMOTD = s2;
						server.populationInfo = EnumChatFormatting.GRAY + "" + j + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + k;
					}
				}
				
				ctx.close();
			}
			
			public void exceptionCaught(ChannelHandlerContext context, Throwable ex) {
				context.close();
			}
		});
	}
	
}
