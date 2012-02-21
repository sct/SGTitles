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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.McMMOPlayerLevelUpEvent;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class McMMOListener implements Listener {
	public static SGTitles plugin;
	
	public McMMOListener (SGTitles instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMcMMOPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
		Player player = event.getPlayer();
		SkillType skill = event.getSkill();
		PlayerProfile profile = SGTitles.mcPlugin.getPlayerProfile(player);
		String titleName = getSkillTitle(skill.toString());
		
		Integer levelReq = SGTitles.config.getInt("mcmmo.level-required");
		String format = SGTitles.config.getString("mcmmo.default-format");
		String position = SGTitles.config.getString("mcmmo.default-position");
		
		if (profile.getSkillLevel(skill) >= levelReq) {
			// See if title already exists
			Title title = TitleManager.get(titleName.toLowerCase());
			if (title == null) {
				String data = format.replace("#skill#", titleName);
				TitleManager.addTitle(titleName.toLowerCase(), data, position);
				title = TitleManager.get(titleName.toLowerCase());
			}
			if (!PlayerManager.checkTitle(player, title)) {
				PlayerManager.giveTitle(player, titleName.toLowerCase());
				player.sendMessage("§5[§6SGTitles§5] §fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
				if (SGTitles.config.getBoolean("mcmmo.broadcast"))
					Bukkit.getServer().broadcastMessage("§5[§6SGTitles§5] §6" + player.getName() + "§3 unlocked the title §b" + titleName + "!");
			}
		}
	}
	
	private String getSkillTitle(String skillName) {
		Map<String,String> skills = new HashMap<String, String>();
		String titleName = null;
		
		skills.put("ACROBATICS", getSkl("Acrobatics"));
		skills.put("ARCHERY", getSkl("Archery"));
		skills.put("AXES", getSkl("Axes"));
		skills.put("MINING", getSkl("Mining"));
		skills.put("EXCAVATION", getSkl("Excavation"));
		skills.put("FISHING", getSkl("Fishing"));
		skills.put("HERBALISM",getSkl("Herbalism"));
		skills.put("REPAIR", getSkl("Repair"));
		skills.put("SWORDS", getSkl("Swords"));
		skills.put("TAMING", getSkl("Taming"));
		skills.put("UNARMED", getSkl("Unarmed"));
		skills.put("WOODCUTTING", getSkl("Woodcutting"));
		
		
		if (skills.containsKey(skillName))
			titleName = skills.get(skillName);
			
		
		return titleName;
	}
	
	private String getSkl(String skillName) {
		String configSec = "mcmmo.skill-titles.";
		return SGTitles.config.getString(configSec + skillName);
	}
}
