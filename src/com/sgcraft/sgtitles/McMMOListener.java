package com.sgcraft.sgtitles;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.McMMOPlayerLevelUpEvent;

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
		plugin.logger.info("[DEBUG] MCMMO EVENT! - Player: " + player.getName() + " Skill: " + skill.toString() + " new level: " + profile.getSkillLevel(skill));
	}
}
