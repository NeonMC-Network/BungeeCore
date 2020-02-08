package me.naptie.bungee.core;


import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import me.naptie.bungee.core.utils.CU;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Messages {

	public static File playerDataFolder;

	@SuppressWarnings("ResultOfMethodCallIgnored")
	static void initialize() {
		playerDataFolder = new File(Main.instance.getDataFolder().getAbsolutePath().split("NeonMC" + (System.getProperty("os.name").startsWith("Windows") ? "\\\\" : File.separator))[0] + "NeonMC" + File.separator + "PlayerData" + File.separator);
		if (!playerDataFolder.exists()) {
			playerDataFolder.mkdir();
		}
	}

	public static String getMessage(Configuration language, String message) {
		return CU.ts(language.getString(message));
	}

	public static String getMessage(String language, String message) {
		try {
			return CU.ts(YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(Main.instance.getDataFolder(), language + ".yml")).getString(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMessage(ProxiedPlayer player, String message) {
		try {
			return CU.ts(getLanguage(player).getString(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getMessage(UUID uuid, String message) {
		return CU.ts(getMessage(getLanguageName(uuid), message));
	}

	private static Configuration getLanguage(ProxiedPlayer player) throws IOException {
		File locale = new File(Main.instance.getDataFolder(), getLanguageName(player) + ".yml");
		return YamlConfiguration.getProvider(YamlConfiguration.class).load(locale);
	}

	public static String getLanguageName(ProxiedPlayer player) {
		File file = new File(playerDataFolder, player.getUniqueId() + ".yml");
		if (!file.exists())
			return "en-US";
		Configuration config = null;
		try {
			config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert config != null;
		return config.getString("language");
	}

	public static String getLanguageName(UUID uuid) {
		File file = new File(playerDataFolder, uuid + ".yml");
		if (!file.exists())
			return "en-US";
		Configuration config = null;
		try {
			config = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert config != null;
		return config.getString("language");
	}

}
