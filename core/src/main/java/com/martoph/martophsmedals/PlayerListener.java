package com.martoph.martophsmedals;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        MartophsMedals.removeMedal(event.getPlayer(), true);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        MartophsMedals.removeMedal(event.getPlayer(), true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!MartophsMedals.currentOutsideVisiblePlates.containsKey(player)) return;

        MartophsMedals.removeMedal(player, true);
    }
}