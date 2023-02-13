package pw.arx.tycoonplugin;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import pw.arx.tycoonplugin.listeners.*;
import pw.arx.tycoonplugin.managers.FileManager;
import pw.arx.tycoonplugin.utils.BlockUtils;
import pw.arx.tycoonplugin.utils.TimerUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class Tycoon extends JavaPlugin {

	private static Tycoon plugin;
	public static FileManager fileManager;

	public static String MAIN_COMMAND = "tycoon";
	public static HashMap<UUID, Boolean> ADMINS = new HashMap<UUID, Boolean>();
	
	// tool java creator
    public static HashMap<UUID, Boolean> TOOLS_ENABLED = new HashMap<UUID, Boolean>();
    public static HashMap<UUID, Location> SELECTION_LOCATIONS = new HashMap<UUID, Location>();

    // tool java builder
    public static HashMap<UUID, Boolean> BUILDER_TOOL_ENABLED = new HashMap<UUID, Boolean>();
    public static HashMap<UUID, Location> BUILDER_TOOL_LOCATIONS = new HashMap<UUID, Location>();
    
    // still flying players
    public static HashMap<UUID, Boolean> STILL_FLYING_PLAYERS = new HashMap<UUID, Boolean>();
    public static HashMap<UUID, Boolean> STILL_FLYING_PLAYERS_MOVING = new HashMap<UUID, Boolean>();

    private static Economy econ = null;
    private HolographicDisplaysAPI hologramsApi = null;

    private static final Logger log = Logger.getLogger("Minecraft");

    // size of tycoons and select box.
    public static Integer SEL_OFFSET = 14; 
    public static Double SEL_RADIUS = SEL_OFFSET*2.85;
    public static Vector HOLOGRAM_OFFSET = new Vector(0.5,1.45,0.5);
    
    public static Tycoon getPlugin() {
		return plugin;
	}
    
    @Override
    public void onEnable() {
		plugin = getPlugin(Tycoon.class);
		PluginManager pm = getServer().getPluginManager();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            this.setEnabled(false);
            return;
        }

        if (!pm.isPluginEnabled("HolographicDisplays")) {
            log.severe("*** HolographicDisplays is not installed or not enabled. ***");
            this.setEnabled(false);
            return;
        }

        hologramsApi = HolographicDisplaysAPI.get(this);
		
		fileManager = new FileManager(this);
		fileManager.loadConfigs(); // normal, keep this
		
        // Listeners
		this.getCommand(MAIN_COMMAND).setExecutor(new CommandListener());
		pm.registerEvents(new PlayerInteractListener(), this);
		pm.registerEvents(new BlockPlaceListener(), this);
		pm.registerEvents(new BlockBreakListener(), this);
		pm.registerEvents(new InventoryClickListener(), this);
		
//		ADMINS.put(UUID.fromString("1504b9e3-4bbf-41db-84a3-f5b5a2018699"), true);

        BlockUtils.loadHolograms();
		TimerUtil.DoTimer();
    }
    
    public static Economy getEconomy() {
        return econ;
    }

    public HolographicDisplaysAPI getHologramsApi() {
        return hologramsApi;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            log.severe("Do you have an economy plugin installed alongside vault?");
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onDisable() {
    	fileManager = null;
    	plugin = null;
    }
}
