package com.sgcraft.sgtitles.utils;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sgcraft.sgtitles.title.TitleManager;

public class Backup {
	public static FileConfiguration importConfig;
	static File importConfigFile = null;
	public static Logger logger = Logger.getLogger("Minecraft");
	
	public static void importTitles(File datafolder) {
		String tData;
		String tPos;
		
		importConfigFile = new File(datafolder,"import.yml");
		if (importConfigFile.exists()) {
			importConfig = YamlConfiguration.loadConfiguration(importConfigFile);
			ConfigurationSection titles = importConfig.getConfigurationSection("titles");
			for (String title : titles.getKeys(false)) {
				logger.info("[SGTitles] [DEBUG] Title from import: " + title);
				tData = titles.getString(title + ".data");
				tPos = titles.getString(title + ".position");
				TitleManager.addTitle(title.toLowerCase(), tData, tPos.toLowerCase(),true);
			}
		} else {
			logger.info("[SGTitles] Failed import. Cannot find import.yml");
		}
	}
}
