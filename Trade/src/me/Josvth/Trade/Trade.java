package me.Josvth.Trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.Josvth.Trade.Handlers.LanguageHandler;
import me.Josvth.Trade.Handlers.LanguageHandler.Message;
import me.Josvth.Trade.Handlers.CommandHandler;
import me.Josvth.Trade.Handlers.TradeHandler;
import me.Josvth.Trade.Handlers.YamlHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Trade extends JavaPlugin{

	Logger logger;
	
	// key = requester , value = requested
	HashMap<Player,Player> pendingRequests = new HashMap<Player, Player>();
	
	public HashMap<Player,Object> activeTrades = new HashMap<Player, Object>();

	ArrayList<Player> ignoring = new ArrayList<Player>();
	
	public YamlHandler yamlHandler;
	public LanguageHandler languageHandler;
	CommandHandler commandHandler;
	
	@Override
	public void onEnable() {
		logger = getLogger();
	}
	
	public void requestPlayer(final Player requester, final Player requested){
		if(!requester.hasPermission("trade.request")){
			languageHandler.sendMessage(requester, Message.REQUEST_NO_PERMISSION, "", "", "");
			return;
		}
		if(activeTrades.containsKey(requested)){
			languageHandler.sendMessage(requester, Message.REQUEST_IN_TRADE, requested.getName(), "", "");
			return;
		}
		if(ignoring.contains(requested)){
			languageHandler.sendMessage(requester, Message.REQUEST_IGNORING, requested.getName(), "", "");
			return;
		}
		if(pendingRequests.containsKey(requester)){
			languageHandler.sendMessage(requester, Message.REQUEST_PLEASE_WAIT, requested.getName(), "", "");
			return;
		}
		pendingRequests.put(requester, requested);
		languageHandler.sendMessage(requester, Message.REQUEST_NEW_SELF, requested.getName(), "", "");
		languageHandler.sendMessage(requested, Message.REQUEST_NEW_OTHER, requester.getName(), "", "");
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				languageHandler.sendMessage(requester, Message.REQUEST_NO_RESPONSE, requested.getName(), "", "");
				pendingRequests.remove(requested);
			}
		}, yamlHandler.config.getInt("request_timeout",10)*20);
	}
	
	public void cancelRequest(Player requester){
		pendingRequests.remove(requester);
	}
	
	public void startTrading(Player player1, Player player2){
		TradeHandler tradeHandler = new TradeHandler(this, player1, player2);
		activeTrades.put(player1, tradeHandler);
		activeTrades.put(player2, tradeHandler);
		tradeHandler.startTrading();
	}


}
