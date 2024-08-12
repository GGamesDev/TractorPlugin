package be.openmc.ggames.tractor;

import org.bukkit.entity.ArmorStand;
import java.util.HashMap;
import java.util.Map;

public class TractorData {
    public static HashMap<String, Double> speed = new HashMap<>();
    public static HashMap<String, Double> speedhigh = new HashMap<>();
    public static HashMap<String, Integer> maxheight = new HashMap<>();
    public static HashMap<String, Double> mainx = new HashMap<>();
    public static HashMap<String, Double> mainy = new HashMap<>();
    public static HashMap<String, Double> mainz = new HashMap<>();
    public static HashMap<String, Integer> seatsize = new HashMap<>();
    public static HashMap<String, Double> seatx = new HashMap<>();
    public static HashMap<String, Double> seaty = new HashMap<>();
    public static HashMap<String, Double> seatz = new HashMap<>();
    public static HashMap<String, Double> frontx = new HashMap<>();
    public static HashMap<String, Double> fronty = new HashMap<>();
    public static HashMap<String, Double> frontz = new HashMap<>();
    public static HashMap<String, Double> backx = new HashMap<>();
    public static HashMap<String, Double> backy = new HashMap<>();
    public static HashMap<String, Double> backz = new HashMap<>();
    public static HashMap<String, Double> volantx = new HashMap<>();
    public static HashMap<String, Double> volanty = new HashMap<>();
    public static HashMap<String, Double> volantz = new HashMap<>();
    public static HashMap<String, Double> trailerx = new HashMap<>();
    public static HashMap<String, Double> trailery = new HashMap<>();
    public static HashMap<String, Double> trailerz = new HashMap<>();
    public static HashMap<String, Double> fuel = new HashMap<>();
    public static HashMap<String, Double> seeds = new HashMap<>();
    public static HashMap<String, Double> seedstype = new HashMap<>();
    public static HashMap<String, Double> bonnemeal = new HashMap<>();
    public static HashMap<String, ArmorStand> autostand = new HashMap<>();
    public static HashMap<String, ArmorStand> autostand2 = new HashMap<>();
    public static Map<String, Long> lastUsage = new HashMap<>();
}
