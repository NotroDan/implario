package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import net.minecraft.entity.player.Player;
import net.minecraft.io.FileRootImpl;
import net.minecraft.logging.Log;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.functional.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class PreYggdrasilConverter {

	private static final Log LOGGER = Log.MAIN;
	public static final File OLD_IPBAN_FILE = new File("banned-ips.txt");
	public static final File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
	public static final File OLD_OPS_FILE = new File("ops.txt");
	public static final File OLD_WHITELIST_FILE = new File("white-list.txt");

	static List<String> readFile(File inFile, Map<String, String[]> read) throws IOException {
		List<String> list = Files.readLines(inFile, Charsets.UTF_8);

		for (String s : list) {
			s = s.trim();

			if (!s.startsWith("#") && s.length() >= 1) {
				String[] astring = s.split("\\|");
				read.put(astring[0].toLowerCase(Locale.ROOT), astring);
			}
		}

		return list;
	}

	private static void lookupNames(MinecraftServer server, Collection<String> names, ProfileLookupCallback callback) {
		String[] astring = Iterators.toArray(Iterators.filter(names.iterator(), s1 -> !StringUtils.isNullOrEmpty(s1)), String.class);

		if (server.isServerInOnlineMode()) server.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, callback);
		else for (String s : astring) {
			UUID uuid = Player.getUUID(new GameProfile(null, s));
			GameProfile gameprofile = new GameProfile(uuid, s);
			callback.onProfileLookupSucceeded(gameprofile);
		}
	}

	public static boolean convertUserBanlist(final MinecraftServer server) throws IOException {
		final UserListBans userlistbans = new UserListBans(ServerConfigurationManager.FILE_PLAYERBANS);

		if (OLD_PLAYERBAN_FILE.exists() && OLD_PLAYERBAN_FILE.isFile()) {
			if (userlistbans.getSaveFile().exists()) {
				try {
					userlistbans.readSavedFile();
				} catch (FileNotFoundException filenotfoundexception) {
					LOGGER.warn("Could not load existing file " + userlistbans.getSaveFile().getName(), filenotfoundexception);
				}
			}

			try {
				final Map<String, String[]> map = Maps.newHashMap();
				readFile(OLD_PLAYERBAN_FILE, map);
				ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
					public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
						server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
						String[] astring = map.get(p_onProfileLookupSucceeded_1_.getName().toLowerCase(Locale.ROOT));

						if (astring == null) {
							PreYggdrasilConverter.LOGGER.warn("Could not convert user banlist entry for " + p_onProfileLookupSucceeded_1_.getName());
							throw new PreYggdrasilConverter.ConversionError("Profile not in the conversionlist");
						}
						Date date = astring.length > 1 ? PreYggdrasilConverter.parseDate(astring[1], null) : null;
						String s = astring.length > 2 ? astring[2] : null;
						Date date1 = astring.length > 3 ? PreYggdrasilConverter.parseDate(astring[3], null) : null;
						String s1 = astring.length > 4 ? astring[4] : null;
						userlistbans.addEntry(new UserListBansEntry(p_onProfileLookupSucceeded_1_, date, s, date1, s1));
					}

					public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
						PreYggdrasilConverter.LOGGER.warn("Could not lookup user banlist entry for " + p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);

						if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
							throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
						}
					}
				};
				lookupNames(server, map.keySet(), profilelookupcallback);
				userlistbans.writeChanges();
				backupConverted(OLD_PLAYERBAN_FILE);
				return true;
			} catch (IOException ioexception) {
				LOGGER.warn("Could not read old user banlist to convert it!", ioexception);
				return false;
			} catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
				LOGGER.error("Conversion failed, please try again later", preyggdrasilconverter$conversionerror);
				return false;
			}
		}
		return true;
	}

	public static boolean convertIpBanlist(MinecraftServer server) throws IOException {
		BanList banlist = new BanList(ServerConfigurationManager.FILE_IPBANS);

		if (OLD_IPBAN_FILE.exists() && OLD_IPBAN_FILE.isFile()) {
			if (banlist.getSaveFile().exists()) {
				try {
					banlist.readSavedFile();
				} catch (FileNotFoundException filenotfoundexception) {
					LOGGER.warn("Could not load existing file " + banlist.getSaveFile().getName(), filenotfoundexception);
				}
			}

			try {
				Map<String, String[]> map = Maps.newHashMap();
				readFile(OLD_IPBAN_FILE, map);

				for (String s : map.keySet()) {
					String[] astring = map.get(s);
					Date date = astring.length > 1 ? parseDate(astring[1], null) : null;
					String s1 = astring.length > 2 ? astring[2] : null;
					Date date1 = astring.length > 3 ? parseDate(astring[3], null) : null;
					String s2 = astring.length > 4 ? astring[4] : null;
					banlist.addEntry(new IPBanEntry(s, date, s1, date1, s2));
				}

				banlist.writeChanges();
				backupConverted(OLD_IPBAN_FILE);
				return true;
			} catch (IOException ioexception) {
				LOGGER.warn("Could not parse old ip banlist to convert it!", ioexception);
				return false;
			}
		}
		return true;
	}

	public static boolean convertOplist(final MinecraftServer server) throws IOException {
		/*final UserListOps userlistops = new UserListOps(ServerConfigurationManager.FILE_OPS);

		if (OLD_OPS_FILE.exists() && OLD_OPS_FILE.isFile()) {
			if (userlistops.getSaveFile().exists()) {
				try {
					userlistops.readSavedFile();
				} catch (FileNotFoundException filenotfoundexception) {
					LOGGER.warn("Could not load existing file " + userlistops.getSaveFile().getName(), filenotfoundexception);
				}
			}

			try {
				//TODO: надо конвертер в игроков
				List<String> list = Files.readLines(OLD_OPS_FILE, Charsets.UTF_8);
				ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
					public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
						server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
						userlistops.addEntry(new UserListOpsEntry(p_onProfileLookupSucceeded_1_, server.getOpPermissionLevel(), false));
					}

					public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
						PreYggdrasilConverter.LOGGER.warn("Could not lookup oplist entry for " + p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);

						if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
							throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
						}
					}
				};
				lookupNames(server, list, profilelookupcallback);
				userlistops.writeChanges();
				backupConverted(OLD_OPS_FILE);
				return true;
			} catch (IOException ioexception) {
				LOGGER.warn("Could not read old oplist to convert it!", ioexception);
				return false;
			} catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
				LOGGER.error("Conversion failed, please try again later", preyggdrasilconverter$conversionerror);
				return false;
			}
		}*/
		return true;
	}

	public static boolean convertWhitelist(final MinecraftServer server) throws IOException {
		final Whitelist whitelist = new Whitelist(new FileRootImpl(new File(".")));

		if (OLD_WHITELIST_FILE.exists() && OLD_WHITELIST_FILE.isFile()) {
			whitelist.read();

			try {
				List<String> list = Files.readLines(OLD_WHITELIST_FILE, Charsets.UTF_8);
				ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
					public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
						server.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
						whitelist.add(p_onProfileLookupSucceeded_1_.getName());
					}

					public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
						PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);

						if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
							throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
						}
					}
				};
				lookupNames(server, list, profilelookupcallback);
				whitelist.save();
				backupConverted(OLD_WHITELIST_FILE);
				return true;
			} catch (IOException ioexception) {
				LOGGER.warn("Could not read old whitelist to convert it!", ioexception);
				return false;
			} catch (PreYggdrasilConverter.ConversionError preyggdrasilconverter$conversionerror) {
				LOGGER.error("Conversion failed, please try again later", preyggdrasilconverter$conversionerror);
				return false;
			}
		}
		return true;
	}

	public static String getStringUUIDFromName(String p_152719_0_) {
		if (!StringUtils.isNullOrEmpty(p_152719_0_) && p_152719_0_.length() <= 16) {
			final MinecraftServer minecraftserver = MinecraftServer.getServer();
			GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(p_152719_0_);

			if (gameprofile != null && gameprofile.getId() != null) {
				return gameprofile.getId().toString();
			}
			if (!minecraftserver.isSinglePlayer() && minecraftserver.isServerInOnlineMode()) {
				final List<GameProfile> list = new ArrayList<>();
				ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
					public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
						minecraftserver.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
						list.add(p_onProfileLookupSucceeded_1_);
					}

					public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
						PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
					}
				};
				lookupNames(minecraftserver, Lists.newArrayList(p_152719_0_), profilelookupcallback);
				return list.size() > 0 && list.get(0).getId() != null ? list.get(0).getId().toString() : "";
			}
			return Player.getUUID(new GameProfile(null, p_152719_0_)).toString();
		}
		return p_152719_0_;
	}

	private static void mkdir(File dir) {
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new PreYggdrasilConverter.ConversionError("Can\'t create directory " + dir.getName() + " in world save directory.");
			}
		} else if (!dir.mkdirs()) {
			throw new PreYggdrasilConverter.ConversionError("Can\'t create directory " + dir.getName() + " in world save directory.");
		}
	}

	private static void backupConverted(File convertedFile) {
		File file1 = new File(convertedFile.getName() + ".converted");
		convertedFile.renameTo(file1);
	}

	private static Date parseDate(String input, Date defaultValue) {
		Date date;

		try {
			date = BanEntry.dateFormat.parse(input);
		} catch (ParseException var4) {
			date = defaultValue;
		}

		return date;
	}

	static class ConversionError extends RuntimeException {

		private ConversionError(String message, Throwable cause) {
			super(message, cause);
		}

		private ConversionError(String message) {
			super(message);
		}

	}

}
