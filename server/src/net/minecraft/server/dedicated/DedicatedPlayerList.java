package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import net.minecraft.logging.Log;
import net.minecraft.server.management.ServerConfigurationManager;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DedicatedPlayerList extends ServerConfigurationManager {

	public DedicatedPlayerList(DedicatedServer server) {
		super(server);
		this.setViewDistance(server.getIntProperty("view-distance", 10));
		this.maxPlayers = server.getIntProperty("max-players", 20);
		this.setWhiteListEnabled(server.getBooleanProperty("white-list", false));

		if (!server.isSinglePlayer()) {
			this.getBannedPlayers().setLanServer(true);
			this.getBannedIPs().setLanServer(true);
		}

		this.saveUserBanList();
		this.saveIpBanList();
		this.readWhiteList();

	}

	public void setWhiteListEnabled(boolean whitelistEnabled) {
		super.setWhiteListEnabled(whitelistEnabled);
		this.getServerInstance().setProperty("white-list", whitelistEnabled);
		this.getServerInstance().saveProperties();
	}

	@Override
	public void removePlayerFromWhitelist(String nick) {
		super.removePlayerFromWhitelist(nick);
		this.saveWhiteList();
	}

	@Override
	public void addWhitelistedPlayer(String nick) {
		super.addWhitelistedPlayer(nick);
		this.saveWhiteList();
	}

	public void loadWhiteList() {
		this.readWhiteList();
	}

	private void saveIpBanList() {
		try {
			getBannedIPs().writeChanges();
		} catch (IOException ioexception) {
			Log.MAIN.warn("Can't save ban-ip list", ioexception);
		}
	}

	private void saveUserBanList() {
		try {
			getBannedPlayers().writeChanges();
		} catch (IOException ioexception) {
			Log.MAIN.warn("Can't save ban list", ioexception);
		}
	}

	private void readWhiteList() {
		try {
			getWhitelistedPlayers().read();
		} catch (Exception exception) {
			Log.MAIN.warn("Can't load whitelist", exception);
		}
	}

	private void saveWhiteList() {
		try {
			this.getWhitelistedPlayers().save();
		} catch (Exception exception) {
			Log.MAIN.warn("Can't save whitelist", exception);
		}
	}

	public boolean canJoin(String nickname) {
		return !this.isWhiteListEnabled() || this.getWhitelistedPlayers().contains(nickname);
	}

	public DedicatedServer getServerInstance() {
		return (DedicatedServer) super.getServerInstance();
	}

}
