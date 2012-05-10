package me.Josvth.Trade.Handlers;

import java.util.Arrays;

import me.Josvth.Trade.Trade;
import me.Josvth.Trade.Exeptions.PlayerNotFoundExeption;
import me.Josvth.Trade.Handlers.LanguageHandler.Message;
import me.Josvth.Trade.TradingInventories.StandardInventory;
import me.Josvth.Trade.TradingInventories.TradingInventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradeHandler implements Runnable {

	Trade plugin;

	Player p1, p2;
	TradingInventory p1inv, p2inv;
	boolean p1acc = false, p2acc = false;

	public TradeHandler(Trade instance, Player player1, Player player2) {
		plugin = instance;
		p1 = player1;
		p2 = player2;
		createTradingInventories();
	}

	private void createTradingInventories() {
		
		int rows = 4;
		int rowsP1 = getAllowedGuiRows(p1);
		int rowsP2 = getAllowedGuiRows(p2);
		
		// Yes this can be done with a xor but I'm lazy
		
		if(rowsP2 > rowsP1){
			if(plugin.yamlHandler.configYaml.getBoolean("use_biggest", true)){
				rows = rowsP2;
			}else{
				rows = rowsP1;
			}
		}else{
			if(plugin.yamlHandler.configYaml.getBoolean("use_biggest", true)){
				rows = rowsP1;
			}else{
				rows = rowsP2;
			}
		}
		
		p1inv = new StandardInventory(plugin, p2.getName(),	rows);
		p2inv = new StandardInventory(plugin, p1.getName(), rows);
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
		getTradingInventory(getOtherPlayer(player)).acceptOther();
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
		getTradingInventory(getOtherPlayer(player)).denyOther();
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

	public void refuse(Player player) throws PlayerNotFoundExeption{
		if(!player.equals(p1) && !player.equals(p2)){
			throw new PlayerNotFoundExeption();
		}else{
			plugin.languageHandler.sendMessage(player, Message.TRADE_REFUSE_SELF, "", "", "");
			plugin.languageHandler.sendMessage(getOtherPlayer(player), Message.TRADE_REFUSE_OTHER, getOtherPlayer(player).getName(), "", "");
			stopTrading();
		}
	}
	
	public boolean hasAccepted(Player player) throws PlayerNotFoundExeption {
		if(player.equals(p1)){
			return p1acc;
		}else if(player.equals(p2)){
			return p2acc;
		}else{
			throw new PlayerNotFoundExeption();
		}
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

	public TradingInventory getTradingInventory(Player player) throws PlayerNotFoundExeption{
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
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 5);
	}

	private void finishTrade() {
		giveItems();
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);
		p1.closeInventory();
		p2.closeInventory();
	}
	
	public void stopTrading() {
		revertItems();
		plugin.activeTrades.remove(p1);
		plugin.activeTrades.remove(p2);
		p1.closeInventory();
		p2.closeInventory();
	}

	private void giveItems() {
		for(ItemStack item : p1inv.getPendingItems()){
			if(item != null){
				p1.getInventory().addItem(item);
			}
		}
		for(ItemStack item : p2inv.getPendingItems()){
			if(item != null){
				p2.getInventory().addItem(item);
			}
		}
	}
	
	private void revertItems() {
		for(ItemStack item : p1inv.getOfferedItems()){
			if(item != null){
				p1.getInventory().addItem(item);
			}
		}
		for(ItemStack item : p2inv.getOfferedItems()){
			if(item != null){
				p2.getInventory().addItem(item);
			}
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
	
	public int getAllowedGuiRows(Player player){
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
			return plugin.yamlHandler.configYaml.getInt("default_rows", 4);
		}
	}
}