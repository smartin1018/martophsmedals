package com.martoph.medals;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static File configFile = new File(MartophsMedals.getPlugin().getDataFolder(), "config.yml");

    public static void loadConfig() {

        if (!MartophsMedals.getPlugin().getDataFolder().exists()) {
            MartophsMedals.getPlugin().getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            MartophsMedals.getPlugin().saveDefaultConfig();
        } else {
            YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);
            if (Text.LOCKED.getValue() == null) {
                configYaml.set("text.locked", "§cLocked!");
            }

            if (Text.SYNTAX.getValue() == null) {
                configYaml.set("text.syntax", "§cInvalid syntax. Usage: {usage}");

            }

            if (Text.INVALIDMATERIAL.getValue() == null) {
                configYaml.set("text.invalidMaterial", "§cInvalid material.");
            }

            if (Text.INVALIDMEDAL.getValue() == null) {
                configYaml.set("text.invalidMaterial", "§cInvalid medal.");
            }

            if (Text.SUCCESSFULCREATE.getValue() == null) {
                configYaml.set("text.successfulCreate", "§aMedal created successfully.");
            }

            if (Text.SUCCESSFULDELETE.getValue() == null) {
                configYaml.set("text.successfulDelete", "§aMedal deleted successfully.");
            }

            if (Text.GUINAME.getValue() == null) {
                configYaml.set("text.guiname", "§6Your Medals");

            }

            if (Text.DESPAWN.getValue() == null) {
                configYaml.set("text.despawn", "§cDespawn Medal");

            }

            if (Text.NEXTPAGE.getValue() == null) {
                configYaml.set("text.nextpage", "§aNext Page");

            }

            if (Text.PREVPAGE.getValue() == null) {
                configYaml.set("text.prevpage", "§aPrevious Page");

            }

            if (Text.HIDDENFROMPLAYER.getValue() == null) {
                configYaml.set("text.hiddenFromPlayer", "§cMedal Hidden!");

            }

            if (Text.HIDDENFROMPLAYERLORE.getValue() == null) {
                configYaml.set("text.hiddenFromPlayerLore", "§7Your medal is hidden from you to allow interaction.;§7Click to enable.");

            }

            if (Text.SHOWNTOPLAYER.getValue() == null) {
                configYaml.set("text.shownToPlayer", "§2Medal Shown");

            }

            if (Text.SHOWNTOPLAYERLORE.getValue() == null) {
                configYaml.set("text.shownToPlayerLore", "§7Your medal is shown to you. This disables interaction.;§7Click to disable.");

            }

            try {
                configYaml.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getString(String path) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        return yamlConfiguration.getString(path);
    }

}
