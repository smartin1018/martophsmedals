package com.martoph.martophsmedals;

import com.mojang.datafixers.util.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class InventoryListener implements Listener {

    private HashMap<Player, Medal> medalsOnPlayers = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (MartophsMedals.guiViewers.containsKey(player.getUniqueId())) {

            event.setCancelled(true);

            int currentPage = MartophsMedals.guiViewers.get(player.getUniqueId());
            int rawSlot = event.getRawSlot();

            // Calculates the last inventory space
            int lastSlot;

            if (Medal.medalsOnEnable.size() >= currentPage * 20) {
                lastSlot = 20;
            } else {
                lastSlot = (int) Math.ceil(((double) Medal.medalsOnEnable.size() % 20) / 5) * 5;
            }

            if (event.getRawSlot() > 11 + lastSlot + 10)
                return;

            if (rawSlot == 51 && GUI.inventoryExists(currentPage + 1)) {
                GUI.sendMedalInventory(player,  currentPage + 1);
            } else if (rawSlot == 47 && currentPage != 1) {
                GUI.sendMedalInventory(player, currentPage - 1);
            } else if (rawSlot == event.getInventory().getSize() - 4 && event.getCurrentItem().getType() == Material.BARRIER) {
                medalsOnPlayers.remove(player);
                MartophsMedals.removeMedal(player, true);
            } else if (rawSlot == event.getInventory().getSize() - 6) {
                if (event.getCurrentItem().getType() == Material.MAGMA_CREAM) {
                    MartophsMedals.medalShown.add(player);

                    MartophsMedals.createMedal(player, medalsOnPlayers.get(player), true);
                    GUI.refresh(player);
                }

                if (event.getCurrentItem().getType() == Material.SLIME_BALL) {

                    MartophsMedals.medalShown.remove(player);
                    MartophsMedals.createMedal(player, medalsOnPlayers.get(player), false);
                    GUI.refresh(player);
                }
            } else {

                try {
                    Medal medal;

                    medal = MartophsMedals.medalMap.get(Pair.of(rawSlot, currentPage));

                    if (medal == null) {
                        return;
                    }

                    if (!player.hasPermission("mmedal." + medal.getName())) return;

                    MartophsMedals.createMedal(player, medal, false);
                    MartophsMedals.medalShown.remove(player);
                    medalsOnPlayers.put(player, medal);
                } catch (NullPointerException ignored) {}
            }

            GUI.refresh(player);
        }
    }

    @EventHandler
    public void onExit(InventoryCloseEvent event) {
        MartophsMedals.guiViewers.remove(event.getPlayer().getUniqueId());
    }

}
