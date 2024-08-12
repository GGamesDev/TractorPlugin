package be.openmc.ggames.tractor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.utils.ItemFactory;
import be.openmc.ggames.utils.TractorUtils;

import static be.openmc.ggames.utils.TractorUtils.isInsideTractor;

import java.util.*;

public class Tractor {
    private String licensePlate;
    private UUID owner;
    
    public Tractor(String licensePlate, boolean isPublic, double fuel, double seeds, String seedsType, double bonnemeal, UUID owner) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
    	this.licensePlate = licensePlate;
        this.owner = owner;
    	config.newKey(licensePlate, isPublic, fuel, seeds, seedsType, bonnemeal, owner);
    }

    public @Nullable Double getCurrentSpeed(){
        if (TractorData.speed.get(this.getLicensePlate()) == null) return null;
        return TractorData.speed.get(licensePlate) * 20;
    }

    public @Nullable Double getCurrentFuel(){
        return TractorData.fuel.get(licensePlate);
    }
    
    public @Nullable Double getCurrentSeeds(){
        return TractorData.seeds.get(licensePlate);
    }
    
    public @Nullable Double getCurrentSeedsType(){
        return TractorData.seedstype.get(licensePlate);
    }
    
    public @Nullable Double getCurrentBonnemeal(){
        return TractorData.bonnemeal.get(licensePlate);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getOwnerName(String licensePlate) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        return Bukkit.getOfflinePlayer(config.getOwner(licensePlate)).getName();
    }

    public boolean isOwner(OfflinePlayer player){
        return this.owner.equals(player.getUniqueId());
    }
    
    public static boolean isSeeder(ArmorStand armorStand) {
    	ItemStack isSeeder = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(7).toItemStack();
    	if (armorStand.getEquipment().getHelmet().isSimilar(isSeeder)) {
    		return true;
    	}
		return false;
    }
    
    public static boolean isTiller(ArmorStand armorStand) {
    	ItemStack isTiller = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(6).toItemStack();
    	if (armorStand.getEquipment().getHelmet().isSimilar(isTiller) ) {
    		return true;
    	}
		return false;
    }
    
    public static boolean isFertilizer(ArmorStand armorStand) {
    	ItemStack isFertilizer = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(8).toItemStack();
    	if (armorStand.getEquipment().getHelmet().isSimilar(isFertilizer) ) {
    		return true;
    	}
		return false;
    }


    public void saveSeats(){
    	TractorData.mainx.put("TRACTOR_MAINSEAT_" + licensePlate, TractorUtils.getLocationArmorStand("seats", "x"));
    	TractorData.mainy.put("TRACTOR_MAINSEAT_" + licensePlate, TractorUtils.getLocationArmorStand("seats", "y"));
    	TractorData.mainz.put("TRACTOR_MAINSEAT_" + licensePlate, TractorUtils.getLocationArmorStand("seats", "z"));
    }
    public enum Seat {
        DRIVER;
        public static Seat getSeat(Player player) throws IllegalStateException {
            if (!isInsideTractor(player)) throw new IllegalStateException("Player is not seated in a tractor!");

            final String tractorName = player.getVehicle().getCustomName();
            if (tractorName.contains("MAINSEAT")) return DRIVER;
            else return null;
        }
    }
}
