package com.martoph.martophsmedals.v1_12_R1;

import com.martoph.martophsmedals.MartophsMedals;
import com.martoph.martophsmedals.Medal;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import static com.martoph.martophsmedals.MartophsMedals.currentOutsideVisiblePlates;
import static com.martoph.martophsmedals.MartophsMedals.currentPlayerOwnedPlates;

public class MedalUtil {

    public static void removeMedal(Player player, Boolean outsideStand) {
        if (currentPlayerOwnedPlates.containsKey(player)) {
            player.eject();
            currentPlayerOwnedPlates.get(player).remove();
            currentPlayerOwnedPlates.remove(player);
        }
        if (currentOutsideVisiblePlates.containsKey(player) && outsideStand) {
            currentOutsideVisiblePlates.get(player).remove();
            currentOutsideVisiblePlates.remove(player);
        }
    }

    public static void createMedal(Player player, Medal medal) {

        removeMedal(player, true);

        ArmorStand armorStandPlayerVisible = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorStandPlayerVisible.setSmall(true);
        armorStandPlayerVisible.setVisible(false);
        armorStandPlayerVisible.setCustomName(medal.getDisplay().replace("&", "§"));
        armorStandPlayerVisible.setCustomNameVisible(true);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player1 == player) continue;
            PacketPlayOutEntityDestroy killStand = new PacketPlayOutEntityDestroy(1, armorStandPlayerVisible.getEntityId());
            ((CraftPlayer) player1).getHandle().playerConnection.sendPacket(killStand);
        }

        ArmorStand armorStandOutsideVisible = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorStandOutsideVisible.setVisible(false);
        armorStandOutsideVisible.setGravity(false);
        armorStandPlayerVisible.setCustomName(medal.getDisplay().replace("&", "§"));
        armorStandPlayerVisible.setCustomNameVisible(true);

        PacketPlayOutEntityDestroy killStand = new PacketPlayOutEntityDestroy(1, armorStandOutsideVisible.getEntityId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(killStand);

        currentOutsideVisiblePlates.put(player, armorStandOutsideVisible);
        currentPlayerOwnedPlates.put(player, armorStandPlayerVisible);
        player.addPassenger(armorStandPlayerVisible);
    }
}
