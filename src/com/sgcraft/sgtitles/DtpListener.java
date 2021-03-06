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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.KillStreakEventDTP;

import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class DtpListener implements Listener {
	public static SGTitles plugin;
	
	public DtpListener (SGTitles instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onKillStreak(KillStreakEventDTP event) {
		ConfigurationSection dSec = SGTitles.config.getConfigurationSection("deathtp");
		Player player = event.getPlayer();
		Boolean isMultiKill = event.isMultiKill();
		Integer kills = event.getKills();
		Integer count = 0;
		String titleName;
		String cSec = "kill-streak";
		String format = dSec.getString("default-format");
		String position = dSec.getString("default-position");
		
		if (isMultiKill)
			cSec = "multi-kill";
		
		plugin.logger.info("[DEBUG] " + cSec.toUpperCase() + " DETECTED! Player: " + player.getName() + " Kills: " + kills);
		
		for (String tempName : dSec.getStringList(cSec)) {
			String[] sPrt = tempName.split(":");
			titleName = sPrt[0];
			count = Integer.valueOf(sPrt[1]);
			if (count.equals(kills)) {
				Title title = TitleManager.get(titleName.toLowerCase());
				if (title == null) {
					String data = format.replace("#title#", titleName);
					TitleManager.addTitle(titleName.toLowerCase(), data, position);
					title = TitleManager.get(titleName.toLowerCase());
				}
				if (!PlayerManager.checkTitle(player, title)) {
					PlayerManager.giveTitle(player, titleName.toLowerCase());
					player.sendMessage("�5[�6SGTitles�5] �fCongratulatons! You have been granted the title: " + titleName.toLowerCase());
					if (SGTitles.config.getBoolean("deathtp.broadcast"))
						Bukkit.getServer().broadcastMessage("�5[�6SGTitles�5] �6" + player.getName() + "�3 unlocked the title �b" + titleName + "!");
				}
			}
		}
	}
}
