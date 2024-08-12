package be.openmc.ggames.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.listeners.TractorClickListener;
import be.openmc.ggames.tractor.Tractor;
import be.openmc.ggames.tractor.TractorData;
import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.*;

public final class TractorUtils {
	
	public static void spawnTractor(String licensePlate, Location location) throws IllegalArgumentException {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
		if (!config.keyExists(licensePlate)) throw new IllegalArgumentException("Tractor does not exists.");

        ArmorStand standSkin = location.getWorld().spawn(location, ArmorStand.class);
        standSkin.setVisible(false);
        standSkin.setCustomName("TRACTOR_SKIN_" + licensePlate);
        standSkin.getEquipment().setHelmet(
        ItemUtils.getTractorItem(licensePlate));

        ArmorStand standMain = location.getWorld().spawn(location, ArmorStand.class);
        standMain.setVisible(false);
        standMain.setCustomName("TRACTOR_MAIN_" + licensePlate);

        Location locationMainSeat = new Location(location.getWorld(), location.getX() + getLocationArmorStand("seats", "x"), location.getY() + getLocationArmorStand("seats", "y"), location.getZ() + getLocationArmorStand("seats", "z"));
        ArmorStand standMainSeat = locationMainSeat.getWorld().spawn(locationMainSeat, ArmorStand.class);
        setGravityAndVisible(standMainSeat, "mainseat", licensePlate, null);
        
        //front
        Location locationFront = new Location(location.getWorld(), location.getX() + getLocationArmorStand("front", "z"), location.getY() + getLocationArmorStand("front", "y"), location.getZ() + getLocationArmorStand("front", "x"));
        ArmorStand standFront = locationFront.getWorld().spawn(locationFront, ArmorStand.class);
        ItemStack itemInHandFront = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(4).toItemStack();
        setGravityAndVisible(standFront, "front", licensePlate, itemInHandFront);
        
        //back
        Location locationBack = new Location(location.getWorld(), location.getX() + getLocationArmorStand("back", "z"), location.getY() + getLocationArmorStand("back", "y"), location.getZ() + getLocationArmorStand("back", "x"));
        ArmorStand standBack = locationBack.getWorld().spawn(locationBack, ArmorStand.class);
        standBack.setSmall(true);
        ItemStack itemInHandBack = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(3).toItemStack();
        setGravityAndVisible(standBack, "back", licensePlate, itemInHandBack);
        
        //volant
        Location locationVolant = new Location(location.getWorld(), location.getX() + getLocationArmorStand("volant", "z"), location.getY() + getLocationArmorStand("volant", "y"), location.getZ() + getLocationArmorStand("volant", "x"));
        ArmorStand standVolant = locationVolant.getWorld().spawn(locationVolant, ArmorStand.class);
        ItemStack itemInHand = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(2).toItemStack();
        standVolant.getEquipment().setHelmet(itemInHand);
        standVolant.setHeadPose(new EulerAngle(Math.toRadians(-90), 0, 0));
        setGravityAndVisible(standVolant, "volant", licensePlate, null);
        
        //trailers
        Location locationTrailer = new Location(location.getWorld(), location.getX() + getLocationArmorStand("trailer", "z"), location.getY() + getLocationArmorStand("trailer", "y"), location.getZ() + getLocationArmorStand("trailer", "x"));
        ArmorStand standTrailer = locationTrailer.getWorld().spawn(locationTrailer, ArmorStand.class);
        setGravityAndVisible(standTrailer, "trailer", licensePlate, null);
    }
    
    public static String getLicensePlate(ItemStack item){
        NBTItem nbt = new NBTItem(item);
        return nbt.getString("tractor.kenteken");
    }
    
    public static ItemStack createAndGetItem(Player owner) {
    	ItemStack item = ItemUtils.getTractorItem(null);
        NBTItem nbt = new NBTItem(item);
        final String licensePlate = nbt.getString("tractor.kenteken");
        @SuppressWarnings("unused")
		Tractor tractor = new Tractor(
                licensePlate,
                false,
                100,
                0,
                "WHEAT",
                0,
                owner.getUniqueId()
        );
        return item;
    }

    public static boolean isTractor(Entity entity){
        return entity.getCustomName() != null && entity instanceof ArmorStand && entity.getCustomName().contains("TRACTOR");
    }
    
    public static String getLicensePlate(@Nullable Entity entity){
        if (entity == null) return null;
        final String name = entity.getCustomName();
        if (name.split("_").length > 1) {
            return name.split("_")[2];
        }
        return null;
    }

