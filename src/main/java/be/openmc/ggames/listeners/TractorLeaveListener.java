package be.openmc.ggames.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

import be.openmc.ggames.tractor.TractorData;
import be.openmc.ggames.utils.BossBarUtils;
import be.openmc.ggames.utils.TractorUtils;

import javax.annotation.Nullable;

public class TractorLeaveListener implements Listener {
	
    protected Event event;
    protected @Nullable Player player;

    @EventHandler
    public void onTractorLeave(EntityDismountEvent event) {
        this.event = event;
        final Entity entity = event.getDismounted();
        if (!(event.getEntity() instanceof Player)) return;
        player = (Player) event.getEntity();

        if (!TractorUtils.isTractor(entity)) return;
        if (!entity.getCustomName().contains("TRACTOR_MAINSEAT_")) return;
        String license = TractorUtils.getLicensePlate(entity);
        if (TractorData.autostand.get("TRACTOR_MAIN_" + license) == null) return;

        BossBarUtils.removeBossBar(player, license);
        TractorUtils.turnOff(license);
    }
}
