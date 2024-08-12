package be.openmc.ggames.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.openmc.ggames.Main;
import be.openmc.ggames.utils.TractorUtils;

public class GiveTractor {
	
	public GiveTractor(Main plugin) {
    }

    public boolean execute(Player player, String[] args) {

        if (args.length != 1) {
            player.sendMessage("&cUtilises /givetractor pseudo");
            return true;
        }

        Player argPlayer = Bukkit.getPlayer(args[0]);

        if (argPlayer == null) {
        	player.sendMessage("&cJoueur non trouvé");
            return true;
        }

        ItemStack car = TractorUtils.createAndGetItem(argPlayer);

        if (car == null){
        	player.sendMessage("&cOn n'a pas trouvé ton tracteur");
            return true;
        }
        argPlayer.getInventory().addItem(car);
        player.sendMessage("&6Tu as donné ton tracteur à &c" + argPlayer.getName());

        return true;
    }
}
