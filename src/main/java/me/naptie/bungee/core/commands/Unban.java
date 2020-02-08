package me.naptie.bungee.core.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import me.naptie.bungee.core.Main;
import me.naptie.bungee.core.Messages;
import me.naptie.bungee.core.utils.CU;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Unban extends Command {

	public Unban() {
		super("unban", "neonmc.proxy.ban", "pardon");
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (commandSender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) commandSender;
			if (strings.length > 0) {
				boolean displayed = false;
				for (String key : Main.bans.getKeys()) {
					if (Main.bans.getString(key + ".name").equalsIgnoreCase(strings[0]) && (Main.bans.getBoolean(key + ".permanent") || Main.bans.getLong(key + ".expiration") > System.currentTimeMillis())) {
						Main.bans.set(key + ".permanent", false);
						Main.bans.set(key + ".expiration", System.currentTimeMillis());
						try {
							ConfigurationProvider.getProvider(YamlConfiguration.class).save(Main.bans, new File(Main.instance.getDataFolder(), "bans.yml"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!displayed) {
							player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "UNBANNED_PLAYER")).replace("%player%", Main.bans.getString(key + ".name"))));
							displayed = true;
						}
					}
				}
				if (!displayed)
					player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "PLAYER_NOT_FOUND")).replace("%player%", strings[0])));
			} else {
				player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "USAGE")).replace("%usage%", "/unban <player>")));
			}
		} else {
			if (strings.length > 0) {
				boolean displayed = false;
				for (String key : Main.bans.getKeys()) {
					if (Main.bans.getString(key + ".name").equalsIgnoreCase(strings[0]) && (Main.bans.getBoolean(key + ".permanent") || Main.bans.getLong(key + ".expiration") > System.currentTimeMillis())) {
						Main.bans.set(key + ".permanent", false);
						Main.bans.set(key + ".expiration", System.currentTimeMillis());
						try {
							ConfigurationProvider.getProvider(YamlConfiguration.class).save(Main.bans, new File(Main.instance.getDataFolder(), "bans.yml"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!displayed) {
							commandSender.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage("zh-CN", "UNBANNED_PLAYER")).replace("%player%", Main.bans.getString(key + ".name"))));
							displayed = true;
						}
					}
				}
				if (!displayed)
					commandSender.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage("zh-CN", "PLAYER_NOT_FOUND")).replace("%player%", strings[0])));
			} else {
				commandSender.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "unban <player>")));
			}
		}
	}
}
