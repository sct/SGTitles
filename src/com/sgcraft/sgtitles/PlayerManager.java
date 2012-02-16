package com.sgcraft.sgtitles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class PlayerManager {
	public static HashMap<String, Title> Suffix = new HashMap<String, Title>();
	public static HashMap<String, Title> Prefix = new HashMap<String, Title>();
	
	public static Boolean checkTitle(Player player, Title title) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(id) AS counted FROM player_titles WHERE player_name='" + player.getName() + "' AND title_name='" + title.getName() + "'");
			int counted = rs.getInt("counted");
			rs.close();
			if (counted > 0)
				return true;
		} catch (SQLException e) {
			// Do error stuff
		}
		return false;
	}
	
	public static Boolean applyTitle(Player player, String name) {
		Title title = TitleManager.get(name);
		if (title != null && checkTitle(player,title)) {
			setActive(player,title);
			player.setDisplayName(formatTitle(player));
			return true;
		}
		return false;
	}
	
	public static String formatTitle(Player player) {
		String oldName = player.getName();
		String newName = player.getName();
		if (Prefix.containsKey(oldName)) {
			Title pTitle = Prefix.get(oldName);
			newName = pTitle.getData() + newName;
		}
		
		if (Suffix.containsKey(oldName)) {
			Title sTitle = Suffix.get(oldName);
			newName = newName + sTitle.getData();
		}
		
		return newName;
	}
	
	public static Boolean giveTitle(Player player, String name) {
		Title title = TitleManager.get(name);
		String pName = player.getName();
		if (!pName.isEmpty() && title != null && !checkTitle(player,title)) {
			SGTitles.sql.query("INSERT INTO player_titles (player_name,title_name) VALUES ('" + pName + "','" + title.getName() + "')");
			return true;
		}
		
		return false;
	}
	
	public static void setActive(Player player, Title title) {
		//make sure the player has an SQL record
		createRecord(player);
		
		if (title.isPrefix()) {
			SGTitles.sql.query("UPDATE active_titles SET title_prefix='" + title.getName() + "' WHERE player_name='" + player.getName() + "'");
			Prefix.put(player.getName(), title);
		} else {
			SGTitles.sql.query("UPDATE active_titles SET title_suffix='" + title.getName() + "' WHERE player_name='" + player.getName() + "'");
			Suffix.put(player.getName(), title);
		}
		
	}
	
	public static void createRecord(Player player) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(player_name) AS counted FROM active_titles");
			int count = rs.getInt("counted");
			rs.close();
			if (count == 0) {
				SGTitles.sql.query("INSERT INTO active_titles (player_name) VALUES ('" + player.getName() + "')");
			}
		} catch (SQLException e) {
			// Do exception stuff
		}
	}
}
