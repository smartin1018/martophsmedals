package com.martoph.medals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Medal {

    private String name, display;
    private Material icon;

    private Plugin martophsMedals;

    public static ArrayList<Medal> medals = new ArrayList<Medal>();

    public Medal(Plugin martophsMedals) {
        this.martophsMedals = martophsMedals;
    }

    public Medal(String name, String display, Material icon) {
        this.name = name;
        this.display = display;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    private File medalFile;
    private YamlConfiguration medalYaml;

    void getAll() throws IOException {

        if (!martophsMedals.getDataFolder().exists()) {
            martophsMedals.getDataFolder().mkdirs();
        }

        medalFile = new File(martophsMedals.getDataFolder(), "medals.yml");

        if (!medalFile.exists()) {
            medalFile.createNewFile();
        }

        medalYaml = YamlConfiguration.loadConfiguration(medalFile);

        if (medalYaml.getConfigurationSection("medals") == null) {
            medalYaml.createSection("medals");
            medalYaml.createSection("medals.example");
            medalYaml.set("medals.example.display", "&fExample");
            medalYaml.set("medals.example.icon", "DIAMOND_BLOCK");
            medalYaml.save(medalFile);
        }

        ConfigurationSection medalSection = medalYaml.getConfigurationSection("medals");

        for (String key : medalSection.getKeys(false)) {

            String display;
            Material icon;

            String path = "medals." + key;

            display = medalYaml.getString(path + ".display");

            icon = Material.getMaterial(medalYaml.get(path + ".icon").toString().toUpperCase());

            try {
                icon.name();
            } catch (NullPointerException e) {
                martophsMedals.getLogger().warning("Invalid icon material for medal \"" + key + ",\" input: " + medalYaml.getString(path + ".icon") + ". Ignoring.");
                continue;
            }

            if (display.length() > 16) {
                display = display.substring(0, 15);
            }

            Medal medal = new Medal(key, display, icon);
            medals.add(medal);
        }
    }

    void create(String name, String display, Material icon){
        Medal medal = new Medal(name, display, icon);
        medals.add(medal);
    }

    void saveAll() throws IOException {

        medalFile = new File(martophsMedals.getDataFolder(), "medals.yml");

        if (!medalFile.exists()) {
            medalFile.createNewFile();
        }

        medalYaml = YamlConfiguration.loadConfiguration(medalFile);

        for (Medal medal : medals) {

            if (medalYaml.getConfigurationSection("medals." + medal.getName()) == null) {
                medalYaml.createSection("medals." + medal.getName());
            }

            medalYaml.set("medals." + medal.getName() + ".display", medal.getDisplay());
            medalYaml.set("medals." + medal.getName() + ".icon", medal.getIcon().name());
        }

        for (String key : medalYaml.getConfigurationSection("medals").getKeys(false)) {
            if (getFromName(key) == null) {
                medalYaml.set("medals." + key, null);
            }
        }
        medalYaml.save(medalFile);
    }

    public static Medal getFromDisplayName(String displayName) {
        Medal medal = null;
        for (Medal medalSearch : Medal.medals) {
            try {
                if (medalSearch.getDisplay().replace("&", "§").equals(displayName)) {
                    medal = medalSearch;
                }
            } catch (NullPointerException e) {
                return null;
            }
        }
        return medal;
    }

    public static Medal getFromName(String name) {
        Medal medal = null;
        for (Medal medalSearch : Medal.medals) {
            try {
                if (medalSearch.getName().equals(name)) {
                    medal = medalSearch;
                }
            } catch (NullPointerException e) {
                return null;
            }
        }
        return medal;
    }
}