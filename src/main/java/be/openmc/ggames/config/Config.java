package be.openmc.ggames.config;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private final FileConfiguration config;
    private final JavaPlugin plugin;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public boolean keyExists(String key) {
        return config.contains(key);
    }
    
    public void newKey(String key, boolean isOpen, double fuel, double seeds, String seedsType, double bonnemeal, UUID owner) {
        if (!keyExists(key)) {
            config.createSection(key);
            config.set(key + ".owner", owner.toString());
            config.set(key + ".isOpen", isOpen);
            config.set(key + ".fuel", fuel);
            config.set(key + ".seeds", seeds);
            config.set(key + ".seedsType", seedsType);
            config.set(key + ".bonnemeal", bonnemeal);

            saveConfig();
        }
    }

    public Map<String, Object> getObjectConfig(String key) {
        if (keyExists(key)) {
            return (Map<String, Object>) config.getConfigurationSection(key).getValues(false);
        }
        return null;
    }

    public String getOwner(String license) {
        return (String) config.getConfigurationSection(license).get("owner");
    }
    
    public Player GetOwnerName(String license) {
    	return Bukkit.getPlayer(getOwner(license));
    }

    public boolean isOpen(String license) {
        return (boolean) config.getConfigurationSection(license).get("isOpen");
    }

    public double getFuel(String license) {
        return (double) config.getConfigurationSection(license).get("fuel");
    }

    public double getSeeds(String license) {
        return (double) config.getConfigurationSection(license).get("seeds");
    }
    
    public String getSeedsType(String license) {
        return (String) config.getConfigurationSection(license).get("seedsType");
    }
    
    public double getBonnemeal(String license) {
        return (double) config.getConfigurationSection(license).get("bonnemeal");
    }

    public void setOwner(String license, UUID owner) {
        config.getConfigurationSection(license).set("owner", owner);
        saveConfig();
    }

    public void setIsOpen(String license, boolean isOpen) {
        config.getConfigurationSection(license).set("isOpen", isOpen);
        saveConfig();
    }

    public void setFuel(String license, double fuel) {
        config.getConfigurationSection(license).set("fuel", fuel);
        saveConfig();
    }

    public void setSeeds(String license, double seeds) {
        config.getConfigurationSection(license).set("seeds", seeds);
        saveConfig();
    }
    
    public void setSeedsType(String license, String seedsType) {
        config.getConfigurationSection(license).set("seedsType", seedsType);
        saveConfig();
    }
    
    public void setBonnemeal(String license, double bonnemeal) {
        config.getConfigurationSection(license).set("bonnemeal", bonnemeal);
        saveConfig();
    }

    private void saveConfig() {
        plugin.saveConfig();
    }
    
    public static String generateKey() {
        Random random = new Random();
        return randomString(random, 2) + randomString(random, 2) + "-" +
               randomString(random, 2) + randomString(random, 2) + "-" +
               randomString(random, 2) + randomString(random, 2);
    }

    private static String randomString(Random random, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
    
}