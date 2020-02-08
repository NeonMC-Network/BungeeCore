package me.naptie.bungee.core.listeners;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import me.naptie.bungee.core.Main;
import me.naptie.bungee.core.Messages;
import me.naptie.bungee.core.commands.Ban;
import me.naptie.bungee.core.utils.CU;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPing(ProxyPingEvent event) {
		ServerPing ping = event.getResponse();
		InetSocketAddress address = event.getConnection().getAddress();
		ServerPing.Players players = ping.getPlayers();
		String language;
		if (getUniqueId(address) == null) {
			language = "en-US";
		} else {
			language = Messages.getLanguageName(getUniqueId(address));
		}

		if (!Main.maintenance) {
			players.setSample(new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo(
					CU.ts(Main.playerListString.get(language)), "")
			});
			String playerCountFormat = Main.playerCountFormat;
			if (Main.playerCountFormat.contains("%ONLINE%")) {
				playerCountFormat = playerCountFormat.replace("%ONLINE%", Integer.toString(ping.getPlayers().getOnline()));
			}
			if (Main.playerCountFormat.contains("%MAX%")) {
				playerCountFormat = playerCountFormat.replace("%MAX%", Integer.toString(ping.getPlayers().getMax()));
			}
			ping.getVersion().setName(playerCountFormat);
			ping.setDescriptionComponent(new TextComponent(CU.ts(Main.messagesOfTheDay.get(language).get(0)) + "\n" + CU.ts(Main.messagesOfTheDay.get(language).get(1))));
		} else {
			ping.getVersion().setProtocol(1);
			ping.getVersion().setName(CU.ts(Main.config.getString("maintenance.status." + language)));
			ping.setDescriptionComponent(new TextComponent(CU.ts(Main.maintenanceMessages.get(language).get(0)) + "\n" + CU.ts(Main.maintenanceMessages.get(language).get(1))));
			ping.getPlayers().setOnline(0);
			ping.getPlayers().setMax(0);
			ping.getPlayers().setSample(new ServerPing.PlayerInfo[]{
					new ServerPing.PlayerInfo(CU.ts(Main.maintenancePlayerListString.get(language)), "")
			});
		}

		event.setResponse(ping);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onConnect(ServerConnectEvent event) {
		final ProxiedPlayer player = event.getPlayer();
		final String language = Messages.getLanguageName(player);
		if (Main.maintenance) {
			if (!player.hasPermission("neonmc.proxy.maintenance")) {
				player.disconnect(CU.ttc(Main.maintenanceInfo.get(language)));
			} else {
				Executors.newSingleThreadExecutor().execute(() -> {
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					player.sendMessage(CU.ttc(Main.maintenanceInfo.get(language)));
				});
			}
		}
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
		UUID uuid = event.getConnection().getUniqueId();
		for (String key : Main.bans.getKeys()) {
			if (Main.bans.getString(key + ".uuid").equals(uuid + "")) {
				if (Main.bans.getBoolean(key + ".permanent")) {
					event.setCancelReason(CU.c(Messages.getMessage(uuid, "PERMANENTLY_BANNED") + "\n\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_1")).replace("%reason%", Main.bans.getString(key + ".reason")) + "\n" + Messages.getMessage(uuid, "BANNED_2") + "\n\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_3")).replace("%id%", key + "") + "\n" + Messages.getMessage(uuid, "BANNED_4")));
					event.setCancelled(true);
				} else if (Main.bans.getLong(key + ".expiration") > System.currentTimeMillis()) {
					event.setCancelReason(CU.c(Objects.requireNonNull(Messages.getMessage(uuid, "TEMPORARILY_BANNED")).replace("%time%", Ban.formatDuration(Messages.getLanguageName(uuid), Main.bans.getLong(key + ".expiration") - System.currentTimeMillis())) + "\n\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_1")).replace("%reason%", Main.bans.getString(key + ".reason")) + "\n" + Messages.getMessage(uuid, "BANNED_2") + "\n\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_3")).replace("%id%", key + "") + "\n" + Messages.getMessage(uuid, "BANNED_4")));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		String address = player.getPendingConnection().getAddress().toString().split(":")[0].replace("/", "");
		for (File file : Objects.requireNonNull(Messages.playerDataFolder.listFiles())) {
			if (file.getName().contains(player.getUniqueId() + "")) {
				try {
					Configuration data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Messages.playerDataFolder, player.getUniqueId() + ".yml"));
					List<String> addresses = data.getStringList("ip");
					addresses.remove("0.0.0.0");
					if (!addresses.contains(address))
						addresses.add(address);
					data.set("ip", addresses);
					ConfigurationProvider.getProvider(YamlConfiguration.class).save(data, new File(Messages.playerDataFolder, player.getUniqueId() + ".yml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/*
		UUID uuid = event.getPlayer().getUniqueId();
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (String key : Main.bans.getKeys()) {
				if (Main.bans.getString(key + ".uuid").equals(uuid + "")) {
					if (Main.bans.getBoolean(key + ".permanent")) {
						player.disconnect(CU.c(Messages.getMessage(uuid, "PERMANENTLY_BANNED") + "\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_1")).replace("%reason%", Main.bans.getString(key + ".reason")) + "\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_2")).replace("%id%", key) + "\n" + "\n" + Messages.getMessage(uuid, "BANNED_3") + "\n" + Messages.getMessage(uuid, "BANNED_4")));
					} else if (Main.bans.getLong(key + ".expiration") > System.currentTimeMillis()) {
						player.disconnect(CU.c(Objects.requireNonNull(Messages.getMessage(uuid, "TEMPORARILY_BANNED")).replace("%time%", Ban.formatTime(Messages.getLanguageName(uuid), Main.bans.getLong(key + ".expiration") - System.currentTimeMillis())) + "\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_1")).replace("%reason%", Main.bans.getString(key + ".reason")) + "\n" + Objects.requireNonNull(Messages.getMessage(uuid, "BANNED_2")).replace("%id%", key) + "\n" + "\n" + Messages.getMessage(uuid, "BANNED_3") + "\n" + Messages.getMessage(uuid, "BANNED_4")));
					}
				}
			}
		});
		 */
	}

	private UUID getUniqueId(InetSocketAddress address) {
		String addressString = address.toString().split(":")[0].replace("/", "");
		for (File file : Objects.requireNonNull(Messages.playerDataFolder.listFiles())) {
			if (file.getName().endsWith(".yml")) {
				try {
					Configuration data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
					List<String> addresses = data.getStringList("ip");
					if (addresses.contains(addressString))
						return UUID.fromString(file.getName().split(".yml")[0]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
