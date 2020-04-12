package com.martoph.martophsmedals;

import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUI {

    public static boolean inventoryExists(int page) {
        return page > (int) Math.ceil((double) Medal.medalsOnEnable.size() / 20);
    }

    private static Inventory getMedalInventory(Player player, int page) {
        MartophsMedals.guiViewers.put(player.getUniqueId(), page);

        int pages = (int) Math.ceil((double) Medal.medalsOnEnable.size() / 20);
        int size = (int) (Math.ceil((double) (Medal.medalsOnEnable.size() > 20 ? 20 : Medal.medalsOnEnable.size()) / 5) * 9) + 18;
        Inventory medalInventory = Bukkit.createInventory(null, size, Text.GUINAME.getValue() + (page > 1 ? " p." + page : ""));

        ItemStack redPane = new ItemStack(Material.AIR), blackPane = new ItemStack(Material.AIR);

        if (MartophsMedals.legacy) {
            Class materialClass = Material.class;
            Material[] materials = (Material[]) materialClass.getEnumConstants();

            for (Material material : materials) {
                if (material.toString().equals("STAINED_GLASS_PANE")) {
                    redPane = new ItemStack(material, 1, (short) 0, (byte) 14);
                    blackPane = new ItemStack(material, 1, (short) 0, (byte) 15);
                    break;
                }
            }

        } else {
            redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            blackPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        }

        medalInventory.addItem(redPane, blackPane, blackPane, blackPane, blackPane, blackPane, blackPane, blackPane, redPane);

        medalInventory.setItem(size - 1, redPane);
        medalInventory.setItem(0, redPane);
        for (int i = 1; i < 8; i++) {
            medalInventory.setItem(size - i - 1, blackPane);
            medalInventory.setItem(i, blackPane);
        }
        medalInventory.setItem(size - 9, redPane);
        medalInventory.setItem(8, redPane);

        for (int i = 1; i <= (size/9) - 2; i++) {
            int row = i*9;
            medalInventory.setItem(row, blackPane);
            medalInventory.setItem(row + 1, blackPane);
            medalInventory.setItem(row + 7, blackPane);
            medalInventory.setItem(row + 8, blackPane);
        }

        int toIndex = Medal.medalsOnEnable.size();

        if (Medal.medalsOnEnable.size() >= page * 20) {
            toIndex = (page * 20);
        }

        int amplifier = 11;
        List<Medal> subMedals = Medal.medalsOnEnable.subList((page - 1) * 20, toIndex);
        for (Medal medal : subMedals) {
            int slot = subMedals.indexOf(medal) + amplifier;
            while ((slot % 9) < 2 || (slot % 9) > 6) {
                slot++;
                amplifier++;
            }

            ItemStack medalItem = MartophsMedals.legacy ? new ItemStack(medal.getIcon(), 1, (short) 0, medal.getData()) : new ItemStack(medal.getIcon());
            ItemMeta medalItemMeta = medalItem.getItemMeta();
            if (!player.hasPermission("mmedals." + medal.getName())) {
                List<String> lore = new ArrayList<String>() {{
                    add("");
                    add(Text.LOCKED.getValue());
                }};
                medalItemMeta.setLore(lore);
            }
            medalItemMeta.setDisplayName(medal.getDisplay());
            medalItem.setItemMeta(medalItemMeta);

            medalInventory.setItem(slot, medalItem);
        }

        ItemStack despawn = new ItemStack(Material.BARRIER);
        ItemMeta despawnItemMeta = despawn.getItemMeta();
        despawnItemMeta.setDisplayName(Text.DESPAWN.getValue());
        despawn.setItemMeta(despawnItemMeta);

        if (MartophsMedals.currentPlayerOwnedPlates.containsKey(player)) {
            medalInventory.setItem(medalInventory.getSize() - 4, despawn);
        }

        ItemStack hiddenFromPlayer = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta hiddenFromPlayerMeta = hiddenFromPlayer.getItemMeta();
        hiddenFromPlayerMeta.setDisplayName(Text.HIDDENFROMPLAYER.getValue());
        hiddenFromPlayerMeta.setLore(Arrays.asList(Text.HIDDENFROMPLAYERLORE.getValue().split(";")));
        hiddenFromPlayer.setItemMeta(hiddenFromPlayerMeta);

        ItemStack shownToPlayer = new ItemStack(Material.SLIME_BALL);
        ItemMeta shownToPlayerMeta = shownToPlayer.getItemMeta();
        shownToPlayerMeta.setDisplayName(Text.SHOWNTOPLAYER.getValue());
        shownToPlayerMeta.setLore(Arrays.asList(Text.SHOWNTOPLAYERLORE.getValue().split(";")));
        shownToPlayer.setItemMeta(shownToPlayerMeta);

        if (MartophsMedals.currentPlayerOwnedPlates.containsKey(player)) {
            if (MartophsMedals.medalShown.contains(player)) {
                medalInventory.setItem(medalInventory.getSize() - 6, shownToPlayer);
            } else {
                medalInventory.setItem(medalInventory.getSize() - 6, hiddenFromPlayer);
            }

        }

        if (pages == 1) {
            return medalInventory;
        }

        Material sign = Material.AIR;

        if (MartophsMedals.legacy) {
            Class materialClass = Material.class;
            Material[] materials = (Material[]) materialClass.getEnumConstants();

            for (Material material : materials) {
                if (material.toString().equals("SIGN")) {
                    sign = material;
                    break;
                }
            }

        } else {
            sign = Material.OAK_SIGN;
        }

        ItemStack nextPage = new ItemStack(sign);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(Text.NEXTPAGE.getValue());
        nextPage.setItemMeta(nextPageMeta);

        ItemStack previousPage = new ItemStack(sign);
        ItemMeta previousPageMeta = previousPage.getItemMeta();
        previousPageMeta.setDisplayName(Text.PREVPAGE.getValue());
        previousPage.setItemMeta(previousPageMeta);

        if (page < pages) {
            medalInventory.setItem(51, nextPage);
        }

        if (page > 1) {
            medalInventory.setItem(47, previousPage);
        }

        return medalInventory;
    }

    public static void sendMedalInventory(Player player) {
        player.openInventory(getMedalInventory(player, 1));
    }

    public static void refresh(Player player) {
        GUI.sendMedalInventory(player, MartophsMedals.guiViewers.get(player.getUniqueId()));
    }

    public static void sendMedalInventory(Player player, int page) {
        player.closeInventory();
        player.openInventory(getMedalInventory(player, page));
    }

    public static void predictMedalPlacement() {
        for (int page = 1; page < Math.ceil((double) Medal.medalsOnEnable.size() / 20) + 1; page++) {
            int toIndex = Medal.medalsOnEnable.size();

            if (Medal.medalsOnEnable.size() >= page * 20) {
                toIndex = (page * 20);
            }

            int amplifier = 11;
            List<Medal> subMedals = Medal.medalsOnEnable.subList((page - 1) * 20, toIndex);
            for (Medal medal : subMedals) {
                int slot = subMedals.indexOf(medal) + amplifier;
                while ((slot % 9) < 2 || (slot % 9) > 6) {
                    slot++;
                    amplifier++;
                }

                MartophsMedals.medalMap.put(Pair.of(slot, page), medal);
            }
        }
    }

}
