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
package com.sgcraft.sgtitles.commands;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.sgcraft.sgtitles.PlayerManager;
import com.sgcraft.sgtitles.SGTitles;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;
import com.sgcraft.sgtitles.utils.Backup;

public class TitleCommands implements CommandExecutor {
	public static SGTitles plugin;
	public static String cmdName = null;
	public static String cmdDesc = null;
	public static String cmdUsage = null;
	public static String pluginName;
	public static String pluginVersion;
	
	public TitleCommands (SGTitles instance) {
		plugin = instance;
		PluginDescriptionFile pdf = plugin.getDescription();
		pluginName = pdf.getName();
		pluginVersion = pdf.getVersion();
	}

	private boolean checkPerm(Player player, String perm) {
		if (player.isOp() || player.hasPermission(pluginName.toLowerCase() + "." + perm.toLowerCase()))
			return true;
		else {
			sendErr(player,"You do not have permission.");
			return false;
		}
	}
	
	private boolean checkPerm(Player player, String perm, Boolean noerror) {
		if (player.isOp() || player.hasPermission(pluginName.toLowerCase() + "." + perm.toLowerCase()))
			return true;
		else {
			return false;
		}
	}
	
	private void displayCmdHelp(Player player) {
		player.sendMessage("§5[§6 " + pluginName + " Help §5]§f--------------------------");
		player.sendMessage("§f| §bCommand: §3" + cmdName);
		player.sendMessage("§f| §bDescription: §3" + cmdDesc);
		player.sendMessage("§f| §bUsage: §3" + cmdUsage);
		player.sendMessage("§5[§6 " + pluginName + " Help §5]§f--------------------------");
	}
	
	private void displayHelp(Player player) {
		player.sendMessage("§5[§6 " + pluginName + " Help §5]§f--------------------------");
		player.sendMessage("§f| §b/title list §3[user]");
		player.sendMessage("§f| §b/title set §3<title>");
		player.sendMessage("§f| §b/title color §3[color]");
		if (checkPerm(player,"admin.clear",true))
			player.sendMessage("§f| §b/title clear §3<prefix/suffix/color> [user]");
		else
			player.sendMessage("§f| §b/title clear §3<prefix/suffix/color>");
		if (checkPerm(player,"admin.add",true))
			player.sendMessage("§f| §b/title add §3<user> <title>");
		if (checkPerm(player,"admin.revoke",true))
			player.sendMessage("§f| §b/title revoke §3<user> <title>");
		if (checkPerm(player,"admin.create",true))
			player.sendMessage("§f| §b/title create §3<name> <data> <prefix/suffix>");
		if (checkPerm(player,"admin.modify",true))
			player.sendMessage("§f| §b/title modify §3<name> <data> [prefix/suffix]");
		if (checkPerm(player,"admin.delete",true))
			player.sendMessage("§f| §b/title delete §3<name>");
		player.sendMessage("§5[§6 " + pluginName + " Help §5]§f--------------------------");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof ColouredConsoleSender)
        {
			sender.sendMessage("Sorry, you can not run these commands from the console!");
            return true;
        }
		
        if (args.length == 0 || args[0].equals("?")) {
        	displayHelp((Player) sender);
        	return true;
        }
        
