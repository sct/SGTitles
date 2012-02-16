package com.sgcraft.sgtitles;

import java.sql.ResultSet;

import org.bukkit.entity.Player;

import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class PlayerManager {
	
	public static Boolean checkTitle(Player player, Title title) {
		ResultSet rs = SGTitles.sql.query("SELECT * FROM `player_titles` WHERE player_name='" + player.getName() + "' AND title_id='" + title.getId() + "'");
		if (rs != null) {
			return true;
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
				displayname = player.getName() + title.getData();
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
			SGTitles.sql.query("INSERT INTO player_titles (player_name,title_id) VALUES ('" + pName + "','" + title.getId() + "'");
			return true;
		}
		
		return false;
	}
}
