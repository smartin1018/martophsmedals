package com.martoph.medals;

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
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

            if (event.getRawSlot() == 51 && event.getCurrentItem().getType() == Material.SIGN) {
                GUI.sendMedalInventory(player, MartophsMedals.guiViewers.get(player.getUniqueId()) + 1);
            } else if (event.getRawSlot() == 47 && event.getCurrentItem().getType() == Material.SIGN) {
                GUI.sendMedalInventory(player, MartophsMedals.guiViewers.get(player.getUniqueId()) - 1);
            } else if (event.getRawSlot() == event.getInventory().getSize() - 4 && event.getCurrentItem().getType() == Material.BARRIER) {
                MartophsMedals.removeMedal(player);
            } else if (event.getRawSlot() == event.getInventory().getSize() - 6) {
                if (event.getCurrentItem().getType() == Material.MAGMA_CREAM) {
                    MartophsMedals.medalHidden.remove(player);

                    MartophsMedals.createMedal(player, Medal.getFromDisplayName(MartophsMedals.currentPlates.get(player).getCustomName()));
                    GUI.refresh(player);
                }

                if (event.getCurrentItem().getType() == Material.SLIME_BALL) {
                    MartophsMedals.medalHidden.add(player);

                    PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(1, MartophsMedals.currentPlates.get(player).getEntityId());
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
                    GUI.refresh(player);
                }
            } else {
                Medal medal;

                medal = Medal.getFromDisplayName(event.getCurrentItem().getItemMeta().getDisplayName());

                if (medal == null) {
                    return;
                }

                MartophsMedals.createMedal(player, medal);
            }

            GUI.refresh(player);
        }
    }

    @EventHandler
    public void onExit(InventoryCloseEvent event) {
        if (MartophsMedals.guiViewers.containsKey(event.getPlayer().getUniqueId())) {
            MartophsMedals.guiViewers.remove(event.getPlayer().getUniqueId());
        }
    }

}
