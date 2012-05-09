package me.Josvth.Trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.Josvth.Trade.Handlers.CommandHandler;
import me.Josvth.Trade.Handlers.TradeHandler;
import me.Josvth.Trade.Handlers.YamlHandler;
import me.Josvth.Trade.Listeners.InTradeListener;
import me.Josvth.Trade.Listeners.RequestListener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Trade extends JavaPlugin{

	Logger logger;

	public HashMap<Player,Player> pendingRequests = new HashMap<Player,Player>();
	public Map<Player,TradeHandler> activeTrades = new HashMap<Player,TradeHandler>();
	public List<Player> ignoreRequests = new ArrayList<Player>();

	InTradeListener inTradeListener;
	RequestListener requestListener;

	CommandHandler commandHandler;
	public YamlHandler yamlHandler;

	@Override
	public void onDisable(){
	}
	
	//test commit in branch
	
	@Override
	public void onEnable() {

		logger = getLogger();

		commandHandler = new CommandHandler(this);
		yamlHandler = new YamlHandler(this);

		inTradeListener = new InTradeListener(this);
		requestListener = new RequestListener(this);

	}

	public void request(final Player requestor, final Player requested){

		if(ignoreRequests.contains(requested) || requested.hasPermission("trade.ignorerequests")){
			if(yamlHandler.messagesYaml.isString("request.ignoring")){
				requestor.sendMessage(formatChatColors(yamlHandler.messagesYaml.getString("request.ignoring").replaceAll("&other_player", requested.getName())));
			}
			return;
		}	

		if(!requestor.getWorld().equals(requestor.getWorld()) && !yamlHandler.messagesYaml.getBoolean("crossworld_trading", false)){
			if(yamlHandler.messagesYaml.isString("request.ignoring")){
				requestor.sendMessage(formatChatColors(yamlHandler.messagesYaml.getString("request.no_crossworld").replaceAll("&other_player", requested.getName())));
			}
			return;
		}
		{

			if(yamlHandler.messagesYaml.isString("request")){
				requested.sendMessage(formatChatColors(yamlHandler.messagesYaml.getString("request.other").replaceAll("&other_player", requestor.getName())));
			}

			if(yamlHandler.messagesYaml.isString("request_self")){
				requestor.sendMessage(formatChatColors(yamlHandler.messagesYaml.getString("request.self").replaceAll("&other_player", requested.getName())));
			}

			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {	
					if(pendingRequests.containsKey(requestor)){
						requestor.sendMessage(formatChatColors(yamlHandler.messagesYaml.getString("request.no_response").replaceAll("&other_player", requested.getName())));
						pendingRequests.remove(requestor);
					}
				}
			}, yamlHandler.configYaml.getInt("request_timeout",10) * 20);

			pendingRequests.put(requestor, requested);
		}
	}

	public void startNewTrade(Player player1, Player player2) {
		TradeHandler tradeHandler = new TradeHandler(this, player1, player2);
		activeTrades.put(player1, tradeHandler);
		activeTrades.put(player2, tradeHandler);
		tradeHandler.startTrading();
	}

	public static String formatChatColors(String string){
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public void logWarning(String message){
		logger.warning(message);
	}
}
