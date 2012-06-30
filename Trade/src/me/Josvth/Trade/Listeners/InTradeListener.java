package me.Josvth.Trade.Listeners;

import java.util.HashMap;
import me.Josvth.Trade.Exeptions.PlayerNotFoundExeption;
import me.Josvth.Trade.Handlers.CurrencyTradeHandler;
import me.Josvth.Trade.Handlers.LanguageHandler;
import me.Josvth.Trade.Handlers.TradeHandler;
import me.Josvth.Trade.Trade;
import me.Josvth.Trade.TradingInventories.CurrencyTradeInventory;
import me.Josvth.Trade.TradingInventories.ItemTradeInventory;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.PluginManager;

public class InTradeListener
  implements Listener
{
  Trade plugin;

  public InTradeListener(Trade instance)
  {
    plugin = instance;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  @EventHandler
  public void onPlayerAbandon(InventoryCloseEvent event) {
    Player player = (Player)event.getPlayer();
    TradeHandler tradeHandler = (TradeHandler)plugin.activeTrades.get(player);
    if (tradeHandler != null)
      try {
        tradeHandler.refuse(player);
      } catch (PlayerNotFoundExeption e) {
        e.printStackTrace();
      }
  }

  @EventHandler
  public void onTradeInventoryClick(InventoryClickEvent event)
  {
    Player player = (Player)event.getWhoClicked();
    TradeHandler tradeHandler = (TradeHandler)plugin.activeTrades.get(player);

    if (tradeHandler == null) return;

    if (event.isShiftClick()) {
      event.setCancelled(true);
      event.setResult(Result.DENY);
    }

    try
    {
      ItemTradeInventory itemTradeInventory = tradeHandler.getTradingInventory(player);

      if (event.getRawSlot() > itemTradeInventory.getSize() - 1) return;

      if ((itemTradeInventory instanceof CurrencyTradeInventory)) {
        int type = ((CurrencyTradeInventory)itemTradeInventory).isCurrencySlot(event.getRawSlot());
        boolean add = event.isLeftClick();
        if (type != 0) {
          ((CurrencyTradeHandler)tradeHandler).addCurrency(player, (add ? 1 : -1) * type);
          event.setCancelled(true);
          return;
        }
      }

      if (itemTradeInventory.isAcceptSlot(event.getRawSlot())) {
        if (tradeHandler.hasAccepted(player))
          tradeHandler.denyTrade(player);
        else {
          tradeHandler.acceptTrade(player);
        }
        event.setCancelled(true);
        return;
      }

      if (itemTradeInventory.isRefuseSlot(event.getRawSlot())) {
        tradeHandler.refuse(player);
        event.setCancelled(true);
        return;
      }

      if (!itemTradeInventory.canUseSlot(event.getRawSlot())) {
        plugin.getLanguageHandler().sendMessage(player, "trade.cannot-use-slot");
        event.setCancelled(true);
        return;
      }
    }
    catch (PlayerNotFoundExeption e) {
      e.printStackTrace();
      tradeHandler.forceStopTrading();
      return;
    }
  }
}