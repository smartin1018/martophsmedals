package com.martoph.martophsmedals;

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

    private static Inventory getMedalInventory(Player player, int page) {
        MartophsMedals.guiViewers.put(player.getUniqueId(), page);

        int pages = (int) Math.ceil((double) Medal.medals.size() / 20);
        int size = (int) (Math.ceil((double) (Medal.medals.size() > 20 ? 20 : Medal.medals.size()) / 5) * 9) + 18;
        Inventory medalInventory = Bukkit.createInventory(null, size, Text.GUINAME.getValue() + (page > 1 ? " p." + page : ""));

        ItemStack redPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 14);
        ItemStack blackPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 15);

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

        int toIndex = Medal.medals.size();

        if (Medal.medals.size() >= page * 20) {
            toIndex = (page * 20);
        }

        int amplifier = 11;
        List<Medal> subMedals = Medal.medals.subList((page - 1) * 20, toIndex);
        for (Medal medal : subMedals) {
            int slot = subMedals.indexOf(medal) + amplifier;
            while ((slot % 9) < 2 || (slot % 9) > 6) {
                slot++;
                amplifier++;
            }

            ItemStack medalItem = new ItemStack(medal.getIcon(), 1, (short) 0, medal.getData());
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

        if (MartophsMedals.currentPlates.containsKey(player)) {
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

        if (MartophsMedals.currentPlates.containsKey(player)) {
            if (MartophsMedals.medalHidden.contains(player)) {
                medalInventory.setItem(medalInventory.getSize() - 6, hiddenFromPlayer);
            } else {
                medalInventory.setItem(medalInventory.getSize() - 6, shownToPlayer);
            }

        }

        if (pages == 1) {
            return medalInventory;
        }

        ItemStack nextPage = new ItemStack(Material.SIGN);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(Text.NEXTPAGE.getValue());
        nextPage.setItemMeta(nextPageMeta);

        ItemStack previousPage = new ItemStack(Material.SIGN);
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

}
