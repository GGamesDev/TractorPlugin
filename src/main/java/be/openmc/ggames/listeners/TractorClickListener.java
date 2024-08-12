package be.openmc.ggames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.tractor.Tractor;
import be.openmc.ggames.tractor.TractorMovement;
import be.openmc.ggames.utils.ItemFactory;
import be.openmc.ggames.utils.TextUtils;
import be.openmc.ggames.utils.TractorUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class TractorClickListener implements Listener {
    private Map<String, Long> lastUsage = new HashMap<>();

    private Entity entity;
    private String license;
    protected Event event;
    protected @Nullable Player player;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();

        this.event = event;
        player = event.getPlayer();
        entity = event.getRightClicked();
        long lastUsed = 0L;
        if (!TractorUtils.isTractor(entity)) return;
        event.setCancelled(true);
        if (!entity.getCustomName().startsWith("TRACTOR")) return;

        if (lastUsage.containsKey(player.getName())) lastUsed = (lastUsage.get(player.getName())).longValue();
        if (System.currentTimeMillis() - lastUsed >= 500) lastUsage.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
        else return;
        license = TractorUtils.getLicensePlate(entity);
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        String trailerName = "TRACTOR_TRAILER_" + license;
		ItemStack isSeeder = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(7).toItemStack();
		ItemStack isTiller = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(6).toItemStack();
		ItemStack isFertilizer = new ItemFactory(Material.DIAMOND_HOE).setAmount(1).setCustomModelData(8).toItemStack();
		if (player.isSneaking()) {
        	if(TractorMovement.isCropItems(itemInHand.getType())) {
    			double maxAmount = itemInHand.getAmount();
        		if(config.getSeedsType(license) == itemInHand.getType().toString()) {
        			if(config.getSeeds(license) + maxAmount > 1000) {
        				double aRetirer = 1000 - config.getSeeds(license);
        				player.getInventory().removeItem(new ItemStack(itemInHand.getType(), (int) aRetirer));
        				config.setSeeds(license, 1000);
        			} else if (config.getSeeds(license) + maxAmount <= 1000) {
        				config.setSeeds(license, config.getSeeds(license) + maxAmount);
        				player.getInventory().removeItem(new ItemStack(itemInHand.getType(), (int) itemInHand.getAmount()));
        			}
        		} else if(config.getSeedsType(license) != itemInHand.getType().toString()) {
        			Material material = Material.matchMaterial(config.getSeedsType(license));
        			player.getInventory().addItem(new ItemStack(material, (int) config.getSeeds(license)));
        			config.setSeeds(license, 0);
        			config.setSeedsType(license, itemInHand.getType().toString());
        			if(config.getSeeds(license) + maxAmount > 1000) {
        				double aRetirer = 1000 - config.getSeeds(license);
        				player.getInventory().removeItem(new ItemStack(itemInHand.getType(), (int) aRetirer));
        				config.setSeeds(license, 1000);
        			} else if (config.getSeeds(license) + maxAmount <= 1000) {
        				config.setSeeds(license, config.getSeeds(license) + maxAmount);
        				player.getInventory().removeItem(new ItemStack(itemInHand.getType(), (int) itemInHand.getAmount()));
        			}
        		}
        	}
        	
        	if(itemInHand.getType() == Material.BONE_MEAL) {
    			double maxAmount = itemInHand.getAmount();
        		if(config.getBonnemeal(license) + maxAmount > 1000) {
        			double aRetirer = 1000 - config.getBonnemeal(license);
        			player.getInventory().removeItem(new ItemStack(itemInHand.getType(), (int) aRetirer));
        			config.setBonnemeal(license, 1000);
        		} else if (config.getBonnemeal(license) + maxAmount <= 1000) {
        			config.setBonnemeal(license, config.getBonnemeal(license) + maxAmount);
        			player.getInventory().removeItem(new ItemStack(itemInHand.getType(), (int) itemInHand.getAmount()));
        		}
        	}
            
    	for (ArmorStand armorStand : Bukkit.getWorlds().get(0).getEntitiesByClass(ArmorStand.class)) {
    		if (trailerName.equals(armorStand.getCustomName())) {
    			if (Tractor.isSeeder(armorStand)) {
        			armorStand.getEquipment().setHelmet(null);
        			player.getInventory().addItem(isSeeder);
        			pickup();
    			}
    			if (Tractor.isTiller(armorStand)) {
        			armorStand.getEquipment().setHelmet(null);
        			player.getInventory().addItem(isTiller);
        			pickup();
    			}
    			if (Tractor.isFertilizer(armorStand)) {
        			armorStand.getEquipment().setHelmet(null);
        			player.getInventory().addItem(isFertilizer);
        			pickup();
    			}
    		}
    		
    	}
            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
            	if (itemInHand.getItemMeta().getCustomModelData() == isSeeder.getItemMeta().getCustomModelData()) {
            		for (ArmorStand armorStand : Bukkit.getWorlds().get(0).getEntitiesByClass(ArmorStand.class)) {
            			if (trailerName.equals(armorStand.getCustomName())) {
            				armorStand.getEquipment().setHelmet(isSeeder);
            				player.getInventory().remove(itemInHand);
            			}
            		}
            	}

            	if (itemInHand.getItemMeta().getCustomModelData() == isTiller.getItemMeta().getCustomModelData()) {
            		for (ArmorStand armorStand : Bukkit.getWorlds().get(0).getEntitiesByClass(ArmorStand.class)) {
            			if (trailerName.equals(armorStand.getCustomName())) {
            				armorStand.getEquipment().setHelmet(isTiller);
            				player.getInventory().remove(itemInHand);
            			}
            		}
            	}
            	if (itemInHand.getItemMeta().getCustomModelData() == isFertilizer.getItemMeta().getCustomModelData()) {
            		for (ArmorStand armorStand : Bukkit.getWorlds().get(0).getEntitiesByClass(ArmorStand.class)) {
            			if (trailerName.equals(armorStand.getCustomName())) {
            				armorStand.getEquipment().setHelmet(isFertilizer);
            				player.getInventory().remove(itemInHand);
            			}
            		}
            	}
            	ItemMeta itemMeta = itemInHand.getItemMeta();
            	if (itemMeta != null) {
            		List<String> lore = itemMeta.getLore();
            		if (lore != null && !lore.isEmpty()) {
        				String secondLine = lore.get(1);
            			if (secondLine.contains("Jerrycan")) {
            				remplirTracteur(player, license);
            			}
            		}
            	}
            } else pickup();
            return;
        }
        enter();
    }

    @NotNull
	public static ItemStack getHelmet(String name) {
		for (ArmorStand armorStand : Bukkit.getWorlds().get(0).getEntitiesByClass(ArmorStand.class)) {
			if (name.equals(armorStand.getCustomName())) {
				@NotNull ItemStack helmet = armorStand.getHelmet();
				return helmet;
			}
		}
		return null;
    }
    private void pickup(){
    	Tractor tractor = TractorUtils.getTractor(license);
        if (tractor == null) return;
        TractorUtils.pickupTractor(license, player);
    }
    public void remplirTracteur(Player player, String license) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
    	ItemStack item = player.getInventory().getItemInMainHand();

        NBTItem nbt = new NBTItem(item);

        final String jerryCanFuel = nbt.getString("tractor.fuelval");
        final String jerryCanSize = nbt.getString("tractor.fuelsize");

        double fuel = Double.parseDouble(jerryCanFuel);
        double toAdd = 100.0 - config.getFuel(license);
        double toDel = fuel - toAdd;
        int toDelRaccourci = (int) Math.floor(toDel);
        String toDelString = Integer.toString(toDelRaccourci);
        player.sendMessage(toDelString);
        if (config.getFuel(license) < 100) {
        	if (fuel > 0) {
        		if (fuel >= toAdd) {
        			ItemMeta meta = item.getItemMeta();
        			List<String> itemlore = new ArrayList<>();
        			itemlore.add(TextUtils.colorize("&8"));
        			itemlore.add(TextUtils.colorize("&7Jerrycan &e" + toDelString + "&7/&e" + jerryCanSize + "&7l"));
        			meta.setLore(itemlore);
        			item.setItemMeta(meta);
        			nbt.setString("tractor.fuelval", toDelString);
        			nbt.mergeNBT(item);
        			config.setFuel(license, 100);
        		} else {
        			ItemMeta meta = item.getItemMeta();
        			List<String> itemlore = new ArrayList<>();
        			itemlore.add(TextUtils.colorize("&8"));
        			itemlore.add(TextUtils.colorize("&7Jerrycan &e0&7/&e" + jerryCanSize + "&7l"));
        			meta.setLore(itemlore);
        			item.setItemMeta(meta);
        			nbt.setString("tractor.fuelval", "0");
        			nbt.mergeNBT(item);
        			config.setFuel(license, config.getFuel(license) + fuel);
        		}
        	} else player.sendMessage("Le jerrycan n'a plus d'essence");
        } else player.sendMessage("Le tracteur est déja rempli");
    }
    private void enter(){
    	Tractor tractor = TractorUtils.getTractor(license);
        if (tractor == null) return;
        TractorUtils.enterTractor(license, player);
    }
}
