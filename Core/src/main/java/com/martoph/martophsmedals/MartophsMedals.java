package com.martoph.martophsmedals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MartophsMedals extends JavaPlugin {

    private static Server server;
    private static Plugin plugin;

    private PluginManager pluginManager;

    public static HashMap<UUID, Integer> guiViewers = new HashMap<>();
    public static ArrayList<Player> medalHidden = new ArrayList<>();
    public static Map<Player, ArmorStand> currentPlates = new HashMap<>();

    private Listener inventoryListner = null;
    private static String version;

    public void onEnable() {
        plugin = this;
        server = getServer();

        String packageName = server.getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1);

        getLogger().info("Loading MartophsMedals for version " + version);

        try {
            final Class<?> inventoryListenerClass = Class.forName("com.martoph.martophsmedals." + version + ".InventoryListener");
            // Check if we have a NMSHandler class at that location.
            if (Listener.class.isAssignableFrom(inventoryListenerClass)) {
                this.inventoryListner = (Listener) inventoryListenerClass.getConstructor().newInstance();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            getLogger().severe("Could not find support for this server version.");
            setEnabled(false);
            return;
        }
        this.getLogger().info("Loading support for " + version);

        ConfigUtil.loadConfig();

        Medal medalClass = new Medal(this);

        try {
            medalClass.getAll(Medal.medals);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Something went wrong! Aborting!");
            setEnabled(false);
        }

        pluginManager = server.getPluginManager();

        pluginManager.addPermission(new Permission("mmedal.medal"));
        pluginManager.addPermission(new Permission("mmedal.admin"));

        for (Medal medal : Medal.medals) {
            pluginManager.addPermission(new Permission("mmedal." + medal.getName()));
        }

        pluginManager.registerEvents(inventoryListner, this);

    }

    static Plugin getPlugin() {
        return plugin;
    }

    public void onDisable() {

        for (Medal medal : Medal.medals) {
            try {
                pluginManager.removePermission(new Permission("mmedal." + medal.getName()));
            } catch (NullPointerException ignored) {
            }
        }
        pluginManager.removePermission(new Permission("mmedal.admin"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
            removeMedal(player);
        }

        Medal medal = new Medal(this);
        try {
            medal.saveAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("medals")) {

            if (!(sender instanceof Player)) {
                sendMessage();
                return false;
            }

            Player player = (Player) sender;

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("list")) {
                    StringBuilder message = new StringBuilder("§a");
                    for (Medal medal : Medal.medals) {
                        if (Medal.medals.indexOf(medal) == Medal.medals.size()-1) {
                            message.append(medal.getName()).append(".");
                            continue;
                        }
                        message.append(medal.getName()).append("§e, §a");
                    }
                    player.sendMessage(message.toString());
                    return true;
                }

                if (!player.hasPermission("mmedal.admin")) {
                    GUI.sendMedalInventory(player);
                    return true;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length < 4 || args.length > 4) {
                        player.sendMessage(Text.SYNTAX.getValue().replace("{usage}", "/medals create <name> <display> <icon material>"));
                        return false;
                    }

                    if (Material.getMaterial(args[3].split(":")[0].toUpperCase()) == null) {
                        player.sendMessage(Text.INVALIDMATERIAL.getValue());
                        return false;
                    }

                    byte data = (byte) 0;

                    try {
                        data = (byte) Integer.parseInt(args[3].split(":")[1]);
                    } catch (IndexOutOfBoundsException | NumberFormatException ignored) {}

                    Medal medalClass = new Medal(this);
                    medalClass.create(args[1], args[2], Material.getMaterial(args[3].split(":")[0].toUpperCase()), data);
                    player.sendMessage(Text.SUCCESSFULCREATE.getValue());
                    return true;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length < 2 || args.length > 2) {
                        player.sendMessage(Text.SYNTAX.getValue().replace("{usage}", "/medals delete <name>"));
                        return false;
                    }

                    if (Medal.getFromName(args[1]) == null) {
                        player.sendMessage(Text.INVALIDMEDAL.getValue());
                        return false;
                    }

                    Medal.medals.remove(Medal.getFromName(args[1]));
                    player.sendMessage(Text.SUCCESSFULDELETE.getValue());
                    return true;
                }
            }

            GUI.sendMedalInventory(player);
        }

        return false;
    }

    private static void sendMessage() {
        final ConsoleCommandSender console = server.getConsoleSender();
        console.sendMessage("§cPlayer command only!");
    }

    public static void createMedal(Player player, Medal medal) {

        removeMedal(player);

        ArmorStand armorstandFinal = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorstandFinal.setSmall(true);
        armorstandFinal.setVisible(false);
        armorstandFinal.setCustomName(medal.getDisplay().replace("&", "§"));
        armorstandFinal.setCustomNameVisible(true);

        currentPlates.put(player, armorstandFinal);
        if (version.equals("v1_8_R3")) {
            player.setPassenger(armorstandFinal);
        } else  {
            player.addPassenger(armorstandFinal);
        }
    }

    public static void removeMedal(Player player) {
        if (MartophsMedals.currentPlates.containsKey(player)) {
            player.eject();
            MartophsMedals.currentPlates.get(player).remove();
            MartophsMedals.currentPlates.remove(player);
        }
    }

}

