package me.Josvth.Trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.Josvth.Trade.Inventories.TradeInventory;

import org.bukkit.entity.Player;
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

		int rowsP1 = getAllowedGuiRows(p1);
		int rowsP2 = getAllowedGuiRows(p2);

		int rows;

		if(rowsP1 > rowsP2 ^ plugin.config.getBoolean("use_biggest",true)){
			rows = rowsP1;
		}else{
			rows = rowsP2;
		}

		p1TradeInventory = new TradeInventory(instance, p1.getName(), p2.getName(), rows);
		p2TradeInventory = new TradeInventory(instance, p2.getName(), p1.getName(), rows);

	}

	private int getAllowedGuiRows(Player player){
		if(player.hasPermission("trade.gui.rows.6")){
			return 6;
		}else if(player.hasPermission("trade.gui.rows.5")){
			return 5;
		}else if(player.hasPermission("trade.gui.rows.4")){
			return 4;
		}else if(player.hasPermission("trade.gui.rows.3")){
			return 3;
		}else if(player.hasPermission("trade.gui.rows.2")){
			return 2;
		}else{
			return plugin.config.getInt("default_gui_rows",4);
		}
	}

	public void startTrading(){

		tradeID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 10);

		p1.openInventory(p1TradeInventory.getInventory());
		p2.openInventory(p2TradeInventory.getInventory());

		if(plugin.config.getString("messages.trade_start") != null){
			p1.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.trade_start").replaceAll("&other_player", p2.getName())));
			p2.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.trade_start").replaceAll("&other_player", p1.getName())));
		}

		if(plugin.config.getString("messages.trade_help") != null){
			String help = Trade.formatChatColors(plugin.config.getString("messages.trade_help").replaceAll("&accept_item",p1TradeInventory.getAcceptItem().getType().name().toLowerCase()).replaceAll("&refuse_item", p1TradeInventory.getRefuseItem().getType().name().toLowerCase()));
			p1.sendMessage(help);
			p2.sendMessage(help);
		}
	}

	public void accept(Player player) {

		if(player.equals(p1)){
			p1accept = true;
		}else{
			p2accept = true;
		}

		if(plugin.config.getString("messages.accept") != null){
			player.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.accept")));
		}

		if(plugin.config.getString("messages.accept_other") != null){
			getOtherPlayer(player).sendMessage(Trade.formatChatColors(plugin.config.getString("messages.accept_other").replaceAll("&other_player", player.getName())));
		}

		getTradeInventory(player).setPending();

		if(p1accept && p2accept) finishTrading();

	}

	public void deaccept(Player player) {

		if(player.equals(p1)){
			p1accept = false;
		}else{
			p2accept = false;
		}

		if(plugin.config.getString("messages.deaccept") != null){
			player.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.deaccept")));
		}

		if(plugin.config.getString("messages.deaccept_other") != null){
			getOtherPlayer(player).sendMessage(Trade.formatChatColors(plugin.config.getString("messages.deaccept_other").replaceAll("&other_player", player.getName())));
		}

		getTradeInventory(player).resetPending();
	}

	public void cancelAccept(Player player){

		if(player.equals(p1)){
			p1accept = false;
		}else{
			p2accept = false;
		}

		if(plugin.config.getString("messages.offer_changed") != null){
			player.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.offer_changed").replaceAll("&other_player", getOtherPlayer(player).getName())));
		}

		getTradeInventory(player).resetPending();
	}

	public void refuse(Player player){
		if(plugin.config.getString("messages.refuse") != null){
			player.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.refuse")));
		}

		if(plugin.config.getString("messages.refuse_other") != null){
			getOtherPlayer(player).sendMessage(Trade.formatChatColors(plugin.config.getString("messages.refuse_other").replaceAll("&other_player", player.getName())));
		}

		stopTrading();
	}

	public void abandon(Player player) {
		if(plugin.config.getString("messages.abandon") != null){
			player.sendMessage(Trade.formatChatColors(plugin.config.getString("messages.abandon")));
		}
		if(plugin.config.getString("messages.abandon_other") != null){
			getOtherPlayer(player).sendMessage(Trade.formatChatColors(plugin.config.getString("messages.abandon_other").replaceAll("&other_player", player.getName())));
		}

		stopTrading();
	}

	// Inventories matcher
	@Override
	public void run() {

		ItemStack[] p1OfferedItems = getOfferedItems(p1);
		ItemStack[] p2PendingItems = getPendingItemsAt(p2);

		if(!Arrays.equals(p1OfferedItems, p2PendingItems)){
			updateOfferedItems(p1,p2);
			if(p2accept) cancelAccept(p2);
		}

		ItemStack[] p2OfferedItems = getOfferedItems(p2);
		ItemStack[] p1PendingItems = getPendingItemsAt(p1);

		if(!Arrays.equals(p2OfferedItems, p1PendingItems)){
			updateOfferedItems(p2,p1);
			if(p1accept) cancelAccept(p1);
		}
	}

	private void updateOfferedItems(Player p1, Player p2) {
		setPendingItems(p2, getOfferedItems(p1));		
	}

	private void setPendingItems(Player player, ItemStack[] offeredItems) {
		for(int slot = 0; slot < offeredItems.length; slot++){
			getTradeInventory(player).getInventory().setItem(getTradeInventory(player).getRightSlots().get(slot), offeredItems[slot]);
		}		
	}

	private ItemStack[] getPendingItemsAt(Player player) {
		List<ItemStack> pendingItems = new ArrayList<ItemStack>();

		for(int slot : getTradeInventory(player).getLeftSlots()){
			pendingItems.add(getTradeInventory(player).getInventory().getItem(slot));
		}

		ItemStack[] pendingItemsList = new ItemStack[pendingItems.size()];

		return pendingItems.toArray(pendingItemsList);
	}

	private ItemStack[] getOfferedItems(Player player) {
		List<ItemStack> offeredItems = new ArrayList<ItemStack>();

		for(int slot: getTradeInventory(player).getLeftSlots()){
			offeredItems.add(getTradeInventory(player).getInventory().getItem(slot));
		}

		ItemStack[] offeredItemsList = new ItemStack[offeredItems.size()];

		return offeredItems.toArray(offeredItemsList);
	}

	// ---------

	public void finishTrading(){
		for(int slot : getTradeInventory(p1).getRightSlots()){
			if(p1TradeInventory.getInventory().getItem(slot) != null)  p1.getInventory().addItem(p1TradeInventory.getInventory().getItem(slot));
		}

		for(int slot : getTradeInventory(p2).getRightSlots()){
			if(p2TradeInventory.getInventory().getItem(slot) != null)  p2.getInventory().addItem(p2TradeInventory.getInventory().getItem(slot));
		}

		p1.closeInventory();
		p2.closeInventory();

		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);

		plugin.getServer().getScheduler().cancelTask(tradeID);

	}

	public void stopTrading(){

		for(int slot : getTradeInventory(p1).getLeftSlots()){
			if(p1TradeInventory.getInventory().getItem(slot) != null)  p1.getInventory().addItem(p1TradeInventory.getInventory().getItem(slot));
		}

		for(int slot : getTradeInventory(p2).getLeftSlots()){
			if(p2TradeInventory.getInventory().getItem(slot) != null)  p2.getInventory().addItem(p2TradeInventory.getInventory().getItem(slot));
		}

		p1.closeInventory();
		p2.closeInventory();

		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);

		plugin.getServer().getScheduler().cancelTask(tradeID);

	}

	public TradeInventory getTradeInventory(Player player) {
		return player.equals(p1) ? p1TradeInventory : p2TradeInventory;
	}

	private Player getOtherPlayer(Player player) {
		return player.equals(p1) ? p2 : p1;
	}

	public boolean hasAccepted(Player player) {
		if(player.equals(p1)){
			return p1accept;
		}else{
			return p2accept;
		}
	}
}
