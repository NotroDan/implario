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
		this.loadOpsList();
		this.readWhiteList();
		this.saveOpsList();

	}

	public void setWhiteListEnabled(boolean whitelistEnabled) {
		super.setWhiteListEnabled(whitelistEnabled);
		this.getServerInstance().setProperty("white-list", whitelistEnabled);
		this.getServerInstance().saveProperties();
	}

	public void addOp(GameProfile profile) {
		super.addOp(profile);
		this.saveOpsList();
	}

	public void removeOp(GameProfile profile) {
		super.removeOp(profile);
		this.saveOpsList();
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

	private void loadOpsList() {
		try {
			this.getOppedPlayers().readSavedFile();
		} catch (Exception exception) {
			Log.MAIN.warn("Не удалось прогрузить список операторов: ");
			Log.MAIN.exception(exception);
		}
	}

	private void saveOpsList() {
		try {
			this.getOppedPlayers().writeChanges();
		} catch (Exception exception) {
			Log.MAIN.warn("Не удалось сохранить список операторов: ");
			Log.MAIN.exception(exception);
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

	public boolean canJoin(GameProfile profile) {
		return !this.isWhiteListEnabled() || this.canSendCommands(profile) || this.getWhitelistedPlayers().contains(profile.getName());
	}

	public DedicatedServer getServerInstance() {
		return (DedicatedServer) super.getServerInstance();
	}

	public boolean func_183023_f(GameProfile p_183023_1_) {
		return this.getOppedPlayers().func_183026_b(p_183023_1_);
	}

}
