package me.Josvth.Trade.Handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Josvth.Trade.Trade;
import me.Josvth.Trade.Handlers.LanguageHandler.Message;

public class CommandHandler implements CommandExecutor {
	
	Trade plugin;
	
	public CommandHandler(Trade instance){
		plugin = instance;
		plugin.getCommand("trade").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		
		if(args.length == 0) return false;
		
		if(args.length == 1){
			
			// /trade ignore
			if(args[0].equalsIgnoreCase("ignore")){
				if(!(sender instanceof Player)){ 
					sender.sendMessage("You must be a player to use this command!");
					return true;
				}
				Player player = (Player)sender;
				if(plugin.ignoring.contains(player)){
					plugin.ignoring.remove(player);
					plugin.languageHandler.sendMessage(player, Message.REQUEST_IGNORING_DISABLE, "", "", "");
				}else{
					plugin.ignoring.add(player);
					plugin.languageHandler.sendMessage(player, Message.REQUEST_IGNORING_ENABLE, "", "", "");
				}
				return true;
			}
			
			// /trade <player>
			if(!(sender instanceof Player)){ 
				sender.sendMessage("You must be a player to use this command!");
				return true;
			}
			Player player = (Player)sender;
			Player requested = plugin.getServer().getPlayer(args[0]);
			if(requested == null){
				plugin.languageHandler.sendMessage(player, Message.REQUEST_PLAYER_NOT_FOUND, args[0], "", "");
			}else{
				plugin.requestPlayer(player, requested);
			}
			
		}
		
		if(args.length == 2){
			
			// /trade accept <player>
			if (args[0].equalsIgnoreCase("accept")){
				if(!(sender instanceof Player)){ 
					sender.sendMessage("You must be a player to use this command!");
					return true;
				}
				Player player = (Player)sender;
				Player requester = plugin.getServer().getPlayer(args[1]);
				if(requester == null){
					plugin.languageHandler.sendMessage(player, Message.REQUEST_PLAYER_NOT_FOUND, args[1], "", "");
				}else if(plugin.pendingRequests.containsKey(requester)){
					plugin.acceptRequest(requester, player);
				}else{
					plugin.languageHandler.sendMessage(player, Message.REQUEST_PLAYER_NOT_REQUESTED, requester.getName(), "", "");
				}
				return true;
			}
			
			// /trade refuse <player>
			if (args[0].equalsIgnoreCase("refuse")){
				if(!(sender instanceof Player)){ 
					sender.sendMessage("You must be a player to use this command!");
					return true;
				}
				Player player = (Player)sender;
				Player requester = plugin.getServer().getPlayer(args[1]);
				if(requester == null){
					plugin.languageHandler.sendMessage(player, Message.REQUEST_PLAYER_NOT_FOUND, args[1], "", "");
				}else if(plugin.pendingRequests.containsKey(requester)){
					plugin.refuseRequest(requester, player);
				}else{
					plugin.languageHandler.sendMessage(player, Message.REQUEST_PLAYER_NOT_REQUESTED, requester.getName(), "", "");
				}
				return true;
			}
		}
		
		return false;
	}
}
