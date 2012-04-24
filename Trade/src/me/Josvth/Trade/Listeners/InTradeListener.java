package me.Josvth.Trade.Listeners;

import me.Josvth.Trade.Trade;
import me.Josvth.Trade.TradeHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InTradeListener implements Listener{
	
	Trade plugin;
	
	public InTradeListener(Trade instance){
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onTradeInventoryClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
				
		if(!plugin.activeTrades.containsKey(player)) return;	// Player is not trading
		
		if (event.getRawSlot() < 27 && TradeHandler.isAcceptSlot(event.getRawSlot())){
			plugin.activeTrades.get(player).playerAcceptTrade(player);
			event.setCancelled(true);
			return;
		}
		
		if (event.getRawSlot() < 27 && !TradeHandler.canUseSlot(event.getRawSlot())){
			player.sendMessage("You cannot use this slot! Use the other side!");
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerAbandonTrade(InventoryCloseEvent event){
		
		Player player = (Player) event.getPlayer();
		
		if(!plugin.activeTrades.containsKey(player)) return;
		
		plugin.activeTrades.get(player).playerCancel(player);
	
	}
}
