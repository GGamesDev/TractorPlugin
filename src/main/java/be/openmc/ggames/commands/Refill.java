package be.openmc.ggames.commands;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.utils.ItemUtils;
import be.openmc.ggames.utils.TextUtils;
import be.openmc.ggames.utils.TractorUtils;

public class Refill {
	
	protected @Nullable Player player;
	public Refill(Main plugin) {
    }

    public void execute(Player player, String[] args) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        final ItemStack item = player.getInventory().getItemInMainHand();

        if (!ItemUtils.isHoldingTractor(player));

        final String licensePlate = TractorUtils.getLicensePlate(item);
        config.setFuel(licensePlate, 100.0);

        player.sendMessage(TextUtils.colorize("&aLe tracteur a été rempli"));
    }
}

