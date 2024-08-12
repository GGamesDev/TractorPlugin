package be.openmc.ggames;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import be.openmc.ggames.commands.Fuel;
import be.openmc.ggames.commands.GiveTractor;
import be.openmc.ggames.commands.Mode;
import be.openmc.ggames.commands.Refill;
import be.openmc.ggames.commands.SetOwner;
import be.openmc.ggames.config.Config;
import be.openmc.ggames.listeners.JoinListener;
import be.openmc.ggames.listeners.TractorClickListener;
import be.openmc.ggames.listeners.TractorLeaveListener;
import be.openmc.ggames.listeners.TractorPlaceListener;
import be.openmc.ggames.tractor.PacketHandler;

public class Main extends JavaPlugin {
    public static Main instance;
    private Fuel fuel;
    private GiveTractor givetractor;
    private Mode mode;
    private Refill refill;
    private SetOwner setowner;
    private Config config;

    @Override
    public void onEnable() {

        instance = this;

        logInfo("Plugin has been loaded!");
        
        registerListener(new TractorPlaceListener());
        registerListener(new TractorClickListener());
        registerListener(new TractorLeaveListener());
        registerListener(new JoinListener());
        fuel = new Fuel(this);
        givetractor = new GiveTractor(this);
        mode = new Mode(this);
        refill = new Refill(this);
        setowner = new SetOwner(this);
        LoopModule();
        
        saveDefaultConfig();
        
        config = new Config(this);
        
    }
    
    public Config getConfigInstance() {
        return config;
    }
    
    @Override
    public void onLoad(){
    }

    @Override
    public void onDisable(){
    }
    
    public void LoopModule() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.isInsideVehicle()) {
                p.kickPlayer("&cTu ne peux pas t'assoeir pas dans un véhicule pendant que le rechargement est en cours");
            }
            PacketHandler.movement(p);
        }
    }
    
    public static String getFileAsString() {
        return String.valueOf(Main.instance.getFile());
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public static void logInfo(String text){
        instance.getLogger().info(text);
    }

    public static void logWarning(String text){
        instance.getLogger().warning(text);
    }

    public static void logSevere(String text){
        instance.getLogger().severe(text);
    }

    public static void schedulerRun(Runnable task){
        Bukkit.getScheduler().runTask(instance, task);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (label.equalsIgnoreCase("fuel")) {
            	fuel.execute(player, args);
                return true;
            } else if (label.equalsIgnoreCase("givetractor")) {
            	givetractor.execute(player, args);
                return true;
            } else if (label.equalsIgnoreCase("mode")) {
            	mode.execute(player, args);
                return true;
            } else if (label.equalsIgnoreCase("refill")) {
            	refill.execute(player, args);
                return true;
            } else if (label.equalsIgnoreCase("setowner")) {
            	setowner.execute(player, args);
                return true;
            }
            
         }
         return false;
     }
}
