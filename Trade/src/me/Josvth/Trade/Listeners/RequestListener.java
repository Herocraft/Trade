package me.Josvth.Trade.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.Josvth.Trade.Trade;

public class RequestListener implements Listener {
	
	Trade plugin;
	
	public RequestListener(Trade instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onRightClickPlayer(PlayerInteractEntityEvent event){
		
		if(!(event.getRightClicked() instanceof Player)) return;
		
		Player requester = event.getPlayer();
		Player requested = (Player)event.getRightClicked();
		
		if(requester.isSneaking()){
			if(!plugin.yamlHandler.configYaml.getBoolean("shift_right_click",false)) return;
		}else{
			if(!plugin.yamlHandler.configYaml.getBoolean("right_click",true)) return;
		}
				
		if(plugin.pendingRequests.containsKey(requested)){
			plugin.acceptRequest(requester, requested);
		}else{
			plugin.requestPlayer(requester, requested);
		}
		
		event.setCancelled(true);
	}
}