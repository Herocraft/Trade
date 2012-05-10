package me.Josvth.Trade.Handlers;

import java.util.Arrays;

import me.Josvth.Trade.Trade;
import me.Josvth.Trade.Exeptions.PlayerNotFoundExeption;
import me.Josvth.Trade.Handlers.LanguageHandler.Message;
import me.Josvth.Trade.TradingInventories.TradingInventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradeHandler implements Runnable {

	Trade plugin;

	Player p1, p2;
	TradingInventory p1inv, p2inv;
	boolean p1acc, p2acc;

	public TradeHandler(Trade instance, Player player1, Player player2) {
		plugin = instance;
		p1 = player1;
		p2 = player2;
		createTradingInventories();
	}

	private void createTradingInventories() {

	}

	public void acceptTrade(Player player) throws PlayerNotFoundExeption{
		if(player.equals(p1)){
			p1acc = true;
		}else if(player.equals(p2)){
			p2acc = true;
		}else{
			throw new PlayerNotFoundExeption();
		}
		plugin.languageHandler.sendMessage(player, Message.TRADE_ACCEPT_SELF, "", "", "");
		plugin.languageHandler.sendMessage(getOtherPlayer(player), Message.TRADE_ACCEPT_OTHER, player.getName(), "", "");
		getTradingInventory(player).accept();
		if(p1acc && p2acc){
			finishTrade();
		}
	}

	public void denyTrade(Player player) throws PlayerNotFoundExeption{
		if(player.equals(p1)){
			p1acc = false;
		}else if(player.equals(p2)){ 
			p2acc = false;	
		}else{
			throw new PlayerNotFoundExeption();
		}
		plugin.languageHandler.sendMessage(player, Message.TRADE_DENY_SELF, "", "", "");
		plugin.languageHandler.sendMessage(getOtherPlayer(player), Message.TRADE_DENY_OTHER, player.getName(), "", "");
		getTradingInventory(player).deny();
	}

	private void cancelAccept(Player player) throws PlayerNotFoundExeption{
		if(player.equals(p1)){
			p1acc = false;
		}else if(player.equals(p2)){
			p2acc = false;
		}else{
			throw new PlayerNotFoundExeption();
		}
		plugin.languageHandler.sendMessage(player, Message.TRADE_OFFER_CHANGED, getOtherPlayer(player).getName(), "", "");
		getTradingInventory(player).deny();
	}

	private Player getOtherPlayer(Player player) throws PlayerNotFoundExeption {
		if(player.equals(p1)){
			return p2;
		}else if(player.equals(p2)){
			return p1;
		}else{
			throw new PlayerNotFoundExeption();
		}
	}

	private TradingInventory getTradingInventory(Player player) throws PlayerNotFoundExeption{
		if(player.equals(p1)){ 
			return p1inv;
		}else if(player.equals(p2)){
			return p2inv;
		}else{
			throw new PlayerNotFoundExeption();
		}
	}

	public void startTrading() {
		p1.openInventory(p1inv.getInventory());
		p2.openInventory(p2inv.getInventory());
		plugin.languageHandler.sendMessage(p1, Message.TRADE_START, p2.getName(), "", "");
		plugin.languageHandler.sendMessage(p2, Message.TRADE_START, p1.getName(), "", "");
		plugin.languageHandler.sendMessage(p1, Message.TRADE_HELP, p2.getName(), p1inv.getAcceptItem().getType().name(), p1inv.getRefuseItem().getType().name());
		plugin.languageHandler.sendMessage(p2, Message.TRADE_HELP, p1.getName(), p2inv.getAcceptItem().getType().name(), p1inv.getRefuseItem().getType().name());
	}

	private void finishTrade() {
		giveItems();
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);	
	}
	
	public void stopTrading() {
		revertItems();
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);		
	}

	private void giveItems() {
		for(ItemStack item : p1inv.getPendingItems()){
			p1.getInventory().addItem(item);
		}
		for(ItemStack item : p2inv.getPendingItems()){
			p2.getInventory().addItem(item);
		}
	}
	
	private void revertItems() {
		for(ItemStack item : p1inv.getOfferedItems()){
			p1.getInventory().addItem(item);
		}
		for(ItemStack item : p2inv.getOfferedItems()){
			p2.getInventory().addItem(item);
		}
	}

	@Override
	public void run() {

		ItemStack[] p1OfferedItems = p1inv.getOfferedItems();
		ItemStack[] p2PendingItems = p2inv.getPendingItems();

		if(!Arrays.equals(p1OfferedItems, p2PendingItems)){
			p2inv.setPendingItems(p1OfferedItems);
			if(p2acc){
				try {
					cancelAccept(p2);
				} catch (PlayerNotFoundExeption e) {
					e.printStackTrace();
				}
			}
		}

		ItemStack[] p2OfferedItems = p2inv.getOfferedItems();
		ItemStack[] p1PendingItems = p1inv.getPendingItems();

		if(!Arrays.equals(p2OfferedItems, p1PendingItems)){
			p1inv.setPendingItems(p2OfferedItems);
			if(p1acc){
				try {
					cancelAccept(p1);
				} catch (PlayerNotFoundExeption e) {
					e.printStackTrace();
				}
			}
		}
	}
}