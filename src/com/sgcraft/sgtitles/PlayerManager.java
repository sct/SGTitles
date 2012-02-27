/*
 *  SGTitles - Give your users a collection of titles
 *  Copyright (C) 2012  SGCraft
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgcraft.sgtitles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.herocraftonline.dev.heroes.hero.Hero;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class PlayerManager {
	public static HashMap<String, Title> Suffix = new HashMap<String, Title>();
	public static HashMap<String, Title> Prefix = new HashMap<String, Title>();
	public static HashMap<String, ChatColor> Color = new HashMap<String, ChatColor>();
	
	public static Boolean applyTitle(Player player, String name) {
		Title title = TitleManager.get(name);
		if (title != null && checkTitle(player,title)) {
			setActive(player,title);
			refreshTitle(player);
			return true;
		}
		return false;
	}
	
	public static Boolean checkTitle(Player player, Title title) {
		try {
			ResultSet rs = SGTitles.sql.query("SELECT count(id) AS counted FROM player_titles WHERE player_name='" + player.getName() + "' AND title_name='" + title.getName() + "'");
			int counted = rs.getInt("counted");
			rs.close();
			if (counted > 0)
				return true;
			
			// Assign title by group!
			if (SGTitles.config.getBoolean("default.use-permissions")) {
				String[] pGroups = SGTitles.permission.getPlayerGroups((String) null,player.getName());
				
				for (String group : pGroups) {
					for (String tName : SGTitles.config.getStringList("groups." + group)) {
						if (tName.equalsIgnoreCase(title.getName())) {
							return true;
						}
					}
				}
			}
		} catch (SQLException e) {
			// Do error stuff
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
	
	public static void addHeroesTitles(Player player) {
		String format = SGTitles.config.getString("heroes.default-format");
		String position = SGTitles.config.getString("heroes.default-position");
		
		if (SGTitles.hPlugin != null) {
			Hero hero = SGTitles.hPlugin.getHeroManager().getHero(player);
			Set<String> classNames = new HashSet<String>();
			
			if (SGTitles.config.getBoolean("heroes.require-master") == false || (hero.getHeroClass() != null && hero.isMaster(hero.getHeroClass()))) {
				classNames.add(hero.getHeroClass().getName());
			}
			if (SGTitles.config.getBoolean("heroes.require-master") == false || (hero.getSecondClass() != null && hero.isMaster(hero.getSecondClass()))) {
				classNames.add(hero.getSecondClass().getName());
			}
			
			for (String titleName : classNames) {
				Title title = TitleManager.get(titleName);
				if (title == null) {
					String data = format.replace("#class#", titleName);
					TitleManager.addTitle(titleName.toLowerCase(), data, position);
					title = TitleManager.get(titleName.toLowerCase());
				}
				
				if (!checkTitle(player, title)) {
					PlayerManager.giveTitle(player, titleName.toLowerCase());
					player.sendMessage("§5[§6SGTitles§5] §fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
					if (SGTitles.config.getBoolean("heroes.broadcast"))
						Bukkit.getServer().broadcastMessage("§5[§6SGTitles§5] §6" + player.getName() + "§3 unlocked the title §b" + titleName + "!");
				}
			}
		}
	}
	
	public static String formatColor(String pName) {
		ChatColor color = getColor(pName);
		if (color != null)
			pName = color.toString() + pName;
		return pName;
	}
	
	public static String formatTitle(Player player) {
		String oldName = player.getName();
		String newName = formatColor(player.getName());
		String spoutName = formatColor(player.getName());
		String spoutFormat = SGTitles.config.getString("spout.format");
		Boolean spout = SGTitles.spoutEnabled;
		
		spoutName = spoutFormat.replace("#player#", spoutName);
		
		if (Prefix.containsKey(oldName)) {
			Title pTitle = Prefix.get(oldName);
			spoutName = spoutName.replace("#prefix#", pTitle.getData());
			newName = pTitle.getData() + newName;
		} else {
			if (spout == true)
				spoutName = spoutName.replace("#prefix#","");
		}
		
		if (Suffix.containsKey(oldName)) {
			Title sTitle = Suffix.get(oldName);
			spoutName = spoutName.replace("#suffix#",sTitle.getData());
			newName = newName + sTitle.getData();
		} else {
			spoutName = spoutName.replace("#suffix#", "");
		}
		
		if (spout == true) {
			spoutName = spoutName.replace("\\n","\n");
			setSpoutTitle(player,TitleManager.replaceColors(spoutName));
		}
		
		return TitleManager.replaceColors(newName);
	}
	
	public static ChatColor getColor(String pName) {
		ChatColor color = null;
		if (Color.containsKey(pName)) {
			color = Color.get(pName);
		} else {
			if (SGTitles.config.getBoolean("default.color-names-by-default")) {
				color = ChatColor.valueOf(SGTitles.config.getString("default.default-name-color").toUpperCase());
			} else {
				color = null;
			}
		}
		
		return color;
	}
	
	public static List<Title> getTitles(Player player) {
		List<Title> titles = new ArrayList<Title>();
		Title gTitle;
		
		try {
			ResultSet rs = SGTitles.sql.query("SELECT * FROM player_titles WHERE player_name='" + player.getName() + "'");
			while (rs.next()) {
				titles.add(TitleManager.get(rs.getString("title_name")));
			}
			rs.close();
			if (SGTitles.config.getBoolean("default.use-permissions")) {
				String[] pGroups = SGTitles.permission.getPlayerGroups((String) null,player.getName());
				for (String group : pGroups) {
					for (String tName : SGTitles.config.getStringList("groups." + group)) {
						gTitle = TitleManager.get(tName);
						if (gTitle != null && !titles.contains(gTitle))
							titles.add(gTitle);
					}
				}
			}
		} catch (SQLException e) {
			// Catch error here!
		}
		
		return titles;
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
	
	public static void refreshTitle(Player player) {
		player.setDisplayName(formatTitle(player));
	}
	
	public static Boolean revokeTitle(Player player, String name) {
		Title title = TitleManager.get(name);
		String pName = player.getName();
		if (!pName.isEmpty() && title != null) {
			SGTitles.sql.query("DELETE FROM player_titles WHERE player_name='" + pName + "' AND title_name='" + title.getName() + "'");
			if (title.isPrefix() && Prefix.containsKey(pName) && Prefix.get(pName).getName() == title.getName())
				clearActive(player,"prefix");
			else if (title.isSuffix() && Suffix.containsKey(pName) && Suffix.get(pName).getName() == title.getName())
				clearActive(player,"suffix");
			refreshTitle(player);
			return true;
		}
		
		return false;
	}
	
	public static void setActive(Player player, ChatColor color) {
		//make sure the player has an SQL record
		createRecord(player);
		
		SGTitles.sql.query("UPDATE active_titles SET title_color='" + color.name().toUpperCase() + "' WHERE player_name='" + player.getName() + "'");
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
	
	public static void setSpoutTitle(Player player, String title) {
		SpoutPlayer sPlayer = Spout.getServer().getPlayerExact(player.getName());
		sPlayer.setTitle(title);
	}
}
