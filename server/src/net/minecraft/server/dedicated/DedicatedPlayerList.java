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
			this.getBannedIPs().writeChanges();
		} catch (IOException ioexception) {
			Log.MAIN.warn("Не удалось сохранить список банов по IP: ");
			Log.MAIN.exception(ioexception);
		}
	}

	private void saveUserBanList() {
		try {
			this.getBannedPlayers().writeChanges();
		} catch (IOException ioexception) {
			Log.MAIN.warn("Не удалось сохранить список банов: ");
			Log.MAIN.exception(ioexception);
		}
	}

	private void readWhiteList() {
		try {
			this.getWhitelistedPlayers().read();
		} catch (Exception exception) {
			Log.MAIN.warn("Не удалось прогрузить вайтлист: ");
			Log.MAIN.exception(exception);
		}
	}

	private void saveWhiteList() {
		try {
			this.getWhitelistedPlayers().save();
		} catch (Exception exception) {
			Log.MAIN.warn("Не удалось сохранить вайтлист: ");
			Log.MAIN.exception(exception);
		}
	}

	public boolean canJoin(String nickname) {
		return !this.isWhiteListEnabled() || this.getWhitelistedPlayers().contains(nickname);
	}

	public DedicatedServer getServerInstance() {
		return (DedicatedServer) super.getServerInstance();
	}

}
