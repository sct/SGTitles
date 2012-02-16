package com.sgcraft.sgtitles;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
	public static SGTitles plugin;
	
	public PlayerListener (SGTitles instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerManager.loadRecord(player);
	}

}
