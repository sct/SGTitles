package com.sgcraft.sgtitles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.dev.heroes.api.HeroChangeLevelEvent;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class HeroesListener implements Listener {
	public static SGTitles plugin;
	
	public HeroesListener (SGTitles instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onHeroChangeLevel(HeroChangeLevelEvent event) {
		Hero hero = event.getHero();
		HeroClass heroClass = event.getHeroClass();
		String format = SGTitles.config.getString("heroes.default-format");
		String position = SGTitles.config.getString("heroes.default-position");
		
		if (hero.isMaster(heroClass)) {
			Player player = hero.getPlayer();
			String titleName = heroClass.getName();
			Title title = TitleManager.get(titleName.toLowerCase());
			if (title == null) {
				String data = format.replace("#class#", titleName);
				TitleManager.addTitle(titleName.toLowerCase(), data, position);
				title = TitleManager.get(titleName.toLowerCase());
			}
			PlayerManager.giveTitle(player, titleName.toLowerCase());
			player.sendMessage("§5[§6SGTitles§5] §fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
			if (SGTitles.config.getBoolean("heroes.broadcast"))
				Bukkit.getServer().broadcastMessage("§5[§6SGTitles§5] §6" + player.getName() + "§3 unlocked the title §b" + titleName + "!");
		}
	}
	
	
}
