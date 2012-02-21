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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.dev.heroes.api.ClassChangeEvent;
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
		
		if (SGTitles.config.getBoolean("heroes.require-master") == false || hero.isMaster(heroClass)) {
			Player player = hero.getPlayer();
			String titleName = heroClass.getName();
			Title title = TitleManager.get(titleName.toLowerCase());
			if (title == null) {
				String data = format.replace("#class#", titleName);
				TitleManager.addTitle(titleName.toLowerCase(), data, position);
				title = TitleManager.get(titleName.toLowerCase());
			}
			if (!PlayerManager.checkTitle(player, title)) {
				PlayerManager.giveTitle(player, titleName.toLowerCase());
				player.sendMessage("§5[§6SGTitles§5] §fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
				if (SGTitles.config.getBoolean("heroes.broadcast"))
					Bukkit.getServer().broadcastMessage("§5[§6SGTitles§5] §6" + player.getName() + "§3 unlocked the title §b" + titleName + "!");
			}
		}
	}
	
	@EventHandler()
	public void onClassChange(ClassChangeEvent event) {
		Hero hero = event.getHero();
		HeroClass heroClass = event.getTo();
		String format = SGTitles.config.getString("heroes.default-format");
		String position = SGTitles.config.getString("heroes.default-position");
		
		if (SGTitles.config.getBoolean("heroes.require-master") == false) {
			Player player = hero.getPlayer();
			String titleName = heroClass.getName();
			Title title = TitleManager.get(titleName.toLowerCase());
			if (title == null) {
				String data = format.replace("#class#", titleName);
				TitleManager.addTitle(titleName.toLowerCase(), data, position);
				title = TitleManager.get(titleName.toLowerCase());
			}
			if (!PlayerManager.checkTitle(player, title)) {
				PlayerManager.giveTitle(player, titleName.toLowerCase());
				player.sendMessage("§5[§6SGTitles§5] §fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
				if (SGTitles.config.getBoolean("heroes.broadcast"))
					Bukkit.getServer().broadcastMessage("§5[§6SGTitles§5] §6" + player.getName() + "§3 unlocked the title §b" + titleName + "!");
			}
		}
	}
	
	
}
