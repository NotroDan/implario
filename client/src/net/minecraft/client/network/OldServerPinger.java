package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.minecraft.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OldServerPinger {

	private static final Logger logger = Logger.getInstance();
	private final List<NetworkManager> pingDestinations = Collections.synchronizedList(new ArrayList<>());

	public void ping(final ServerData server) throws UnknownHostException {
		ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
		final NetworkManager networkmanager = NetworkManager.func_181124_a(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
		this.pingDestinations.add(networkmanager);
		server.serverMOTD = "Pinging...";
		server.pingToServer = -1L;
		server.playerList = null;
		networkmanager.setNetHandler(new PingerNetHandlerStatusClient(networkmanager, server));

		try {
			networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
			networkmanager.sendPacket(new C00PacketServerQuery());
		} catch (Throwable ex) {
			logger.error(ex);
		}
	}

	private void tryCompatibilityPing(final ServerData server) {
		final ServerAddress serveraddress = ServerAddress.func_78860_a(server.serverIP);
		ChannelInitializer<Channel> initializer = new PingerChannelInitializer(server, serveraddress);
		EventLoopGroup group = NetworkManager.CLIENT_NIO_EVENTLOOP.getValue();
		new Bootstrap().group(group).handler(initializer).channel(NioSocketChannel.class)
				.connect(serveraddress.getIP(), serveraddress.getPort());
	}

	public void pingPendingNetworks() {
		synchronized (this.pingDestinations) {
			Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

			while (iterator.hasNext()) {
				NetworkManager networkmanager = iterator.next();
				if (networkmanager.isChannelOpen()) networkmanager.processReceivedPackets();
				else {
					iterator.remove();
					networkmanager.checkDisconnected();
				}
			}
		}
	}

	public void clearPendingNetworks() {
		synchronized (this.pingDestinations) {
			Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

			while (iterator.hasNext()) {
				NetworkManager networkmanager = iterator.next();
				if (networkmanager.isChannelOpen()) {
					iterator.remove();
					networkmanager.closeChannel(new ChatComponentText("Cancelled"));
				}
			}
		}
	}

	private class PingerNetHandlerStatusClient implements INetHandlerStatusClient {

		private final NetworkManager networkmanager;
		private final ServerData server;
		private boolean completed;
		private boolean success;
		private long startTime;

		public PingerNetHandlerStatusClient(NetworkManager networkmanager, ServerData server) {
			this.networkmanager = networkmanager;
			this.server = server;
			completed = false;
			success = false;
			startTime = 0L;
		}

		public void handleServerInfo(S00PacketServerInfo packetIn) {
			if (this.success) {
				networkmanager.closeChannel(new ChatComponentText("Received unrequested status"));
				return;
			}
			this.success = true;
			ServerStatusResponse res = packetIn.getResponse();

			server.serverMOTD = res.getServerDescription() != null ?
					res.getServerDescription().getFormattedText() : "";

			if (res.getProtocolVersionInfo() != null) {
				server.gameVersion = res.getProtocolVersionInfo().getName();
				server.version = res.getProtocolVersionInfo().getProtocol();
			} else {
				server.gameVersion = "Old";
				server.version = 0;
			}

			if (res.getPlayerCountData() == null) server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
			else {
				server.populationInfo = EnumChatFormatting.GRAY + "" + res.getPlayerCountData().getOnlinePlayerCount() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + res.getPlayerCountData().getMaxPlayers();

				if (ArrayUtils.isNotEmpty(res.getPlayerCountData().getPlayers())) {
					StringBuilder stringbuilder = new StringBuilder();

					for (GameProfile gameprofile : res.getPlayerCountData().getPlayers()) {
						if (stringbuilder.length() > 0) stringbuilder.append("\n");

						stringbuilder.append(gameprofile.getName());
					}

					if (res.getPlayerCountData().getPlayers().length < res.getPlayerCountData().getOnlinePlayerCount()) {
						if (stringbuilder.length() > 0) stringbuilder.append("\n");
						int unshown = res.getPlayerCountData().getOnlinePlayerCount() - res.getPlayerCountData().getPlayers().length;
						stringbuilder.append("... ещё ").append(unshown).append(" ...");
					}

					server.playerList = stringbuilder.toString();
				}
			}

			if (res.getFavicon() == null) server.setBase64EncodedIconData(null);
			else {
				String s = res.getFavicon();

				if (s.startsWith("data:image/png;base64,"))
					server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
				else OldServerPinger.logger.error("Invalid server icon (unknown format)");
			}

			this.startTime = Minecraft.getSystemTime();
			networkmanager.sendPacket(new C01PacketPing(this.startTime));
			this.completed = true;
		}

		public void handlePong(S01PacketPong packetIn) {
			long i = this.startTime;
			long j = Minecraft.getSystemTime();
			server.pingToServer = j - i;
			networkmanager.closeChannel(new ChatComponentText("Finished"));
		}

		public void onDisconnect(IChatComponent reason) {
			if (!this.completed) {
				OldServerPinger.logger.error("Can\'t ping " + server.serverIP + ": " + reason.getUnformattedText());
				server.serverMOTD = EnumChatFormatting.DARK_RED + "Не удалось получить данные о сервере.";
				server.populationInfo = "§сОффлайн";
				OldServerPinger.this.tryCompatibilityPing(server);
			}
		}

	}

}
