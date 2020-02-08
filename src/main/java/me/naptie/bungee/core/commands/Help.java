package me.naptie.bungee.core.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import me.naptie.bungee.core.Main;
import me.naptie.bungee.core.Messages;
import me.naptie.bungee.core.utils.CU;

public class Help extends Command {

	public Help() {
		super("help", null, "bukkit:help");
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (commandSender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) commandSender;
			for (String text : Main.config.getStringList("help." + Messages.getLanguageName(player))) {
				player.sendMessage(CU.ttc(text));
			}
		} else {
			for (String text : Main.config.getStringList("help.zh-CN")) {
				commandSender.sendMessage(CU.ttc(text));
			}
		}
	}
}
