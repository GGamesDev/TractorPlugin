package be.openmc.ggames.utils;

import static be.openmc.ggames.utils.TractorUtils.isInsideTractor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import be.openmc.ggames.tractor.Tractor;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemUtils {

    public static ItemStack getTractorItem(String licensePlate){
        if (licensePlate == null) licensePlate = generateLicencePlate();
        ItemStack tractor = (new ItemFactory(Material.DIAMOND_HOE))
                .setCustomModelData(1)
                .setName(TextUtils.colorize("&6Tracteur"))
                .setNBT("tractor.kenteken", licensePlate)
                .setLore("&a", "&a" + licensePlate, "&a")
                .setUnbreakable(true)
                .toItemStack();
        	return tractor;
    }

    private static String generateLicencePlate() {
        String plate = String.format("%s-%s-%s", RandomStringUtils.random(2, true, false), RandomStringUtils.random(2, true, false), RandomStringUtils.random(2, true, false));
        return plate.toUpperCase();
    }
    
    public static boolean checkPermission(String permission, Player player) {
        if (player.hasPermission(permission)) {
            return true;
        }

        player.sendMessage(TextUtils.colorize("&aTu n'as pas les permissions"));

        return false;
    }
    
    
    public static ItemStack jerrycanItem(int maxFuel, int currentFuel) {
        ItemStack is = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(5).setNBT("tractor.fuelval", "" + currentFuel).setNBT("tractor.fuelsize", "" + maxFuel).toItemStack();
        ItemMeta im = is.getItemMeta();
        List<String> itemlore = new ArrayList<>();
        itemlore.add(TextUtils.colorize("&8"));
        itemlore.add(TextUtils.colorize("&7Jerrycan &e" + currentFuel + "&7/&e" + maxFuel + "&7l"));
        assert im != null;
        im.setLore(itemlore);
        im.setUnbreakable(true);
        im.setDisplayName(TextUtils.colorize("&6Jerrycan " + maxFuel + "L"));
        is.setItemMeta(im);
        return is;
    }
    public static Tractor getTractor(Player player){
        if (player == null) return null;

        if (isInsideTractor(player) && TractorUtils.getTractor(TractorUtils.getLicensePlate(player.getVehicle())).isOwner(player))
            return TractorUtils.getTractor(TractorUtils.getLicensePlate(player.getVehicle()));


        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.hasItemMeta() && (new NBTItem(item)).hasKey("tractor.kenteken"))
            return TractorUtils.getTractor(TractorUtils.getLicensePlate(item));

        player.sendMessage("Aucun véhicule n'a été utilisé dans le commandement (vous devez vous asseoir dans un tracteur ou en tenir un)");
        return null;
    }
    public static boolean isHoldingTractor(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta() || !(new NBTItem(item)).hasKey("tractor.kenteken")) {
            player.sendMessage(TextUtils.colorize("&cTu n'as pas de tracteur dans votre main"));
            return false;
        }
        return true;
    }

}