        if (titleCommand("add",args,sender,"admin.add")) {
        	cmdName = "Add";
        	cmdDesc = "Adds a title to a player";
        	cmdUsage = "/title add <user> <title>";
        	if (args.length != 3 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	Player target = Bukkit.getServer().getPlayer(args[1]);
        	if (target == null) {
        		sendErr((Player) sender, "That player does not exist or is offline!");
        		return true;
        	}
        	
        	if (PlayerManager.giveTitle(target, args[2])) {
        		sendMsg((Player) sender,target.getName() + " has been granted the title: " + args[2]);
        		sendMsg(target,"You have been granted the title: " + args[2]);
        	} else {
        		sendErr((Player) sender,"That title does not exist or that player already has it!");
        	}
        	
        	return true;
        }
        
        if (titleCommand("set",args,sender)) {
        	cmdName = "Set";
        	cmdDesc = "Sets a title as active";
        	cmdUsage = "/title set <title>";
        	if (args.length != 2 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	Player player = Bukkit.getServer().getPlayer(sender.getName());
        	if (PlayerManager.applyTitle(player, args[1].toLowerCase())) {
        		sendMsg((Player) sender,"Title has been applied!");
        	} else {
        		sendErr((Player) sender,"Title does not exist or you do not own it");
        	}
        	
        	return true;
        }
        
        if (titleCommand("create",args,sender,"admin.create")) {
        	cmdName = "Create";
        	cmdDesc = "Creates a new title";
        	cmdUsage = "/title create <name> <data> <prefix/suffix>";
        	if (args.length != 4 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	if (!args[3].equalsIgnoreCase("prefix") && !args[3].equalsIgnoreCase("suffix")) {
        		sendErr((Player) sender,"The title must be a prefix or suffix!");
        		return true;
        	}
        	
        	if (TitleManager.get(args[1]) != null) {
        		sendErr((Player) sender,"A title with that name already exists");
        	} else {
        		TitleManager.addTitle(args[1], args[2], args[3]);
        		sendMsg((Player) sender,"The title " + args[1] + " has been created!");
        	}
        	return true;
        	
        }
        
        if (titleCommand("modify",args,sender,"admin.modify")) {
        	cmdName = "Modify";
        	cmdDesc = "Modifies an existing title";
        	cmdUsage = "/title modify <name> <data> [prefix/suffix]";
        	if (args.length < 3 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	if (args.length == 4 && !args[3].equalsIgnoreCase("prefix") && !args[3].equalsIgnoreCase("suffix")) {
        		sendErr((Player) sender,"The title must be a prefix or suffix!");
        		return true;
        	}
        	
        	Title title = TitleManager.get(args[1]);
        	if (title != null) {
        		if (args.length == 4)
        			TitleManager.updateTitle(title, args[2], args[3]);
        		else
        			TitleManager.updateTitle(title, args[2], title.getPos());
        		sendMsg((Player) sender,"Title successfully updated!");
        	} else {
        		sendErr((Player) sender,"That title does not exist");
        	}
        	
        	
        	return true;
        }
        
        if (titleCommand("delete",args,sender,"admin.delete")) {
        	cmdName = "Delete";
        	cmdDesc = "Deletes a title completely";
        	cmdUsage = "/title delete <name>";
        	if (args.length != 2 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	Title title = TitleManager.get(args[1]);
        	if (title != null) {
        		TitleManager.removeTitle(title);
        		sendMsg((Player) sender,"Title succesfully deleted!");
        	} else {
        		sendErr((Player) sender,"That title does not exist");
        	}
        	
        	return true;
        }
        
        if (titleCommand("revoke",args,sender,"admin.revoke")) {
        	cmdName = "Revoke";
        	cmdDesc = "Removes a title from a player";
        	cmdUsage = "/title revoke <name> <title>";
        	if (args.length != 3 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	Player target = Bukkit.getServer().getPlayer(args[1]);
        	if (target == null) {
        		sendErr((Player) sender, "That player does not exist or is offline!");
        		return true;
        	}
        	
        	if (PlayerManager.revokeTitle(target,args[2])) {
        		sendMsg((Player) sender,"Title revoked from " + args[1]);
        		return true;
        	}
        	
        	return true;
        }
        
        if (titleCommand("clear",args,sender)) {
        	cmdName = "Clear";
        	cmdDesc = "Clears an active title or title color";
        	if (checkPerm((Player) sender,"admin.clear",true))
        		cmdUsage = "/title clear <prefix/suffix/color> [user]";
        	else
        		cmdUsage = "/title clear <prefix/suffix/color>";
        	if (args.length < 2 || args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	Player target;
        	Boolean self = true;
        	if (args.length > 2) {
        		if (!checkPerm((Player) sender,"admin.clear",true)) {
        			sendErr((Player) sender,"You do not have permission to clear other users");
        			return true;
        		}
        		target = Bukkit.getServer().getPlayer(args[2]);
        		self = false;
        	} else {
        		target = Bukkit.getServer().getPlayer(sender.getName());
        	}
        	
        	if (target == null) {
        		sendErr((Player) sender, "That player does not exist or is offline!");
        		return true;
        	}
        	
        	if (args[1].equalsIgnoreCase("prefix")) {
        		PlayerManager.clearActive(target, "prefix");
        	} else if (args[1].equalsIgnoreCase("suffix")) {
        		PlayerManager.clearActive(target, "suffix");
        	} else if (args[1].equalsIgnoreCase("color")) {
        		PlayerManager.clearActive(target, "color");
        	} else {
        		sendErr((Player) sender, "You need to enter either prefix/suffix/color!");
        		return true;
        	}
        	
        	PlayerManager.refreshTitle(target);
        	
        	if (self == true)
        		sendMsg((Player) sender,"Your " + args[1].toUpperCase() + " has been cleared!");
        	else {
        		sendMsg((Player) sender,"You have cleared " + target.getName() + "'s " + args[1].toUpperCase());
        		sendMsg(target,"Your " + args[1].toUpperCase() + " has been cleared!");
        	}
        	return true;
        }
        
        if (titleCommand("list",args,sender)) {
        	cmdName = "List";
        	cmdDesc = "Lists all available titles to a player";
        	cmdUsage = "/title list [user]";
        	if (args.length > 1 && args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
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
        		sendErr((Player) sender, "That player does not exist or is offline!");
        		return true;
        	}
        	
        	List<Title> titles = PlayerManager.getTitles(target);
        	if (self == true)
        		sender.sendMessage("§5[§6 Your Titles §5]§f--------------------------");
        	else
        		sender.sendMessage("§5[§6 " + target.getName() + "'s Titles §5]§f--------------------------");
        	for (Title title : titles) {
        		sender.sendMessage("§f| §bName: §3" + title.getName() + " §bType: §3" + title.getPos().toUpperCase() + " §bTitle: §f" + TitleManager.replaceColors(title.getData()));
        		total++;
        	}
        	if (total == 0)
        		sender.sendMessage("§f| §bNo titles");
        	if (self == true)
        		sender.sendMessage("§5[§6 Your Titles §5]§f--------------------------");
        	else
        		sender.sendMessage("§5[§6 " + target.getName() + "'s Titles §5]§f--------------------------");
        	return true;
        }
        
        if (titleCommand("color",args,sender)) {
        	cmdName = "Color";
        	cmdDesc = "List available colors or sets current title color";
        	cmdUsage = "/title color [color]";
        	if (args.length > 1 && args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	if (args.length == 1) {
        		for (ChatColor color : SGTitles.getAllColors()) {
        			if (sender.isOp() || sender.hasPermission("sgtitles.color." + color.name().toLowerCase())) {
        				sender.sendMessage(color.toString() + color.name());
        			}
        		}
        	} else {
        		if (sender.isOp() || sender.hasPermission("sgtitles.color." + args[1].toLowerCase())) {
        			PlayerManager.setColor((Player) sender, args[1].toUpperCase());
        			sendMsg((Player) sender,"Your color has been changed!");
        		}
        	}
        	return true;
        }
        
        if (titleCommand("import",args,sender,"admin.import")) {
        	cmdName = "Import";
        	cmdDesc = "Imports titles from import.yml";
        	cmdUsage = "/title import";
        	if (args.length > 1 && args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	Backup.importTitles(plugin.getDataFolder());
        	sendMsg((Player) sender,"Import success!");
        	return true;
        }
        
        if (titleCommand("export",args,sender,"admin.export")) {
        	cmdName = "Export";
        	cmdDesc = "Exports all titles to backup.yml";
        	cmdUsage = "/title export";
        	if (args.length > 1 && args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	try {
				Backup.exportTitles(plugin.getDataFolder());
			} catch (IOException e) {
				e.printStackTrace();
			}
        	sendMsg((Player) sender,"Export success!");
        	return true;
        }
        
        if (titleCommand("reload",args,sender,"admin.reload")) {
        	cmdName = "Reload";
        	cmdDesc = "Reloads config.yml";
        	cmdUsage = "/title reload";
        	if (args.length > 1 && args[1].equalsIgnoreCase("?")) {
        		displayCmdHelp((Player) sender);
        		return true;
        	}
        	
        	plugin.reload();
        	sendMsg((Player) sender,"Config reloaded!");
        	
        	return true;
        }
        
        if (titleCommand("fulllist",args,sender) && (args.length == 1)) {
        	for (Title title : SGTitles.TitleList.values()) {
        		sender.sendMessage("[DEBUG] Title: " + title.getName() + " Data: " + TitleManager.replaceColors(title.getData()) + " Position:" + title.getPos() + ":");
        	}
        	return true;
        }
        
        displayHelp((Player) sender);
        
        return true;
	}
	
	private void sendErr(Player player, String msg) {
		msg = "§c" + msg;
		sendMsg(player,msg);
	}
	
	private void sendMsg(Player player, String msg) {
		player.sendMessage("§5[§6" + pluginName + "§5] §f" + msg);
	}
	
	private boolean titleCommand(String label,String[] args, CommandSender sender) {
		if (args[0].equalsIgnoreCase(label) && checkPerm((Player) sender, label))
			return true;
		else
			return false;
	}
	
	private boolean titleCommand(String label,String[] args, CommandSender sender, String perm) {
		if (args[0].equalsIgnoreCase(label) && checkPerm((Player) sender, perm))
			return true;
		else
			return false;
	}
}
