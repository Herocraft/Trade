package me.Josvth.Trade.Handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Josvth.Trade.Trade;

public class LanguageHandler {

	Trade plugin;

	public enum Message{

		TRADE_ACCEPT_SELF ("trade.accept.self"),
		TRADE_ACCEPT_OTHER ("trade.accept.other"), 
		TRADE_DENY_SELF ("trade.deny.self"),
		TRADE_DENY_OTHER ("trade.deny.other"),
		TRADE_OFFER_CHANGED ("trade.offer_changed"),
		TRADE_START ("trade.start"),
		TRADE_HELP ("trade.help"),
		TRADE_CANNOT_USE_SLOT ("trade.cannot_use_slot"),
		TRADE_REFUSE_OTHER ("trade.refuse.other"),
		TRADE_REFUSE_SELF ("trade.refuse.self"),
		
		REQUEST_IN_TRADE ("request.in_trade"), 
		REQUEST_IGNORING ("request.ignoring.is_ignoring"),
		REQUEST_IGNORING_ENABLE ("request.ignoring.enabled"),
		REQUEST_IGNORING_DISABLE ("request.ignoring.disabled"),
		REQUEST_PLEASE_WAIT ("request.please_wait"), 
		REQUEST_NO_PERMISSION ("request.no_permission"), 
		REQUEST_NEW_SELF ("request.new.self"),
		REQUEST_NEW_OTHER ("request.new.other"), 
		REQUEST_NO_RESPONSE ("request.no_response"), 
		REQUEST_REFUSED ("request.refused"), 
		REQUEST_PLAYER_NOT_REQUESTED ("request.player_not_requested"),
		REQUEST_PLAYER_NOT_FOUND ("request.player_not_found"); 
		

		public final String path;

		Message(String yamlPath){
			path = yamlPath;
		}
	}

	public LanguageHandler(Trade instance){
		plugin = instance;
	}

	public void sendMessage(Player player, Message message, String playerName, String acceptItemName, String refuseItemName) {
		String string = plugin.yamlHandler.languageYaml.getString(message.path);
		if(string != null){
			string.replaceAll("&other_player", playerName);
			string.replaceAll("&accept_item", acceptItemName);
			string.replaceAll("&refuse_item", refuseItemName);
			player.sendMessage(formatColorCodes(string));
		}
	}

	public static final String formatColorCodes(String string){
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
