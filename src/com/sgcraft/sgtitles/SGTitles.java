package com.sgcraft.sgtitles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.*;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import com.gmail.nossr50.mcMMO;
import com.sgcraft.sgtitles.commands.TitleCommands;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class SGTitles extends JavaPlugin {
	public static SGTitles plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration config;
	public static SQLite sql;
	public static HashMap<String, Title> TitleList = new HashMap<String, Title>();  
	public static Permission permission = null;
	public static mcMMO mcPlugin;
	public static DeathTpPlus dtpPlugin;
	public static boolean spoutEnabled = false;
	
	public static List<ChatColor> getAllColors() {
		List<ChatColor> colors = new ArrayList<ChatColor>();
		for (ChatColor color : ChatColor.values()) {
			colors.add(color);
		}
		return colors;
	}
	
	private void addCommands() {
		getCommand("title").setExecutor(new TitleCommands(this));
	}
	
	private boolean checkMcMMO() {
		Plugin mmo = getServer().getPluginManager().getPlugin("mcMMO");
		if (mmo != null) {
			mcPlugin = (mcMMO) mmo;
			return true;
		} else {
			mcPlugin = null;
			return false;
		}
	}
	
	private boolean checkDtp() {
		Plugin dtp = getServer().getPluginManager().getPlugin("DeathTpPlus");
		if (dtp != null) {
			dtpPlugin = (DeathTpPlus) dtp;
			return true;
		} else {
			dtpPlugin = null;
			return false;
		}
	}
	
	private boolean checkSpout() {
		Plugin sPlugin = getServer().getPluginManager().getPlugin("Spout");
		if (sPlugin != null) {
			spoutEnabled = true;
			return true;
		} else
			return false;
	}
	
	private void createTables() {
		// Create Titles Table
		sql.createTable("CREATE TABLE if not exists titles (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, data TEXT NOT NULL, position TEXT NOT NULL);");
		
		// Player/Title Association Table
		sql.createTable("CREATE TABLE if not exists player_titles (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT NOT NULL, title_name INTEGER NOT NULL)");
		
		// Active Title Database
		sql.createTable("CREATE TABLE if not exists active_titles (player_name TEXT NOT NULL, title_prefix TEXT, title_suffix TEXT, title_color TEXT)");
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		logger.info("[" + pdf.getName() + "] is now disabled!");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = this.getDescription();
		config = getConfig();
        config.options().copyDefaults(true);
		saveConfig();
		sql = new SQLite(logger, "[ " + pdf.getName() + "]", "titles", getDataFolder().getPath());
		createTables();
		
		if (checkMcMMO())
			logger.info("[" + pdf.getName() + "] McMMO detected. Loading support...");
		if (checkDtp())
			logger.info("[" + pdf.getName() + "] DeathTpPlus detected. Loading support...");
		if (checkSpout())
			logger.info("[" + pdf.getName() + "] Spout detected. Loading support...");
		
		addCommands();
		startListeners();
		setupPermissions();
		TitleManager.loadAllTitles();
		logger.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " is now enabled!");
	}
	
	private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	private void startListeners() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		if (mcPlugin != null)
			getServer().getPluginManager().registerEvents(new McMMOListener(this), this);
		if (dtpPlugin != null)
			getServer().getPluginManager().registerEvents(new DtpListener(this),this);
	}
}
