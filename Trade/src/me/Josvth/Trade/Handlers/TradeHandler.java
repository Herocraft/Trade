package me.Josvth.Trade.Handlers;

import me.Josvth.Trade.Trade;
import me.Josvth.Trade.Exeptions.PlayerNotFoundExeption;
import me.Josvth.Trade.Handlers.LanguageHandler.Message;
import me.Josvth.Trade.TradingInventories.TradingInventory;

import org.bukkit.entity.Player;

public class TradeHandler {

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
	
	private Player getOtherPlayer(Player player) throws PlayerNotFoundExeption {
		if(player.equals(p1)){
			return p2;
		}else if(player.equals(p2)){
			return p1;
		}else{
			throw new PlayerNotFoundExeption();
		}
	}

	private TradingInventory getTradingInventory(Player player){
		if(player.equals(p1)) return p1inv;
		if(player.equals(p2)) return p2inv;
		return null;
	}

	public void startTrading() {

	}

	public void stopTrading() {
		revertItems();
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);		
	}

	private void revertItems() {
		for(int slot : p1inv.getOwnSlots()){

		}
		for(int slot : p2inv.getOwnSlots()){

		}
	}
}