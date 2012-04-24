package me.Josvth.Trade;

import java.util.HashMap;
import java.util.Map;

import me.Josvth.Trade.Listeners.InTradeListener;
import me.Josvth.Trade.Listeners.RequestListener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Trade extends JavaPlugin implements Listener{
	
	public HashMap<Player,Player> pendingRequests = new HashMap<Player,Player>();
	public Map<Player,TradeHandler> activeTrades = new HashMap<Player,TradeHandler>();
	
	InTradeListener inTradeListener;
	RequestListener requestListener;
	
	@Override
	public void onEnable() {
		inTradeListener = new InTradeListener(this);
		requestListener = new RequestListener(this);
	}

	public void startNewTrade(Player player1, Player player2) {
		TradeHandler tradeHandler = new TradeHandler(this, player1, player2);
		activeTrades.put(player1, tradeHandler);
		activeTrades.put(player2, tradeHandler);
		tradeHandler.startTrading();
	}
	
}
