package be.openmc.ggames.commands;

import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.tractor.Tractor;
import be.openmc.ggames.utils.ItemUtils;
import be.openmc.ggames.utils.TextUtils;
import be.openmc.ggames.utils.TractorUtils;

public class SetOwner {
	
	protected @Nullable Player player;
	
    public SetOwner(Main plugin) {
    }

    public void execute(Player player, String[] args) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        ItemStack item = player.getInventory().getItemInMainHand();

        boolean playerSetOwner = (boolean) false;

        if (!playerSetOwner && !ItemUtils.checkPermission("tractor.setowner", player)) {
        }

        if (!ItemUtils.isHoldingTractor(player));

        if (args.length != 1) {
        	player.sendMessage(TextUtils.colorize("&cUtilises /setowner pseudo"));
        }

        String licensePlate = TractorUtils.getLicensePlate(item);

        if (config.keyExists(licensePlate)) {
        	player.sendMessage(TextUtils.colorize("&aLe tracteur n'a pas été trouvé"));
        }

        Player argPlayer = Bukkit.getPlayer(args[0]);
        if (argPlayer == null) {
        	player.sendMessage(TextUtils.colorize("&aLe joueur n'a pas été trouvé"));
        }

        Tractor tractor = TractorUtils.getTractor(licensePlate);
        assert tractor != null;

        if ((playerSetOwner || !player.hasPermission("tractor.setowner")) && !tractor.isOwner(player)) {
        	player.sendMessage(TextUtils.colorize("&aCe n'est pas ton tracteur"));
        }
        config.setOwner(licensePlate, argPlayer.getUniqueId());

        player.sendMessage(TextUtils.colorize("&aEnregistré"));
    }
}
