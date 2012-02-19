package com.sgcraft.sgtitles.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sgcraft.sgtitles.SGTitles;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class Backup {
	public static FileConfiguration importConfig;
	public static FileConfiguration exportConfig;
	static File importConfigFile = null;
	static File exportConfigFile = null;
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
	
	public static void exportTitles(File datafolder) throws IOException {
		
		exportConfigFile = new File(datafolder,"export.yml");
		if (!exportConfigFile.exists())
			exportConfigFile.createNewFile();
		else {
			exportConfigFile.delete();
			exportConfigFile.createNewFile();
		}
		
		exportConfig = YamlConfiguration.loadConfiguration(exportConfigFile);
		ConfigurationSection tSec = exportConfig.createSection("titles");
		for (Title title : SGTitles.TitleList.values()) {
			tSec.set(title.getName(), null);
			tSec.set(title.getName() + ".data", title.getData());
			tSec.set(title.getName() + ".position", title.getPos());
		}
		exportConfig.save(exportConfigFile);
	}
}
