package com.sgcraft.sgtitles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class PlayerManager {
	public static HashMap<String, Title> Suffix = new HashMap<String, Title>();
	public static HashMap<String, Title> Prefix = new HashMap<String, Title>();
	public static HashMap<String, ChatColor> Color = new HashMap<String, ChatColor>();
	
	public static List<Title> getTitles(Player player) {
		List<Title> titles = new ArrayList<Title>();
		
		try {
			ResultSet rs = SGTitles.sql.query("SELECT * FROM player_titles WHERE player_name='" + player.getName() + "'");
			while (rs.next()) {
				titles.add(TitleManager.get(rs.getString("title_name")));
			}
			rs.close();
			for (String tName : SGTitles.config.getStringList("groups." + SGTitles.permission.getPrimaryGroup(player))) {
				titles.add(TitleManager.get(tName));
			}
		} catch (SQLException e) {
			// Catch error here!
		}
		
		return titles;
	}
	
	public static Boolean checkTitle(Player player, Title title) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(id) AS counted FROM player_titles WHERE player_name='" + player.getName() + "' AND title_name='" + title.getName() + "'");
			int counted = rs.getInt("counted");
			rs.close();
			if (counted > 0)
				return true;
			
			// Assign title by group!
			for (String tName : SGTitles.config.getStringList("groups." + SGTitles.permission.getPrimaryGroup(player))) {
				if (tName.equalsIgnoreCase(title.getName())) {
					return true;
				}
			}
		} catch (SQLException e) {
			// Do error stuff
		}
		return false;
	}
	
	public static Boolean applyTitle(Player player, String name) {
		Title title = TitleManager.get(name);
		if (title != null && checkTitle(player,title)) {
			setActive(player,title);
			refreshTitle(player);
			return true;
		}
		return false;
	}
	
	public static void refreshTitle(Player player) {
		setSpoutTitle(player,formatTitle(player,true));
		player.setDisplayName(formatTitle(player,false));
	}
	
	public static String formatTitle(Player player, Boolean spout) {
		String oldName = player.getName();
		String newName = formatColor(player.getName());
		if (Prefix.containsKey(oldName)) {
			Title pTitle = Prefix.get(oldName);
			if (spout == true)
				newName = pTitle.getData() + "\n" + newName;
			else
				newName = pTitle.getData() + newName;
		}
		
		if (Suffix.containsKey(oldName)) {
			Title sTitle = Suffix.get(oldName);
			newName = newName + sTitle.getData();
		}
		
		return TitleManager.replaceColors(newName);
	}
	
	public static String formatColor(String pName) {
		ChatColor color = getColor(pName);
		pName = color.toString() + pName;
		return pName;
	}
	
	public static void setColor(Player player, ChatColor color) {
		Color.put(player.getName(), color);
		setActive(player,color);
		refreshTitle(player);
	}
	
	public static void setColor(Player player, String cName) {
		try {
			ChatColor color = ChatColor.valueOf(cName);
			setColor(player, color);
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	public static ChatColor getColor(String pName) {
		ChatColor color = null;
		if (Color.containsKey(pName)) {
			color = Color.get(pName);
		} else {
			color = ChatColor.valueOf("WHITE");
		}
		
		return color;
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
	
	public static Boolean revokeTitle(Player player, String name) {
		Title title = TitleManager.get(name);
		String pName = player.getName();
		if (!pName.isEmpty() && title != null) {
			SGTitles.sql.query("DELETE FROM player_titles WHERE player_name='" + pName + "' AND title_name='" + title.getName() + "'");
			if (title.isPrefix() && Prefix.get(pName).getName() == title.getName())
				clearActive(player,"prefix");
			else if (title.isSuffix() && Suffix.get(pName).getName() == title.getName())
				clearActive(player,"suffix");
			refreshTitle(player);
			return true;
		}
		
		return false;
	}
	
	public static void clearActive(Player player, String position) {
		String pName = player.getName();
		if (position.equalsIgnoreCase("prefix")) {
			SGTitles.sql.query("UPDATE active_titles SET title_prefix=NULL WHERE player_name='" + player.getName() + "'");
			Prefix.remove(pName);
		} else if (position.equalsIgnoreCase("suffix")) {
			SGTitles.sql.query("UPDATE active_titles SET title_suffix=NULL WHERE player_name='" + player.getName() + "'");
			Suffix.remove(pName);
		} else if (position.equalsIgnoreCase("color")) {
			SGTitles.sql.query("UPDATE active_titles SET title_color=NULL WHERE player_name='" + player.getName() + "'");
			Color.remove(pName);
		}
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
	
	public static void setActive(Player player, ChatColor color) {
		//make sure the player has an SQL record
		createRecord(player);
		
		SGTitles.sql.query("UPDATE active_titles SET title_color='" + color.name().toUpperCase() + "' WHERE player_name='" + player.getName() + "'");
	}
	
	public static void loadRecord(Player player) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(player_name) AS counted,title_prefix,title_suffix,title_color FROM active_titles WHERE player_name='" + player.getName() + "'");
			int count = rs.getInt("counted");
			String pName = rs.getString("title_prefix");
			String sName = rs.getString("title_suffix");
			String cName = rs.getString("title_color");
			if (count > 0) {
				if (pName != null) {
					Prefix.put(player.getName(), SGTitles.TitleList.get(pName));
				}
				
				if (sName != null) {
					Suffix.put(player.getName(), SGTitles.TitleList.get(sName));
				}
				
				if (cName != null) {
					Color.put(player.getName(), ChatColor.valueOf(cName));
				}
				
				refreshTitle(player);
			}
			rs.close();
		} catch (SQLException e) {
			// Do exception stuff
		}
	}
	
	public static void createRecord(Player player) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(player_name) AS counted FROM active_titles WHERE player_name='" + player.getName() + "'");
			int count = rs.getInt("counted");
			rs.close();
			if (count == 0) {
				SGTitles.sql.query("INSERT INTO active_titles (player_name) VALUES ('" + player.getName() + "')");
			}
		} catch (SQLException e) {
			// Do exception stuff
		}
	}
	
	public static void setSpoutTitle(Player player, String title) {
		SpoutPlayer sPlayer = Spout.getServer().getPlayerExact(player.getName());
		sPlayer.setTitle(title);
	}
}
