package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.Logger;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.*;
import net.minecraft.util.chat.ChatComponentText;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NetworkSystem {

	private static final Logger logger = Logger.getInstance();
	public static final LazyLoadBase<NioEventLoopGroup> eventLoops = new LazyLoadBase<NioEventLoopGroup>() {
		protected NioEventLoopGroup load() {
			return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
		}
	};
	public static final LazyLoadBase<EpollEventLoopGroup> field_181141_b = new LazyLoadBase<EpollEventLoopGroup>() {
		protected EpollEventLoopGroup load() {
			return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
		}
	};
	public static final LazyLoadBase<LocalEventLoopGroup> SERVER_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
		protected LocalEventLoopGroup load() {
			return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
		}
	};

	/**
	 * Reference to the MinecraftServer object.
	 */
	private final MinecraftServer mcServer;

	/**
	 * True if this NetworkSystem has never had his endpoints terminated
	 */
	public volatile boolean isAlive;
	private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
	private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

	public NetworkSystem(MinecraftServer server) {
		this.mcServer = server;
		this.isAlive = true;
	}

	/**
	 * Adds a channel that listens on publicly accessible network ports
	 */
	public void addLanEndpoint(InetAddress address, int port) throws IOException {
		synchronized (this.endpoints) {
			Class<? extends ServerSocketChannel> oclass;
			LazyLoadBase<? extends EventLoopGroup> lazyloadbase;

			if (Epoll.isAvailable() && this.mcServer.useEpoll()) {
				oclass = EpollServerSocketChannel.class;
				lazyloadbase = field_181141_b;
				logger.info("Тип системы подключений - Epoll");
			} else {
				oclass = NioServerSocketChannel.class;
				lazyloadbase = eventLoops;
				logger.info("Тип системы подключений - NIO");
			}

			this.endpoints.add(new ServerBootstrap().channel(oclass).childHandler(new ChannelInitializer<Channel>() {
				protected void initChannel(Channel channel) throws Exception {
					try {
						channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
					} catch (ChannelException ignored) {
					}

					channel.pipeline()
							.addLast("timeout", new ReadTimeoutHandler(30))
							.addLast("legacy_query", new PingResponseHandler(NetworkSystem.this))
							.addLast("splitter", new MessageSerialization.Splitter())
							.addLast("decoder", new MessageSerialization.Decoder(EnumPacketDirection.SERVERBOUND))
							.addLast("prepender", new MessageSerialization.Prepender())
							.addLast("encoder", new MessageSerialization.Encoder(EnumPacketDirection.CLIENTBOUND));

					NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
					NetworkSystem.this.networkManagers.add(networkmanager);
					channel.pipeline().addLast("packet_handler", networkmanager);
					networkmanager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, networkmanager));
				}
			}).group(lazyloadbase.getValue()).localAddress(address, port).bind().syncUninterruptibly());
		}
	}

	/**
	 * Adds a channel that listens locally
	 */
	public SocketAddress addLocalEndpoint() {
		ChannelFuture channelfuture;

		synchronized (this.endpoints) {
			channelfuture = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
				protected void initChannel(Channel p_initChannel_1_) throws Exception {
					NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
					networkmanager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, networkmanager));
					NetworkSystem.this.networkManagers.add(networkmanager);
					p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
				}
			}).group(eventLoops.getValue()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
			this.endpoints.add(channelfuture);
		}

		return channelfuture.channel().localAddress();
	}

	/**
	 * Shuts down all open endpoints (with immediate effect?)
	 */
	public void terminateEndpoints() {
		this.isAlive = false;

		for (ChannelFuture channelfuture : this.endpoints) {
			try {
				channelfuture.channel().close().sync();
			} catch (InterruptedException var4) {
				logger.error("Interrupted whilst closing channel");
			}
		}
	}

	/**
	 * Will try to process the packets received by each NetworkManager, gracefully manage processing failures and cleans
	 * up dead connections
	 */
	public void networkTick() {
		synchronized (this.networkManagers) {
			Iterator<NetworkManager> iterator = this.networkManagers.iterator();

			while (iterator.hasNext()) {
				final NetworkManager networkmanager = iterator.next();

				if (networkmanager.hasNoChannel()) continue;
				if (!networkmanager.isChannelOpen()) {
					iterator.remove();
					networkmanager.checkDisconnected();
					continue;
				}
				try {
					networkmanager.processReceivedPackets();
				} catch (Exception exception) {
					if (networkmanager.isLocalChannel()) {
						CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
						CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
						crashreportcategory.addCrashSectionCallable("Connection", networkmanager::toString);
						throw new ReportedException(crashreport);
					}

					logger.warn("Failed to handle packet for " + networkmanager.getRemoteAddress(), exception);
					final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");
					networkmanager.sendPacket(new S40PacketDisconnect(chatcomponenttext), p_operationComplete_1_ -> networkmanager.closeChannel(chatcomponenttext));
					networkmanager.disableAutoRead();
				}
			}
		}
	}

	public MinecraftServer getServer() {
		return this.mcServer;
	}

}
