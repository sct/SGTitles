package com.sgcraft.sgtitles.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

import com.sgcraft.sgtitles.PlayerManager;
import com.sgcraft.sgtitles.SGTitles;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;

public class TitleCommands implements CommandExecutor {
	public static SGTitles plugin;
	
	public TitleCommands (SGTitles instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof ColouredConsoleSender)
        {
			sender.sendMessage("Sorry, you can not run these commands from the console!");
            return true;
        }
		
        if (args.length == 0) {
            return false;
        }
        
        if (titleCommand("add",args,sender) && (args.length > 2)) {
        	if (args[1].isEmpty() || args[2].isEmpty()) {
        		sender.sendMessage("[SGTitles] Usage: /titles add [user] <title>");
        		return true;
        	}
        	Player target = Bukkit.getServer().getPlayer(args[1]);
        	if (target == null) {
        		sender.sendMessage("[SGTitles] This player does not exist or is offline!");
        		return true;
        	}
        	
        	if (PlayerManager.giveTitle(target, args[2])) {
        		sender.sendMessage("[SGTitles] " + target.getName() + " now has been given the title: " + args[2]);
        	} else {
        		sender.sendMessage("[SGTitles] That title does not exist!");
        	}
        	
        	return true;
        }
        
        if (titleCommand("set",args,sender) && (args.length > 1)) {
        	Player player = Bukkit.getServer().getPlayer(sender.getName());
        	if (PlayerManager.applyTitle(player, args[1])) {
        		sender.sendMessage("[SGTitles] Title has been applied!");
        	} else {
        		sender.sendMessage("[SGTitles] Title does not exist or you do not own it");
        	}
        	
        	return true;
        }
        
        if (titleCommand("create",args,sender) && (args.length == 4)) {
        	
        	if (!args[3].equalsIgnoreCase("prefix") && !args[3].equalsIgnoreCase("suffix")) {
        		sender.sendMessage("[SGTitles] The title must be a prefix or a suffix!");
        		return true;
        	}
        	
        	if (TitleManager.get(args[1]) != null) {
        		sender.sendMessage("[SGTitles] A title with that name already exists");
        	} else {
        		TitleManager.addTitle(args[1], args[2], args[3]);
        		sender.sendMessage("[SGTitles] The title " + args[1] + " has been added!");
        	}
        	return true;
        	
        }
        
        if (titleCommand("delete",args,sender) && (args.length > 1)) {
        	Title title = TitleManager.get(args[1]);
        	if (title != null) {
        		TitleManager.removeTitle(title);
        		sender.sendMessage("[SGCraft] Title removed!");
        	} else {
        		sender.sendMessage("[SGCraft] Unknown title!");
        	}
        	
        	return true;
        }
        
        if (titleCommand("revoke",args,sender) && (args.length > 2)) {
        	
        	Player target = Bukkit.getServer().getPlayer(args[1]);
        	if (target == null) {
        		sender.sendMessage("[SGTitles] This player does not exist or is offline!");
        		return true;
        	}
        	
        	if (PlayerManager.revokeTitle(target,args[2])) {
        		sender.sendMessage("[SGTitles] Title revoked from " + args[1]);
        		return true;
        	}
        	
        	return false;
        }
        
        if (titleCommand("clear",args,sender) && (args.length >= 2)) {
        	Player target;
        	Boolean self = true;
        	if (args.length > 2) {
        		target = Bukkit.getServer().getPlayer(args[2]);
        		self = false;
        	} else {
        		target = Bukkit.getServer().getPlayer(sender.getName());
        	}
        	
        	if (target == null) {
        		sender.sendMessage("[SGTitles] This player does not exist or is offline!");
        		return true;
        	}
        	
        	if (args[1].equalsIgnoreCase("prefix")) {
        		PlayerManager.clearActive(target, "prefix");
        	} else if (args[1].equalsIgnoreCase("suffix")) {
        		PlayerManager.clearActive(target, "suffix");
        	} else if (args[1].equalsIgnoreCase("color")) {
        		PlayerManager.clearActive(target, "color");
        	} else {
        		sender.sendMessage("[SGTitles] You need to enter either prefix or suffix!");
        		return true;
        	}
        	
        	PlayerManager.refreshTitle(target);
        	
        	if (self == true)
        		sender.sendMessage("[SGTitles] Your " + args[1].toUpperCase() + " has been cleared!");
        	else {
        		sender.sendMessage("[SGTitles] You have cleared " + target.getName() + "'s " + args[1].toUpperCase());
        		target.sendMessage("[SGTitles] Your " + args[1].toUpperCase() + " has been cleared!");
        	}
        	return true;
        }
        
        if (titleCommand("list",args,sender) && (args.length >= 1)) {
        	Player target;
        	Boolean self = true;
        	int total = 0;
        	if (args.length > 1) {
        		target = Bukkit.getServer().getPlayer(args[1]);
        		self = false;
        	} else {
        		target = Bukkit.getServer().getPlayer(sender.getName());
        	}
        	
        	if (target == null) {
        		sender.sendMessage("[SGTitles] This player does not exist or is offline!");
        		return true;
        	}
        	
        	List<Title> titles = PlayerManager.getTitles(target);
        	if (self == true)
        		sender.sendMessage("+ Your Titles");
        	else
        		sender.sendMessage("+ " + target.getName() + "'s Titles");
        	
        	sender.sendMessage("---------------------------------------");
        	for (Title title : titles) {
        		sender.sendMessage("| Name: " + title.getName() + " Type: " + title.getPos().toUpperCase() + " Title: " + TitleManager.replaceColors(title.getData()));
        		total++;
        	}
        	if (total == 0)
        		sender.sendMessage("No titles");
        	sender.sendMessage("---------------------------------------");
        	return true;
        }
        
        if (titleCommand("color",args,sender) && (args.length >= 1)) {
        	if (args.length == 1) {
        		for (ChatColor color : SGTitles.getAllColors()) {
        			if (sender.isOp() || sender.hasPermission("sgtitles.color." + color.name().toLowerCase())) {
        				sender.sendMessage(color.toString() + color.name());
        			}
        		}
        	} else {
        		if (sender.isOp() || sender.hasPermission("sgtitles.color." + args[1].toLowerCase())) {
        			PlayerManager.setColor((Player) sender, args[1].toUpperCase());
        			sender.sendMessage("[SGTitles] Your color has been changed!");
        		}
        	}
        	return true;
        }
        
        if (titleCommand("fulllist",args,sender) && (args.length == 1)) {
        	for (Title title : SGTitles.TitleList.values()) {
        		sender.sendMessage("[DEBUG] Title: " + title.getName() + " Data: " + TitleManager.replaceColors(title.getData()) + " Position:" + title.getPos() + ":");
        	}
        	return true;
        }
        
        return false;
	}
	
	private boolean titleCommand(String label,String[] args, CommandSender sender) {
		if (args[0].equalsIgnoreCase(label) && (sender.isOp() || sender.hasPermission("sgtitles." + label)))
			return true;
		else
			return false;
	}
}
