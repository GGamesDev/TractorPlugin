package be.openmc.ggames.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import be.openmc.ggames.Main;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.tractor.TractorData;

import java.util.HashMap;

public class BossBarUtils {

    public static HashMap<String, BossBar> Fuelbar = new HashMap<>();
    public static HashMap<String, BossBar> Seedsbar = new HashMap<>();
    public static HashMap<String, BossBar> Bonnemealbar = new HashMap<>();
    
    private static void updateBossBarColor(BossBar bar, double value) {
        if (value < 30) {
            bar.setColor(BarColor.RED);
        } else if (value < 60) {
            bar.setColor(BarColor.YELLOW);
        } else {
            bar.setColor(BarColor.GREEN);
        }
    }

    public static void setBossBarFuelValue(double counter, String licensePlate) {
        BossBar bar = Fuelbar.get(licensePlate);
        bar.setProgress(counter);
        bar.setTitle(Math.round(counter * 100.0D) + "% " + TextUtils.colorize("Carburant"));

        Double fuel = TractorData.fuel.get(licensePlate);
        updateBossBarColor(bar, fuel);
    }

    public static void setBossBarSeedsValue(double counter, String licensePlate) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        BossBar bar = Seedsbar.get(licensePlate);
        bar.setProgress(counter);
        bar.setTitle(Math.round(counter * 100.0D) + "% " + config.getSeedsType(licensePlate));

        Double seeds = TractorData.seeds.get(licensePlate);
        updateBossBarColor(bar, seeds);
    }
    
    public static void setBossBarBonnemealValue(double counter, String licensePlate) {
        BossBar bar = Bonnemealbar.get(licensePlate);
        bar.setProgress(counter);
        bar.setTitle(Math.round(counter * 100.0D) + "% " + TextUtils.colorize("Bonnemeal"));

        Double bonnemeal = TractorData.bonnemeal.get(licensePlate);
        updateBossBarColor(bar, bonnemeal);
    }

    public static void removeBossBar(Player player, String licensePlate) {
        BossBar fuelBar = Fuelbar.get(licensePlate);
        if (fuelBar != null) {
            fuelBar.removePlayer(player);
        }
        
        BossBar seedsBar = Seedsbar.get(licensePlate);
        if (seedsBar != null) {
            seedsBar.removePlayer(player);
        }
        
        BossBar bonnemealBar = Bonnemealbar.get(licensePlate);
        if (bonnemealBar != null) {
        	bonnemealBar.removePlayer(player);
        }
    }

    public static void addBossBar(Player player, String licensePlate) {
    	Main plugin = (Main) Bukkit.getPluginManager().getPlugin("Tractor");
    	Config config = plugin.getConfigInstance();
        double fuel = config.getFuel(licensePlate);
        String fuelString = String.valueOf(fuel);
        BossBar fuelBar = Bukkit.createBossBar(Math.round(Double.parseDouble(fuelString)) + "% " + TextUtils.colorize("Carburant"), BarColor.GREEN, BarStyle.SOLID);
        updateBossBarColor(fuelBar, fuel);
        Fuelbar.put(licensePlate, fuelBar);
        fuelBar.addPlayer(player);
        
        double seeds = config.getSeeds(licensePlate)/10;
        String seedsString = String.valueOf(seeds);
        BossBar seedsBar = Bukkit.createBossBar(Math.round(Double.parseDouble(seedsString)) + "% " + config.getSeedsType(licensePlate), BarColor.GREEN, BarStyle.SOLID);
        updateBossBarColor(seedsBar, seeds);
        Seedsbar.put(licensePlate, seedsBar);
        seedsBar.addPlayer(player);
        
        double bonnemeal = config.getSeeds(licensePlate);
        String bonnemealString = String.valueOf(bonnemeal);
        BossBar bonnemealBar = Bukkit.createBossBar(Math.round(Double.parseDouble(bonnemealString)) + "% " + TextUtils.colorize("Bonnemeal"), BarColor.GREEN, BarStyle.SOLID);
        updateBossBarColor(bonnemealBar, bonnemeal);
        Bonnemealbar.put(licensePlate, bonnemealBar);
        bonnemealBar.addPlayer(player);
    }
}

