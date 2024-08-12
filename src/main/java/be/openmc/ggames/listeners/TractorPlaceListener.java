package be.openmc.ggames.listeners;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.tractor.Tractor;
import be.openmc.ggames.utils.TextUtils;
import be.openmc.ggames.utils.TractorUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class TractorPlaceListener implements Listener {
	
    protected Event event;
    protected @Nullable Player player;

    @EventHandler
    public void onTractorPlace(final PlayerInteractEvent event) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        this.event = event;
        player = event.getPlayer();

        final Action action = event.getAction();
        final ItemStack item = event.getItem();
        final Block clickedBlock = event.getClickedBlock();

        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        if (item.getAmount() == 0) return;
        if (!item.hasItemMeta()
                || clickedBlock == null
        ) return;
        if (!(new NBTItem(item)).hasTag("tractor.kenteken")) return;
        String license = TractorUtils.getLicensePlate(item);
        if (license == null) return;

        Location loc = event.getClickedBlock().getLocation();
        Tractor tractor = TractorUtils.getTractor(license);
        if (tractor == null) return;

        if (event.getHand() != EquipmentSlot.HAND) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextUtils.colorize("&cTu ne peux pas interagir avec ce bloc lorsque tu as un élément de tracteur dans ta main"));
            return;
        }
        if (!config.keyExists(license)) {
            player.sendMessage(TextUtils.colorize("&cLe véhicule n'a pas été trouvé"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (TractorUtils.getTractor(license) == null) {
        	player.sendMessage(TextUtils.colorize("&cLe véhicule n'a pas été trouvé"));
            return;
        }

        Location location = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());

        TractorUtils.spawnTractor(license, location);
        player.getInventory().remove(player.getEquipment().getItemInHand());
    }
}
