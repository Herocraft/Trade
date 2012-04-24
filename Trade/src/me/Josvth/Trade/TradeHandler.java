package me.Josvth.Trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeHandler implements Runnable{

	Trade plugin;

	int tradeID;
	
	Player p1;
	Player p2;

	TradeInventory p1TradeInventory;
	TradeInventory p2TradeInventory;
	
	boolean p1accept = false;
	boolean p2accept = false;
	
	public TradeHandler(Trade instance, Player player1, Player player2){

		plugin = instance;

		p1 = player1;
		p2 = player2;

		p1TradeInventory = new TradeInventory(instance, p1.getName(), p2.getName());
		p2TradeInventory = new TradeInventory(instance, p2.getName(), p1.getName());

	}

	public void startTrading(){
		
		tradeID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 10);
		
		p1.openInventory(p1TradeInventory.getInventory());
		p2.openInventory(p2TradeInventory.getInventory());
		
		p1.sendMessage("You are trading with '"+ p2.getName() +"'!");
		p1.sendMessage("Click the green wool to accept the trade!");
		
		p2.sendMessage("You are trading with '"+ p1.getName() +"'!");
		p2.sendMessage("Click the green wool to accept the trade!");
	}
	
	
	public void playerAcceptTrade(Player player) {
		if(player.equals(p1)){
			p1accept = true;
		}else{
			p2accept = true;
		}
		
		player.sendMessage("You've accepted the trade!");
		getOtherPlayer(player).sendMessage(player.getName() + " accepted the trade!");
		getTradingInventory(player).setItem(TradeInventory.ACCEPT_SLOT, TradeInventory.ACCEPT_ITEM_PENDING);
		
		if(p1accept && p2accept) finishTrading();
	
	}
		
	public void playerCancel(Player player) {
		getOtherPlayer(player).sendMessage(player.getName() + " cancelled the trading.");
		stopTrading();
	}
		
	private void cancelTradeAccept(Player player) {
		if(player.equals(p1)){
			p1accept = false;
		}else{
			p2accept = false;
		}
		getTradingInventory(player).setItem(TradeInventory.ACCEPT_SLOT, TradeInventory.ACCEPT_ITEM);
	}
	
	// Inventories matcher
	@Override
	public void run() {
		
		ItemStack[] p1OfferedItems = getOfferedItems(p1);
		ItemStack[] p2PendingItems = getPendingItemsAt(p2);
				
		if(!Arrays.equals(p1OfferedItems, p2PendingItems)){
			updateOfferedItems(p1,p2);
			if(p2accept){
				cancelTradeAccept(p2);
				p2.sendMessage(p1.getName() + " changed their offer.");
			}
		}

		ItemStack[] p2OfferedItems = getOfferedItems(p2);
		ItemStack[] p1PendingItems = getPendingItemsAt(p1);

		if(!Arrays.equals(p2OfferedItems, p1PendingItems)){
			updateOfferedItems(p2,p1);
			if(p1accept){
				cancelTradeAccept(p1);
				p1.sendMessage(p2.getName() + " changed their offer.");
			}
		}
	}
	
	private void updateOfferedItems(Player p1, Player p2) {
		setPendingItems(p2, getOfferedItems(p1));		
	}

	private void setPendingItems(Player player, ItemStack[] offeredItems) {
		for(int slot = 0; slot < offeredItems.length; slot++){
			getTradingInventory(player).setItem(TradeInventory.RIGHT_SLOTS[slot], offeredItems[slot]);
		}		
	}

	private ItemStack[] getPendingItemsAt(Player player) {
		List<ItemStack> pendingItems = new ArrayList<ItemStack>();

		for(int slot: TradeInventory.RIGHT_SLOTS){
			pendingItems.add(getTradingInventory(player).getItem(slot));
		}

		ItemStack[] pendingItemsList = new ItemStack[pendingItems.size()];

		return pendingItems.toArray(pendingItemsList);
	}

	private ItemStack[] getOfferedItems(Player player) {
		List<ItemStack> offeredItems = new ArrayList<ItemStack>();

		for(int slot: TradeInventory.LEFT_SLOTS){
			offeredItems.add(getTradingInventory(player).getItem(slot));
		}

		ItemStack[] offeredItemsList = new ItemStack[offeredItems.size()];

		return offeredItems.toArray(offeredItemsList);
	}
	
	// ---------
	
	public void finishTrading(){
		for(int slot: TradeInventory.RIGHT_SLOTS){
			if(p1TradeInventory.getInventory().getItem(slot) != null)  p1.getInventory().addItem(p1TradeInventory.getInventory().getItem(slot));
			if(p2TradeInventory.getInventory().getItem(slot) != null)  p2.getInventory().addItem(p2TradeInventory.getInventory().getItem(slot));
		}
		
		p1.closeInventory();
		p2.closeInventory();
		
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);
		
		plugin.getServer().getScheduler().cancelTask(tradeID);
		
	}
	
	public void stopTrading(){

		for(int slot: TradeInventory.LEFT_SLOTS){
			if(p1TradeInventory.getInventory().getItem(slot) != null)  p1.getInventory().addItem(p1TradeInventory.getInventory().getItem(slot));
			if(p2TradeInventory.getInventory().getItem(slot) != null)  p2.getInventory().addItem(p2TradeInventory.getInventory().getItem(slot));
		}
		
		p1.closeInventory();
		p2.closeInventory();
		
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);
		
		plugin.getServer().getScheduler().cancelTask(tradeID);
		
	}
	
	private Inventory getTradingInventory(Player player) {
		return player.equals(p1) ? p1TradeInventory.getInventory() : p2TradeInventory.getInventory();
	}
	
	public static boolean canUseSlot(int slot){		
		return Arrays.asList(TradeInventory.LEFT_SLOTS).contains(slot);
	}

	public static boolean isAcceptSlot(int slot){
		return slot == TradeInventory.ACCEPT_SLOT;
	}

	private Player getOtherPlayer(Player player) {
		return player.equals(p1) ? p2 : p1;
	}
}