	public static Tractor getTractor(String licensePlate) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        if (!config.keyExists(licensePlate)) return null;
        Main.logInfo(licensePlate);
    	
        return new Tractor(
                licensePlate,
                config.isOpen(licensePlate),
                config.getFuel(licensePlate),
                config.getSeeds(licensePlate),
                config.getSeedsType(licensePlate),
                config.getBonnemeal(licensePlate),
                UUID.fromString((String) config.getOwner(licensePlate))
        );
    }

    public static boolean isInsideTractor(Player p){
        if (p == null) return false;
        if (!p.isInsideVehicle()) return false;
        return isTractor(p.getVehicle());
    }

    public static void pickupTractor(String license, Player player) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
    	Tractor tractor = getTractor(license);
        if (tractor == null) {
            for (World world : Bukkit.getServer().getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getCustomName() != null && entity.getCustomName().contains(license)) {
                        entity.remove();
                    }
                }
            }
            player.sendMessage("Le tracteur n'est pas trouvé");
            return;
        }

        /*if (config.GetOwnerName(license) == null) {
        	player.sendMessage("Le tracteur n'est pas trouvé");
            Main.logSevere("Could not find the owner of the tractor " + license + "! The tractorData.yml must be malformed!");
            return;
        }*/

        if (tractor.isOwner(player)) {
            for (World world : Bukkit.getServer().getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getCustomName() != null && entity.getCustomName().contains(license)) {
                        ArmorStand test = (ArmorStand) entity;
                        if (test.getCustomName().contains("TRACTOR_SKIN_" + license)) {
                            if (!TextUtils.checkInvFull(player)) {
                                player.getInventory().addItem(test.getHelmet());
                            } else {
                            	player.sendMessage("&aTon inventaire est rempli");
                                return;
                            }
                        }
                        test.remove();
                    }
                }
            }
        } else {
            player.sendMessage("&cSeul le propriétaire peut récupérer ce véhicule. Le propriétaire est &4%" + config.GetOwnerName(license).toString() + "&c");
            return;
        }
    }

	public static void enterTractor(String licensePlate, Player p) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        if (!(TractorData.autostand2.get(licensePlate) == null)) {
            if (!TractorData.autostand2.get(licensePlate).isEmpty()) {
                return;
            }
        }

        Tractor tractor = getTractor(licensePlate);

        if (tractor == null) {
            p.sendMessage("&aLe véhicule n'est pas trouvé");
            return;
        }

        /*if (config.GetOwnerName(licensePlate) == null) {
        	p.sendMessage("&aLe véhicule n'est pas trouvé");
            Main.logSevere("Could not find the owner of tractor " + licensePlate + "! The tractorData.yml must be malformed!");
            return;
        }*/

        if (!config.isOpen(licensePlate) && !tractor.isOwner(p) && !p.hasPermission("tractor.ride")){
            //p.sendMessage("&cTu0 ne peux pas conduire ce tracteur. Demandes à &4" + config.GetOwnerName(licensePlate) + "&c");
        	p.sendMessage("&cTu0 ne peux pas conduire ce tracteur");
        	return;
        }

        for (Entity entity : p.getWorld().getEntities()) {

            if (entity.getCustomName() != null && entity.getCustomName().contains(licensePlate)) {
                ArmorStand tractorAs = (ArmorStand) entity;
                if (!entity.isEmpty()) {
                    return;
                }
                TractorData.fuel.put(licensePlate, config.getFuel(licensePlate));
                TractorData.seeds.put(licensePlate, config.getSeeds(licensePlate));
                TractorData.bonnemeal.put(licensePlate, config.getBonnemeal(licensePlate));
                Location location = new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch());

                if (tractorAs.getCustomName().contains("TRACTOR_SKIN_" + licensePlate)) {
                    basicStandCreator(licensePlate, "SKIN", location, tractorAs.getHelmet(), false);
                    basicStandCreator(licensePlate, "MAIN", location, null, true);
                    tractor.saveSeats();
                    mainSeatStandCreator(licensePlate, location, p, getLocationArmorStand("seats", "x"), getLocationArmorStand("seats", "y"), getLocationArmorStand("seats", "z"));
                    BossBarUtils.addBossBar(p, licensePlate);
                    Location locationfront = new Location(location.getWorld(), location.getX() + getLocationArmorStand("front", "z"), (Double) location.getY() + getLocationArmorStand("front", "y"), location.getZ() + getLocationArmorStand("front", "x"));
                    TractorData.frontx.put("TRACTOR_FRONT_" + licensePlate, getLocationArmorStand("front", "x"));
                    TractorData.fronty.put("TRACTOR_FRONT_" + licensePlate, getLocationArmorStand("front", "y"));
                    TractorData.frontz.put("TRACTOR_FRONT_" + licensePlate, getLocationArmorStand("front", "z"));
                    ArmorStand asfront = locationfront.getWorld().spawn(locationfront, ArmorStand.class);
                    ItemStack itemInHandFront = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(4).toItemStack();
                    setGravityAndVisible(asfront, "front", licensePlate, itemInHandFront);
                    TractorData.autostand.put("TRACTOR_FRONT_" + licensePlate, asfront);

                    Location locationback = new Location(location.getWorld(), location.getX() + getLocationArmorStand("back", "z"), (Double) location.getY() + getLocationArmorStand("back", "y"), location.getZ() + getLocationArmorStand("back", "x"));
                    TractorData.backx.put("TRACTOR_BACK_" + licensePlate, getLocationArmorStand("back", "x"));
                    TractorData.backy.put("TRACTOR_BACK_" + licensePlate, getLocationArmorStand("back", "y"));
                    TractorData.backz.put("TRACTOR_BACK_" + licensePlate, getLocationArmorStand("back", "z"));
                    ArmorStand asback = locationback.getWorld().spawn(locationback, ArmorStand.class);
                    ItemStack itemInback = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(3).toItemStack();
                    setGravityAndVisible(asback, "back", licensePlate, itemInback);
                    asback.setSmall(true);
                    TractorData.autostand.put("TRACTOR_BACK_" + licensePlate, asback);

                  	Location locationvolant = new Location(location.getWorld(), location.getX() + getLocationArmorStand("volant", "z"), (Double) location.getY() + getLocationArmorStand("volant", "y"), location.getZ() + getLocationArmorStand("volant", "x"));
                  	TractorData.volantx.put("TRACTOR_VOLANT_" + licensePlate, getLocationArmorStand("volant", "x"));
                  	TractorData.volanty.put("TRACTOR_VOLANT_" + licensePlate, getLocationArmorStand("volant", "y"));
                    TractorData.volantz.put("TRACTOR_VOLANT_" + licensePlate, getLocationArmorStand("volant", "z"));
                    ArmorStand asvolant = locationvolant.getWorld().spawn(locationvolant, ArmorStand.class);
                    ItemStack itemInvolant = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(2).toItemStack();
                    asvolant.getEquipment().setHelmet(itemInvolant);
                    asvolant.setHeadPose(new EulerAngle(Math.toRadians(-90), 0, 0));
                    setGravityAndVisible(asvolant, "volant", licensePlate, null);
                    TractorData.autostand.put("TRACTOR_VOLANT_" + licensePlate, asvolant);

                 	Location locationtrailer = new Location(location.getWorld(), location.getX() + getLocationArmorStand("trailer", "z"), (Double) location.getY() + getLocationArmorStand("trailer", "y"), location.getZ() + getLocationArmorStand("trailer", "x"));
                 	TractorData.trailerx.put("TRACTOR_TRAILER_" + licensePlate, getLocationArmorStand("trailer", "x"));
                    TractorData.trailery.put("TRACTOR_TRAILER_" + licensePlate, getLocationArmorStand("trailer", "y"));
                    TractorData.trailerz.put("TRACTOR_TRAILER_" + licensePlate, getLocationArmorStand("trailer", "z"));
                    ArmorStand astrailer = locationtrailer.getWorld().spawn(locationtrailer, ArmorStand.class);
                    TractorClickListener.getHelmet("TRACTOR_TRAILER_" + licensePlate);
                    astrailer.getEquipment().setHelmet(TractorClickListener.getHelmet("TRACTOR_TRAILER_" + licensePlate));
                    setGravityAndVisible(astrailer, "trailer", licensePlate, null);
                    TractorData.autostand.put("TRACTOR_TRAILER_" + licensePlate, astrailer);
                }
                tractorAs.remove();
            }
        }
    }

    private static void basicStandCreator(String license, String type, Location location, ItemStack item, Boolean gravity) {
        ArmorStand as = location.getWorld().spawn(location, ArmorStand.class);
        as.setVisible(false);
        as.setCustomName("TRACTOR_" + type + "_" + license);
        as.setHelmet(item);
        as.setGravity(gravity);

        TractorData.autostand.put("TRACTOR_" + type + "_" + license, as);
    }

    private static void mainSeatStandCreator(String license, Location location, Player p, double x, double y, double z) {
        Location location2 = new Location(location.getWorld(), location.getX() + Double.valueOf(z), location.getY() + Double.valueOf(y), location.getZ() + Double.valueOf(z));
        ArmorStand as = location2.getWorld().spawn(location2, ArmorStand.class);
        as.setVisible(false);
        as.setCustomName("TRACTOR_MAINSEAT_" + license);
        as.setGravity(false);

        TractorData.autostand.put("TRACTOR_MAINSEAT_" + license, as);
        TractorData.speed.put(license, 0.0);
        TractorData.speedhigh.put(license, 0.0);
        TractorData.mainx.put("TRACTOR_MAINSEAT_" + license, x);
        TractorData.mainy.put("TRACTOR_MAINSEAT_" + license, y);
        TractorData.mainz.put("TRACTOR_MAINSEAT_" + license, z);

        as.setPassenger(p);
        TractorData.autostand2.put(license, as);
    }

    public static Tractor.Seat getSeat(Player player){
        return Tractor.Seat.getSeat(player);
    }

    public static boolean kickOut(Player player) throws IllegalStateException {
        if (getSeat(player) == null) throw new IllegalStateException("Player is not seated in a tractor!");

        Entity seat = player.getVehicle();

        final String license = getLicensePlate(seat);
        if (seat.removePassenger(player)){
            BossBarUtils.removeBossBar(player, license);
            return turnOff(license);
        }
        return false;
    }

    public static boolean turnOff(/*@NotNull*/ Tractor tractor){
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        final String licensePlate = tractor.getLicensePlate();

        if (TractorData.autostand.get("TRACTOR_MAIN_" + licensePlate) == null) return false;

        double fuel = TractorData.fuel.get(licensePlate);
        double seeds = TractorData.seeds.get(licensePlate);
        double bonnemeal = TractorData.bonnemeal.get(licensePlate);
        config.setFuel(licensePlate, fuel);
        config.setSeeds(licensePlate, seeds);
        config.setBonnemeal(licensePlate, bonnemeal);
        return true;
    }

    public static boolean turnOff(@NotNull String licensePlate){
        if (getTractor(licensePlate) == null) return false;
        return turnOff(getTractor(licensePlate));
    }
    public static void setGravityAndVisible(ArmorStand armorStand, String type, String licensePlate, @Nullable ItemStack itemInHandBack) {
    	if (itemInHandBack == null) {
    		armorStand.setCustomName("TRACTOR_" + type.toUpperCase() + "_" + licensePlate);
    		armorStand.setGravity(false);
    		armorStand.setVisible(false);
    	} else {
    		armorStand.setCustomName("TRACTOR_" + type.toUpperCase() + "_" + licensePlate);
    		armorStand.setGravity(false);
    		armorStand.setVisible(false);
    		armorStand.setLeftArmPose(new EulerAngle(0, Math.toRadians(0), Math.toRadians(270)));
    		armorStand.setRightArmPose(new EulerAngle(0, Math.toRadians(0), Math.toRadians(90)));
    		armorStand.getEquipment().setItemInMainHand(itemInHandBack);
    		armorStand.getEquipment().setItemInOffHand(itemInHandBack);
    		armorStand.setArms(true);
    	}
    }
	public static double getLocationArmorStand(String type, String xyz) {
		if (type == "seats") {
			if (xyz == "x") return 0.0;
			if (xyz == "y") return -0.2;
			if (xyz == "z") return 0.0;
		}
		if (type == "front") {
			if (xyz == "x") return -0.25;
			if (xyz == "y") return -0.415;
			if (xyz == "z") return 0.0;
		}
		if (type == "back") {
			if (xyz == "x") return 2.61;
			if (xyz == "y") return 0.07;
			if (xyz == "z") return 0.0;
		}
		if (type == "volant") {
			if (xyz == "x") return 1.6;
			if (xyz == "y") return 0.6;
			if (xyz == "z") return 0.0;
		}
		if (type == "trailer") {
			if (xyz == "x") return 0.0;
			if (xyz == "y") return -0.3;
			if (xyz == "z") return 0.0;
		}
		return 0.0;
	}
}
