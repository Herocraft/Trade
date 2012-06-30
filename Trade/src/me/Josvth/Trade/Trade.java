package me.Josvth.Trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.Josvth.Trade.Handlers.LanguageHandler;
import me.Josvth.Trade.Handlers.LanguageHandler.Message;
import me.Josvth.Trade.Handlers.CommandHandler;
import me.Josvth.Trade.Handlers.TradeHandler;
import me.Josvth.Trade.Handlers.YamlHandler;
import me.Josvth.Trade.Listeners.InTradeListener;
import me.Josvth.Trade.Listeners.RequestListener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;

public class Trade extends JavaPlugin{

	Logger logger;
	
	// key = requester , value = requested
	public HashMap<Player,Player> pendingRequests = new HashMap<Player, Player>();
	
	public HashMap<Player,TradeHandler> activeTrades = new HashMap<Player, TradeHandler>();

	public ArrayList<Player> ignoring = new ArrayList<Player>();
	
	public MobArenaHandler mobArenaHandler;
	public YamlHandler yamlHandler;
	public LanguageHandler languageHandler;
	CommandHandler commandHandler;
	
	InTradeListener inTradeListener;
	RequestListener requestListener;
	
	@Override
	public void onEnable() {
		logger = getLogger();
		
		mobArenaHandler = getMobArenaHandler(this);
		
		yamlHandler = new YamlHandler(this);
		languageHandler = new LanguageHandler(this);
		commandHandler = new CommandHandler(this);
		
		inTradeListener = new InTradeListener(this);
		requestListener = new RequestListener(this);
	}
	
	public void requestPlayer(final Player requester, final Player requested){
		if(!requester.hasPermission("trade.request")){
			languageHandler.sendMessage(requester, Message.REQUEST_NO_PERMISSION, "", "", "");
			return;
		}
		if(!requester.canSee(requested) && yamlHandler.configYaml.getBoolean("must_see",true)){
			languageHandler.sendMessage(requester, Message.REQUEST_MUST_SEE, "", "", "");
			return;
		}
		if(!requester.getWorld().equals(requested.getWorld()) && !yamlHandler.configYaml.getBoolean("cross_world",false)){
			languageHandler.sendMessage(requester, Message.REQUEST_NO_CROSS_WORLD, "", "", "");
			return;
		}
		if(!requester.getGameMode().equals(requested.getGameMode()) && !yamlHandler.configYaml.getBoolean("cross_gamemode",false)){
			languageHandler.sendMessage(requester, Message.REQUEST_NO_CROSS_GAMEMODE, "", "", "");
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
				if(pendingRequests.containsKey(requester)){
					languageHandler.sendMessage(requester, Message.REQUEST_NO_RESPONSE, requested.getName(), "", "");
					pendingRequests.remove(requester);
				}
			}
		}, yamlHandler.configYaml.getInt("request_timeout",10)*20);
	}
	
	public void acceptRequest(Player requested, Player requester){	
		startTrading(requested, requester);
		pendingRequests.remove(requester);
	}
	
	public void refuseRequest(Player requested, Player requester){
		languageHandler.sendMessage(requester, Message.REQUEST_REFUSED, requested.getName(), "", "");
		pendingRequests.remove(requester);
	}	
		
	public void startTrading(Player player1, Player player2){
		TradeHandler tradeHandler = new TradeHandler(this, player1, player2);
		activeTrades.put(player1, tradeHandler);
		activeTrades.put(player2, tradeHandler);
		tradeHandler.startTrading();
	}

    private MobArenaHandler getMobArenaHandler(Plugin plugin) {
        MobArenaHandler mobArenaHandler = null;

        if (plugin != null && plugin instanceof MobArena) {
            mobArenaHandler = new MobArenaHandler();
            logger.info("Successfully hooked " + plugin.getDescription().getName());
        }

        return mobArenaHandler;
    }
}
