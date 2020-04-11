package com.martoph.martophsmedals;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (MartophsMedals.guiViewers.containsKey(player.getUniqueId())) {

            event.setCancelled(true);

            int currentPage = MartophsMedals.guiViewers.get(player.getUniqueId());

            if (event.getRawSlot() == 51 && GUI.inventoryExists(currentPage + 1)) {
                GUI.sendMedalInventory(player,  currentPage + 1);
            } else if (event.getRawSlot() == 47 && currentPage != 1) {
                GUI.sendMedalInventory(player, currentPage - 1);
            } else if (event.getRawSlot() == event.getInventory().getSize() - 4 && event.getCurrentItem().getType() == Material.BARRIER) {
                MartophsMedals.removeMedal(player, true);
            } else if (event.getRawSlot() == event.getInventory().getSize() - 6) {
                if (event.getCurrentItem().getType() == Material.MAGMA_CREAM) {
                    MartophsMedals.medalShown.add(player);

                    MartophsMedals.createMedal(player, Medal.getFromDisplayName(MartophsMedals.currentPlayerOwnedPlates.get(player).getCustomName()), true);
                    GUI.refresh(player);
                }

                if (event.getCurrentItem().getType() == Material.SLIME_BALL) {

                    MartophsMedals.medalShown.remove(player);
                    MartophsMedals.createMedal(player, Medal.getFromDisplayName(MartophsMedals.currentPlayerOwnedPlates.get(player).getCustomName()), false);
                    GUI.refresh(player);
                }
            } else {

                try {
                    Medal medal;

                    medal = Medal.getFromDisplayName(event.getCurrentItem().getItemMeta().getDisplayName());

                    if (medal == null) {
                        return;
                    }

                    if (!player.hasPermission("mmedal." + medal.getName())) return;

                    MartophsMedals.createMedal(player, medal, false);
                    MartophsMedals.medalShown.remove(player);
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
