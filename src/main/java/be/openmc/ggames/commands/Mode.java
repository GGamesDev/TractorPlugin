package be.openmc.ggames.commands;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.utils.TextUtils;
import be.openmc.ggames.utils.TractorUtils;

public class Mode {
	
	protected @Nullable Player player;
	public Mode(Main plugin) {
    }

	public void execute(Player player, String[] args) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
    	ItemStack item = player.getInventory().getItemInMainHand();
        if (args.length != 1) {
        	player.sendMessage(TextUtils.colorize("&cUtilises /mode private/public"));
        }

        String publicOrPrivate = args[0];

        if (publicOrPrivate != null) {
            String licensePlate = TractorUtils.getLicensePlate(item);

            if (publicOrPrivate.equalsIgnoreCase("public")) {
            	player.sendMessage(TextUtils.colorize("&aRendu public"));
            	config.setIsOpen(licensePlate, true);
            	config.isOpen(licensePlate);
            } else if (publicOrPrivate.equalsIgnoreCase("private")) {
            	player.sendMessage(TextUtils.colorize("&aRendu privé"));
            	config.setIsOpen(licensePlate, false);
            } else {
            	player.sendMessage(TextUtils.colorize("&cUtilises /mode private/public"));
            }
        }
    }

}
