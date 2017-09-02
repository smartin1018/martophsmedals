package com.martoph.martophsmedals;

import org.bukkit.ChatColor;

public enum  Text {
    LOCKED("locked"),
    SYNTAX("syntax"),
    INVALIDMATERIAL("invalidMaterial"),
    INVALIDMEDAL("invalidMedal"),
    SUCCESSFULCREATE("successfulCreate"),
    SUCCESSFULDELETE("successfulDelete"),
    GUINAME("guiname"),
    DESPAWN("despawn"),
    NEXTPAGE("nextpage"),
    PREVPAGE("prevpage"),
    HIDDENFROMPLAYER("hiddenFromPlayer"),
    HIDDENFROMPLAYERLORE("hiddenFromPlayerLore"),
    SHOWNTOPLAYER("shownToPlayer"),
    SHOWNTOPLAYERLORE("shownToPlayerLore");

    private String value;

    Text(String value) {
        this.value = value;
    }

    public String getValue() {
        return ChatColor.translateAlternateColorCodes('&', ConfigUtil.getString("text." + value));
    }
}
