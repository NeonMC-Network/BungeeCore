package me.naptie.bungee.core.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import me.naptie.bungee.core.Main;
import me.naptie.bungee.core.Messages;
import me.naptie.bungee.core.utils.CU;

import java.util.Objects;

public class Maintenance extends Command {

	public Maintenance() {
		super("maintenance", "neonmc.proxy.maintenance", "mtn");
	}

	@Override
	public void execute(CommandSender commandSender, String[] strings) {
		if (strings.length == 0) {
			if (!Main.maintenance) {
				Main.setMaintenance(true, false, 0);
				Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
			} else {
				Main.maintenance = false;
				commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_OFF")));
			}
		} else if (strings.length == 1) {
			if (strings[0].equalsIgnoreCase("on") || strings[0].equalsIgnoreCase("true") || strings[0].equalsIgnoreCase("enable")) {
				if (!Main.maintenance) {
					Main.setMaintenance(true, false, 0);
					Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
				} else {
					commandSender.sendMessage(CU.c((commandSender instanceof  ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_ALREADY_ON") : Messages.getMessage("zh-CN", "MAINTENANCE_ALREADY_ON")));
				}
			} else if (strings[0].equalsIgnoreCase("off") || strings[0].equalsIgnoreCase("false") || strings[0].equalsIgnoreCase("disable")) {
				if (Main.maintenance) {
					Main.maintenance = false;
					commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_OFF")));
				} else {
					commandSender.sendMessage(CU.c((commandSender instanceof  ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_ALREADY_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_ALREADY_OFF")));
				}
			} else {
				commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
			}
		} else if (strings.length == 2) {
			if (strings[0].equalsIgnoreCase("on") || strings[0].equalsIgnoreCase("true") || strings[0].equalsIgnoreCase("enable")) {
				if (!Main.maintenance) {
					if (strings[1].equalsIgnoreCase("true") || strings[1].equalsIgnoreCase("yes")) {
						Main.setMaintenance(true, true, 0);
						Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
					} else if (strings[1].equalsIgnoreCase("false") || strings[1].equalsIgnoreCase("no")) {
						Main.setMaintenance(true, false, 0);
						Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
					} else if (isNumeric(strings[1])) {
						Main.setMaintenance(true, false, Integer.valueOf(strings[1]));
						Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
					} else {
						commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
					}
				} else {
					commandSender.sendMessage(CU.c((commandSender instanceof  ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_ALREADY_ON") : Messages.getMessage("zh-CN", "MAINTENANCE_ALREADY_ON")));
				}
			} else if (strings[0].equalsIgnoreCase("off") || strings[0].equalsIgnoreCase("false") || strings[0].equalsIgnoreCase("disable")) {
				if (Main.maintenance) {
					Main.maintenance = false;
					commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_OFF")));
				} else {
					commandSender.sendMessage(CU.c((commandSender instanceof  ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_ALREADY_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_ALREADY_OFF")));
				}
			} else {
				commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
			}
		} else {
			if (strings[0].equalsIgnoreCase("on") || strings[0].equalsIgnoreCase("true") || strings[0].equalsIgnoreCase("enable")) {
				if (!Main.maintenance) {
					if (strings[1].equalsIgnoreCase("true") || strings[1].equalsIgnoreCase("yes")) {
						if (isNumeric(strings[2])) {
							Main.setMaintenance(true, true, Integer.valueOf(strings[2]));
							Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
						} else {
							commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
						}
					} else if (strings[1].equalsIgnoreCase("false") || strings[1].equalsIgnoreCase("no")) {
						if (isNumeric(strings[2])) {
							Main.setMaintenance(true, false, Integer.valueOf(strings[2]));
							Main.logger.info(Messages.getMessage("zh-CN", "MAINTENANCE_ON"));
						} else {
							commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
						}
					} else {
						commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
					}
				} else {
					commandSender.sendMessage(CU.c((commandSender instanceof  ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_ALREADY_ON") : Messages.getMessage("zh-CN", "MAINTENANCE_ALREADY_ON")));
				}
			} else if (strings[0].equalsIgnoreCase("off") || strings[0].equalsIgnoreCase("false") || strings[0].equalsIgnoreCase("disable")) {
				if (Main.maintenance) {
					Main.maintenance = false;
					commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_OFF")));
				} else {
					commandSender.sendMessage(CU.c((commandSender instanceof  ProxiedPlayer) ? Messages.getMessage((ProxiedPlayer) commandSender, "MAINTENANCE_ALREADY_OFF") : Messages.getMessage("zh-CN", "MAINTENANCE_ALREADY_OFF")));
				}
			} else {
				commandSender.sendMessage(CU.c((commandSender instanceof ProxiedPlayer) ? Objects.requireNonNull(Messages.getMessage((ProxiedPlayer) commandSender, "USAGE")).replace("%usage%", "/maintenance <enable> [kick] [delay]") : Objects.requireNonNull(Messages.getMessage("zh-CN", "USAGE")).replace("%usage%", "maintenance <enable> [kick] [delay]")));
			}
		}
	}

	private boolean isNumeric(String str) {
		if (str == null) {
			return false;
		} else {
			int sz = str.length();
			for (int i = 0; i < sz; ++i) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	}
}
