package me.Josvth.Trade.Handlers;

import me.Josvth.Trade.Trade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	
	Trade plugin;
	
	public CommandHandler(Trade instance){
		plugin = instance;
		plugin.getCommand("trade").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length == 1){
			
			if(args[0].equalsIgnoreCase("accept")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					if(plugin.pendingRequests.containsKey(player)){
						plugin.startNewTrade(player, plugin.pendingRequests.get(player));
						plugin.pendingRequests.remove(player);
					}
					return true;
				}else{
					sender.sendMessage("This command can only be used by players!");
					return true;
				}	
			}
				
			if(args[0].equalsIgnoreCase("refuse")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					if(plugin.pendingRequests.containsKey(player)){
						if(plugin.yamlHandler.messagesYaml.isString("request.refused")){
							plugin.pendingRequests.get(player).sendMessage(Trade.formatChatColors(plugin.yamlHandler.messagesYaml.getString("request.refused").replaceAll("&other_player", player.getName())));
						}
						plugin.pendingRequests.remove(player);
					}
					return true;
				}else{
					sender.sendMessage("This command can only be used by players!");
					return true;
				}	
			}
			
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("trade.reload")){
				plugin.yamlHandler.loadYamls();
				sender.sendMessage("Yamls reloaded!");
				return true;
			}
			
			Player p2 = plugin.getServer().getPlayer(args[0]);
			
			if(p2 != null){
				if(sender instanceof Player){
					Player p1 = (Player)sender;
					if(p1.hasPermission("trade.request")){
						
					}else{
						p1.sendMessage(Trade.formatChatColors("request.no_permission"));
					}
				}else{
					sender.sendMessage("This command can only be used by players!");
					return true;
				}
			}
			
		}else if(args.length == 2){
			Player p1 = plugin.getServer().getPlayer(args[0]);
			Player p2 = plugin.getServer().getPlayer(args[1]);
			
			if(p1 != null && p2 != null){
				if(sender.hasPermission("trade.maketrade")){
					plugin.startNewTrade(p1, p2);
					sender.sendMessage("Trade made!");
				}else{
					sender.sendMessage("You don't have the permission to make new trades!");
				}
				return true;
			}
		}
		return false;
	}

}
