package me.naptie.bungee.core;

import com.google.common.io.ByteStreams;
import me.naptie.bungee.core.commands.Ban;
import me.naptie.bungee.core.commands.Help;
import me.naptie.bungee.core.commands.Maintenance;
import me.naptie.bungee.core.commands.Unban;
import me.naptie.bungee.core.listeners.EventListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import me.naptie.bungee.core.utils.CU;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static net.md_5.bungee.api.ProxyServer.getInstance;

public class Main extends Plugin {

    public static Main instance;
    public static Configuration config;
	public static Configuration bans;
    public static String playerCountFormat;
    public static Map<String, List<String>> messagesOfTheDay = new HashMap<>();
    public static Map<String, String> playerListString = new HashMap<>();
    public static boolean maintenance = false;
    public static Map<String, List<String>> maintenanceMessages = new HashMap<>();
    public static Map<String, String> maintenancePlayerListString = new HashMap<>();
    public static Map<String, String> maintenanceInfo = new HashMap<>();
    public static Logger logger;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {

        instance = this;
        logger = this.getLogger();
        Messages.initialize();

	    if (!getDataFolder().exists()) {
		    getDataFolder().mkdir();
	    }

	    saveResource("config.yml", false);
	    try {
		    config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
	    } catch (IOException e) {
		    e.printStackTrace();
	    }

	    for (String language : config.getStringList("languages")) {
		    File localeFile = new File(getDataFolder(), language + ".yml");
		    if (localeFile.exists()) {
			    if (config.getBoolean("update-language-files")) {
				    saveResource(language + ".yml", true);
			    }
		    } else {
			    saveResource(language + ".yml", false);
		    }
	    }

	    saveResource("bans.yml", false);
	    try {
		    bans = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "bans.yml"));
	    } catch (IOException e) {
		    e.printStackTrace();
	    }

        playerCountFormat = config.getString("player-count-format");
        for (String language : config.getStringList("languages")) {
            messagesOfTheDay.put(language, config.getStringList("messages-of-the-day." + language));

            List<String> playerList = config.getStringList("player-list." + language);
            StringBuilder playerListString1 = new StringBuilder();
            for (int i = 0; i < playerList.size(); i++) {
                if (i == 0) {
                    playerListString1 = new StringBuilder(playerList.get(0));
                } else {
                    playerListString1.append("\n").append(playerList.get(i));
                }
            }
            playerListString.put(language, playerListString1.toString());

            maintenance = config.getBoolean("maintenance.enable");
            maintenanceMessages.put(language, config.getStringList("maintenance.messages." + language));
            maintenanceInfo.put(language, config.getString("maintenance.info." + language));

            List<String> maintenancePlayerList = config.getStringList("maintenance.player-list." + language);
            StringBuilder maintenancePlayerListString1 = new StringBuilder();
            for (int i = 0; i < maintenancePlayerList.size(); i++) {
                if (i == 0) {
                    maintenancePlayerListString1 = new StringBuilder(maintenancePlayerList.get(0));
                } else {
                    maintenancePlayerListString1.append("\n").append(maintenancePlayerList.get(i));
                }
            }
            maintenancePlayerListString.put(language, maintenancePlayerListString1.toString());
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config,
                    new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        getProxy().getPluginManager().registerListener(this, new EventListener());
        getProxy().getPluginManager().registerCommand(this, new Maintenance());
	    getProxy().getPluginManager().registerCommand(this, new Ban());
	    getProxy().getPluginManager().registerCommand(this, new Help());
	    getProxy().getPluginManager().registerCommand(this, new Unban());

    }

    @Override
    public void onDisable() {
        config.set("maintenance.enable", maintenance);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger = null;
    }

    public static void setMaintenance(boolean mtn, final boolean kick, final int delaySec) {
        maintenance = mtn;

        for (final ProxiedPlayer player : getInstance().getPlayers()) {
            if (delaySec < 4) {
                player.sendMessage(CU.c(Messages.getMessage(player, "MAINTENANCE_ON")));
                if (kick && !player.hasPermission("neonmc.proxy.maintenance")) {
                    player.disconnect(CU.ttc(maintenanceInfo.get(Messages.getLanguageName(player))));
                }
                return;
            }

            player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "MAINTENANCE_MODE_TURNING_ON")).replace("%sec%", Integer.toString(delaySec))));

            Executors.newSingleThreadExecutor().execute(() -> {
                int halfDelaySec = delaySec / 2;
                try {
                    TimeUnit.SECONDS.sleep(halfDelaySec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "MAINTENANCE_MODE_TURNING_ON")).replace("%sec%", Integer.toString(halfDelaySec))));

                int quarterDelaySec = halfDelaySec / 2;
                try {
                    TimeUnit.SECONDS.sleep(quarterDelaySec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "MAINTENANCE_MODE_TURNING_ON")).replace("%sec%", Integer.toString(quarterDelaySec))));

                try {
                    TimeUnit.SECONDS.sleep(quarterDelaySec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                player.sendMessage(CU.c(Messages.getMessage(player, "MAINTENANCE_ON")));
                if (kick && !player.hasPermission("neonmc.proxy.maintenance")) {
                    player.disconnect(CU.ttc(maintenanceInfo.get(Messages.getLanguageName(player))));
                }
            });
        }
    }

	@SuppressWarnings({"ResultOfMethodCallIgnored", "UnstableApiUsage"})
	private void saveResource(String fileName, boolean replace) {
		File file = new File(getDataFolder(), fileName);
		if (!file.exists() || replace) {
			try {
				file.createNewFile();
				try (InputStream is = getResourceAsStream(fileName);
				     OutputStream os = new FileOutputStream(file)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to create configuration file", e);
			}
		}
	}
}
