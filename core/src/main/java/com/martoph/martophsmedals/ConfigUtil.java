package com.martoph.martophsmedals;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

class ConfigUtil {

    private static File configFile = new File(MartophsMedals.getPlugin().getDataFolder(), "config.yml");

    static void loadConfig() {

        if (!MartophsMedals.getPlugin().getDataFolder().exists()) {
            MartophsMedals.getPlugin().getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            MartophsMedals.getPlugin().saveDefaultConfig();
        } else {
            YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);
            if (configYaml.getString(Text.LOCKED.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.locked", "&cLocked!");
            }

            if (configYaml.getString(Text.SYNTAX.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.syntax", "&cInvalid syntax. Usage: {usage}");

            }

            if (configYaml.getString(Text.INVALIDMATERIAL.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.invalidMaterial", "&cInvalid material.");
            }

            if (configYaml.getString(Text.INVALIDMEDAL.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.invalidMedal", "&cInvalid medal.");
            }

            if (configYaml.getString(Text.SUCCESSFULCREATE.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.successfulCreate", "&aMedal created successfully.");
            }

            if (configYaml.getString(Text.SUCCESSFULDELETE.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.successfulDelete", "&aMedal deleted successfully.");
            }

            if (configYaml.getString(Text.GUINAME.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.guiname", "&6Your Medals");

            }

            if (configYaml.getString(Text.DESPAWN.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.despawn", "&cDespawn com.martoph.martophsmedals.Medal");

            }

            if (configYaml.getString(Text.NEXTPAGE.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.nextpage", "&aNext Page");

            }

            if (configYaml.getString(Text.PREVPAGE.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.prevpage", "&aPrevious Page");

            }

            if (configYaml.getString(Text.HIDDENFROMPLAYER.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.hiddenFromPlayer", "&cMedal Hidden!");

            }

            if (configYaml.getString(Text.HIDDENFROMPLAYERLORE.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.hiddenFromPlayerLore", "&7Your medal is hidden from you to allow interaction.;&7Click to enable.");

            }

            if (configYaml.getString(Text.SHOWNTOPLAYER.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.shownToPlayer", "&2Medal Shown");

            }

            if (configYaml.getString(Text.SHOWNTOPLAYERLORE.getValue()) == null) {
                configYaml.set("configYaml.getString(Text.shownToPlayerLore", "&7Your medal is shown to you. This disables interaction.;&7Click to disable.");

            }

            try {
                configYaml.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static String getString(String path) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        return yamlConfiguration.getString(path);
    }

}
