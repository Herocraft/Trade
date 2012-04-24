package me.Josvth.Trade.Listeners;

import me.Josvth.Trade.Trade;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RequestListener implements Listener{
	
	Trade plugin;
	
	public RequestListener(Trade instance){
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onTradeRequest(PlayerInteractEntityEvent event){	
		
		//TODO rewrite this listener with something like double clicking. Because it's fancier.
		
		if (event.getRightClicked() instanceof Player){
			
			final Player requestor = event.getPlayer();
			final Player requested = (Player)event.getRightClicked();
			
			if(plugin.activeTrades.containsKey(requested)){
				
				requestor.sendMessage(requested.getName() + " is already in trade. Please try later.");
				
				return;
			}
			
			if(plugin.pendingRequests.containsKey(requested) && plugin.pendingRequests.get(requested).equals(requestor)){
				
				plugin.pendingRequests.remove(requested);
				
				plugin.startNewTrade(requestor,requested);
				
				return;
				
			}else if(plugin.pendingRequests.containsKey(requestor)){
				
				requestor.sendMessage("Please wait until " + requested.getName() + " accepts your trade request.");
				return;
				
			}else{
				
				requestor.sendMessage("You requested " + requested.getName() + " to trade.");
				requested.sendMessage(requestor.getName() + " wants to trade with you! Right click him to accept.");
				
				plugin.pendingRequests.put(requestor, requested);

				plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
					public void run() {	
						if(plugin.pendingRequests.containsKey(requestor)){
							requestor.sendMessage(requested.getName() + " didn't respond to your trade request!");
							plugin.pendingRequests.remove(requestor);
						}
					}
				}, 200);
				
			}
		}
	}	
}
