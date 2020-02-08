package me.naptie.bungee.core.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import me.naptie.bungee.core.Main;
import me.naptie.bungee.core.Messages;
import me.naptie.bungee.core.utils.CU;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Ban extends Command {

	public Ban() {
		super("ban", "neonmc.proxy.ban");
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (commandSender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) commandSender;
			if (strings.length > 0) {
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(strings[0]);
				if (target == null) {
					player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "PLAYER_NOT_FOUND")).replace("%player%", strings[0])));
					return;
				}
				if (strings.length == 1) {
					ban(commandSender, target, true, "", "Banned by a staff.");
				} else {
					if (strings[1].equalsIgnoreCase("permanent") || strings[1].equalsIgnoreCase("perm")) {
						StringBuilder reason = new StringBuilder();
						for (int i = 2; i < strings.length; i++) {
							if (i == 2)
								reason = new StringBuilder(strings[2]);
							else
								reason.append(" ").append(strings[i]);
						}
						ban(commandSender, target, true, "", reason.toString().equals("") ? "Banned by a staff." : reason.toString());
					} else {
						StringBuilder reason = new StringBuilder();
						for (int i = 2; i < strings.length; i++) {
							if (i == 2)
								reason = new StringBuilder(strings[2]);
							else
								reason.append(" ").append(strings[i]);
						}
						ban(commandSender, target, false, strings[1], reason.toString().equals("") ? "Banned by a staff." : reason.toString());
					}
				}
			} else {
				player.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage(player, "USAGE")).replace("%usage%", "/ban <player> [time] [reason]; [time] = \"?d?h?m?s\"")));
			}
		} else {
			if (strings.length > 0) {
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(strings[0]);
				if (target == null) {
					commandSender.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage("zh-CN", "PLAYER_NOT_FOUND")).replace("%player%", strings[0])));
					return;
				}
				if (strings.length == 1) {
					ban(commandSender, target, true, "", "Banned by a staff.");
				} else {
					if (strings[1].equalsIgnoreCase("permanent") || strings[1].equalsIgnoreCase("perm")) {
						StringBuilder reason = new StringBuilder();
						for (int i = 2; i < strings.length; i++) {
							if (i == 2)
								reason = new StringBuilder(strings[2]);
							else
								reason.append(" ").append(strings[i]);
						}
						ban(commandSender, target, true, "", reason.toString().equals("") ? "Banned by a staff." : reason.toString());
					} else {
						StringBuilder reason = new StringBuilder();
						for (int i = 2; i < strings.length; i++) {
							if (i == 2)
								reason = new StringBuilder(strings[2]);
							else
								reason.append(" ").append(strings[i]);
						}
						ban(commandSender, target, false, strings[1], reason.toString().equals("") ? "Banned by a staff." : reason.toString());
					}
				}
			} else {
				commandSender.sendMessage(CU.c(Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "ban <player> [time] [reason]; [time] = \"?d?h?m?s\"")));
			}
		}
	}

	private void ban(CommandSender performer, ProxiedPlayer player, boolean permanent, String time, String reason) {
		String key = "#" + Long.toHexString(System.currentTimeMillis()).toUpperCase();
		Main.bans.set(key + ".name", player.getName());
		Main.bans.set(key + ".uuid", player.getUniqueId().toString());
		Main.bans.set(key + ".permanent", permanent);
		Calendar c = Calendar.getInstance();
		if (!permanent) {
			Date date = new Date();
			c.setTime(date);
			if (time.contains("d")) {
				c.add(Calendar.DAY_OF_YEAR, Integer.parseInt(time.split("d")[0]));
				time = time.split("d")[1];
			}
			if (time.contains("h")) {
				c.add(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split("h")[0]));
				time = time.split("h")[1];
			}
			if (time.contains("m")) {
				c.add(Calendar.MINUTE, Integer.parseInt(time.split("m")[0]));
				time = time.split("m")[1];
			}
			if (time.contains("s")) {
				c.add(Calendar.SECOND, Integer.parseInt(time.split("s")[0]));
			}
			Main.bans.set(key + ".expiration", c.getTimeInMillis());
			performer.sendMessage(CU.c(performer instanceof ProxiedPlayer ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) performer, "BANNED_PLAYER_TEMPORARILY")).replace("%player%", player.getName()).replace("%time%", formatDuration(Messages.getLanguageName(player), Main.bans.getLong(key + ".expiration") - System.currentTimeMillis())).replace("%reason%", reason) : Objects.requireNonNull(Messages.getMessage("zh-CN", "BANNED_PLAYER_TEMPORARILY")).replace("%player%", player.getName()).replace("%time%", formatDuration(Messages.getLanguageName(player), Main.bans.getLong(key + ".expiration") - System.currentTimeMillis())).replace("%reason%", reason)));
		} else {
			performer.sendMessage(CU.c(performer instanceof ProxiedPlayer ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) performer, "BANNED_PLAYER_PERMANENTLY")).replace("%player%", player.getName()).replace("%reason%", reason) : Objects.requireNonNull(Messages.getMessage("zh-CN", "BANNED_PLAYER_PERMANENTLY")).replace("%player%", player.getName()).replace("%reason%", reason)));
		}
		Main.bans.set(key + ".reason", reason);
		Main.bans.set(key + ".performer", performer.getName());
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(Main.bans, new File(Main.instance.getDataFolder(), "bans.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.disconnect(CU.c((permanent ? Messages.getMessage(player, "PERMANENTLY_BANNED") : Objects.requireNonNull(Messages.getMessage(player, "TEMPORARILY_BANNED")).replace("%time%", formatDuration(Messages.getLanguageName(player), c.getTimeInMillis() - System.currentTimeMillis()))) + "\n\n" + Objects.requireNonNull(Messages.getMessage(player, "BANNED_1")).replace("%reason%", reason) + "\n" + Messages.getMessage(player, "BANNED_2") + "\n\n" + Objects.requireNonNull(Messages.getMessage(player, "BANNED_3")).replace("%id%", key + "") + "\n" + Messages.getMessage(player, "BANNED_4")));
	}

	public static String formatDuration(String language, long duration) {
		duration = duration / 1000;
		int seconds = (int) (duration % 60);
		int minutes = (int) (TimeUnit.SECONDS.toMinutes(duration) % 60);
		int hours = (int) (TimeUnit.SECONDS.toHours(duration) % 24);
		int days = (int) (TimeUnit.SECONDS.toDays(duration) % 30);

		List<String> stringList = new ArrayList<>();
		if (days > 0)
			if (days == 1)
				stringList.add(days + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "DAYS")).replace("%s%", "") : Messages.getMessage(language, "DAYS")));
			else
				stringList.add(days + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "DAYS")).replace("%s%", "s") : Messages.getMessage(language, "DAYS")));

		if (hours > 0)
			if (hours == 1)
				stringList.add(hours + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "HOURS")).replace("%s%", "") : Messages.getMessage(language, "HOURS")));
			else
				stringList.add(hours + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "HOURS")).replace("%s%", "s") : Messages.getMessage(language, "HOURS")));

		if (minutes > 0)
			if (minutes == 1)
				stringList.add(minutes + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "MINUTES")).replace("%s%", "") : Messages.getMessage(language, "MINUTES")));
			else
				stringList.add(minutes + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "MINUTES")).replace("%s%", "s") : Messages.getMessage(language, "MINUTES")));

		if (stringList.isEmpty() || seconds > 0)
			if (seconds == 1)
				stringList.add(seconds + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "SECONDS")).replace("%s%", "") : Messages.getMessage(language, "SECONDS")));
			else
				stringList.add(seconds + (language.contains("en") ? Objects.requireNonNull(Messages.getMessage(language, "SECONDS")).replace("%s%", "s") : Messages.getMessage(language, "SECONDS")));

		StringBuilder ft = new StringBuilder(stringList.get(0));
		for (int i = 1; i < stringList.size(); i++) {
			ft.append(stringList.get(i));
		}
		String result = ft.toString();
		if (result.endsWith(Objects.requireNonNull(Messages.getMessage(language, "SEPARATOR")))) result = replaceLast(result, Objects.requireNonNull(Messages.getMessage(language, "SEPARATOR")), "");
		if (result.endsWith(Objects.requireNonNull(Messages.getMessage(language, "MS_SEPARATOR")))) result = replaceLast(result, Objects.requireNonNull(Messages.getMessage(language, "MS_SEPARATOR")), "");
		return result;
	}

	private static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}
}
