package me.naptie.bungee.core.utils;

import net.md_5.bungee.api.ChatColor;

import net.md_5.bungee.api.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;

public class CU {

    public static String ts(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static TextComponent ttc(String string) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', string));
    }

    public static TextComponent c(String message) {
        return new TextComponent(message);
    }

    public static List<String> tl(List<String> stringList) {
        List<String> newStringList = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            newStringList.add(ChatColor.translateAlternateColorCodes('&', stringList.get(i)));
        }
        return newStringList;
    }

}
