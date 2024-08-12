package be.openmc.ggames.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.openmc.ggames.Main;
import be.openmc.ggames.utils.ItemUtils;
import be.openmc.ggames.utils.TextUtils;

public class Fuel {

    public Fuel(Main plugin) {
    }

    public void execute(Player player, String[] args) {

        if (args.length != 1) {
        	player.sendMessage(TextUtils.colorize("&cUtilises /fuel litres"));
        }

        String stringNumber = args[0];
        int number = Integer.parseInt(stringNumber);
        ItemStack jerrycan = ItemUtils.jerrycanItem(number, number);
        
        player.getInventory().addItem(jerrycan);
    }
   
}