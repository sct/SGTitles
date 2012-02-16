package com.sgcraft.sgtitles;

import java.util.HashMap;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.*;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		logger.info("[" + pdf.getName() + "] is now disabled!");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = this.getDescription();
		// Create Config
		config = getConfig();
        config.options().copyDefaults(true);
		saveConfig();
		// Connect to SQLite DB
		sql = new SQLite(logger, "[SGTitles]", "titles", getDataFolder().getPath());
		// Create tables if they dont exist
		createTables();
		addCommands();
		startListeners();
		setupPermissions();
		TitleManager.loadAllTitles();
		logger.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " is now enabled!");
	}
	
	private void addCommands() {
		getCommand("title").setExecutor(new TitleCommands(this));
	}
	
	public void startListeners() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	private void createTables() {
		// Create Titles Table
		sql.createTable("CREATE TABLE if not exists titles (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, data TEXT NOT NULL, position TEXT NOT NULL);");
		
		// Player/Title Association Table
		sql.createTable("CREATE TABLE if not exists player_titles (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT NOT NULL, title_name INTEGER NOT NULL)");
		
		// Active Title Database
		sql.createTable("CREATE TABLE if not exists active_titles (player_name TEXT NOT NULL, title_prefix TEXT, title_suffix TEXT)");
	}
	
	private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
}
