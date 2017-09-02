package com.martoph.martophsmedals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MartophsMedals extends JavaPlugin {

    private static Server server;
    private static Plugin plugin;

    private PluginManager pluginManager;

    public static ArrayList<Player> medalShown = new ArrayList<>();
    public static HashMap<UUID, Integer> guiViewers = new HashMap<>();
    public static Map<Player, ArmorStand> currentPlayerOwnedPlates = new HashMap<>();
    public static Map<Player, ArmorStand> currentOutsideVisiblePlates = new HashMap<>();

    private static String version;

    static Class<?> medalUtil;

    public void onEnable() {
        plugin = this;
        server = getServer();

        String packageName = server.getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1);

        getLogger().info("Loading MartophsMedals for version " + version);

        try {
            medalUtil = Class.forName("com.martoph.martophsmedals." + version + ".MedalUtil");

            /* Check if we have a NMSHandler class at that location.
            if (Listener.class.isAssignableFrom(inventoryListenerClass)) {
                this.inventoryListner = (Listener) inventoryListenerClass.getConstructor().newInstance();
            }*/
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
            medalClass.getAll(Medal.medalsOnEnable);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Something went wrong! Aborting!");
            setEnabled(false);
        }

        pluginManager = server.getPluginManager();

        pluginManager.addPermission(new Permission("mmedal.admin"));

        for (Medal medal : Medal.medalsOnEnable) {
            pluginManager.addPermission(new Permission("mmedal." + medal.getName()));
        }

        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, ArmorStand> entry : currentOutsideVisiblePlates.entrySet()) {
                    entry.getValue().teleport(getPlayerHeadLocation(entry.getKey()));
                }
            }
        }.runTaskTimer(this, 0, 1);

    }

    static Plugin getPlugin() {
        return plugin;
    }

    public void onDisable() {
        Medal medalClass = new Medal(this);

        try {
            medalClass.getAll(Medal.medalsOnDisable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Medal> union = new ArrayList<Medal>(Medal.medalsOnEnable) {{
            addAll(Medal.medalsOnDisable);
        }};

        try {
            for (Medal medal : union) {
                try {
                    pluginManager.removePermission(new Permission("mmedal." + medal.getName()));
                } catch (NullPointerException ignored) {
                }
            }
            pluginManager.removePermission(new Permission("mmedal.admin"));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
                removeMedal(player, true);
            }

            try {
                medalClass.saveAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            getLogger().severe("Something may've gone horribly wrong with loading and unloading!");
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
                    for (Medal medal : Medal.medalsOnEnable) {
                        if (Medal.medalsOnEnable.indexOf(medal) == Medal.medalsOnEnable.size()-1) {
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

                    Medal.medalsOnEnable.remove(Medal.getFromName(args[1]));
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

    public static Location getPlayerHeadLocation(Player player) {
        return player.getLocation().add(0, .1, 0);
    }

    public static void removeMedal(Player player, boolean outsideStand) {
        try {
            Method removeMedal = medalUtil.getDeclaredMethod("removeMedal", Player.class, Boolean.class);
            removeMedal.setAccessible(true);
            removeMedal.invoke(medalUtil, player, outsideStand);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void createMedal(Player player, Medal medal, Boolean playerHead) {
        try {
            Method createMedal = medalUtil.getDeclaredMethod("createMedal", Player.class, Medal.class, Boolean.class);
            createMedal.setAccessible(true);
            createMedal.invoke(medalUtil, player, medal, playerHead);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}

