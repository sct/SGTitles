package com.sgcraft.sgtitles.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

import com.sgcraft.sgtitles.PlayerManager;
import com.sgcraft.sgtitles.SGTitles;
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
        
        return false;
	}
	
	private boolean titleCommand(String label,String[] args, CommandSender sender) {
		if (args[0].equalsIgnoreCase(label) && (sender.isOp() || sender.hasPermission("sgtitles." + label)))
			return true;
		else
			return false;
	}
}
