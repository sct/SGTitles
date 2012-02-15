package com.sgcraft.sgtitles;

import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SGTitles extends JavaPlugin {
	public static SGTitles plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration config;
	public static SQLite sql;
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		logger.info("[" + pdf.getName() + "] is now disabled!");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = this.getDescription();
		// Connect to SQLite DB
		sql = new SQLite(logger, "SGTitles", "titles", getDataFolder().getPath());
		sql.open();
		
		logger.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " is now enabled!");
	}
}
