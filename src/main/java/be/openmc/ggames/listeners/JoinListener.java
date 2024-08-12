package be.openmc.ggames.listeners;

import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import be.openmc.ggames.tractor.PacketHandler;

public class JoinListener implements Listener {
    protected Event event;
    protected @Nullable Player player;

    @EventHandler
    public void onJoinEventPlayer(PlayerJoinEvent event) {
        this.event = event;
        player = event.getPlayer();
        PacketHandler.movement(player);
    }
}
