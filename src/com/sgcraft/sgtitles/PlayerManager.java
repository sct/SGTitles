package com.sgcraft.sgtitles;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class PlayerManager {
	
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
		String displayname;
		if (title != null && checkTitle(player,title)) {
			if (title.isPrefix()) {
				displayname = title.getData() + player.getName();
			} else {
				displayname = title.getData() + player.getName();
			}
			player.setDisplayName(displayname);
			return true;
		}
		return false;
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
}
