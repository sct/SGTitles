package com.sgcraft.sgtitles;

import java.util.HashMap;
import java.util.Map;

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
		
		if (profile.getSkillLevel(skill).equals(levelReq)) {
			// See if title already exists
			Title title = TitleManager.get(titleName.toLowerCase());
			if (title == null) {
				String data = format.replace("#skill#", titleName);
				TitleManager.addTitle(titleName.toLowerCase(), data, position);
				title = TitleManager.get(titleName.toLowerCase());
			}
			PlayerManager.giveTitle(player, titleName.toLowerCase());
			player.sendMessage("§5[§6SGTitles§5] §fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
		}
	}
	
	private String getSkillTitle(String skillName) {
		Map<String,String> skills = new HashMap<String, String>();
		String titleName = null;
		
		skills.put("ACROBATICS", "Ninja");
		skills.put("ARCHERY", "Marksman");
		skills.put("AXES", "Viking");
		skills.put("MINING", "Miner");
		skills.put("EXCAVATION","Excavator");
		skills.put("FISHING", "Angler");
		skills.put("HERBALISM","Farmer");
		skills.put("REPAIR","Blacksmith");
		skills.put("SWORDS","Fencer");
		skills.put("TAMING","Trainer");
		skills.put("UNARMED","Berseker");
		skills.put("WOODCUTTING","Lumberjack");
		
		
		if (skills.containsKey(skillName))
			titleName = skills.get(skillName);
			
		
		return titleName;
	}
}
