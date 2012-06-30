package me.Josvth.Trade.Handlers;

import java.util.Arrays;
import java.util.HashMap;
import me.Josvth.Trade.Exeptions.PlayerNotFoundExeption;
import me.Josvth.Trade.Trade;
import me.Josvth.Trade.TradingInventories.ItemTradeInventory;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

public class TradeHandler
  implements Runnable
{
  Trade plugin;
  LanguageHandler languageHandler;
  final Player p1;
  final Player p2;
  ItemTradeInventory p1inv;
  ItemTradeInventory p2inv;
  boolean p1acc = false; boolean p2acc = false;

  public TradeHandler(Trade instance, Player player1, Player player2) {
    plugin = instance;

    languageHandler = plugin.getLanguageHandler();

    p1 = player1;
    p2 = player2;
    createTradingInventories();
  }

  protected void createTradingInventories()
  {
    int rows = 4;
    int rowsP1 = getAllowedGuiRows(p1);
    int rowsP2 = getAllowedGuiRows(p2);

    if (rowsP2 > rowsP1) {
      if (plugin.yamlHandler.configYaml.getBoolean("use_biggest", true))
        rows = rowsP2;
      else {
        rows = rowsP1;
      }
    }
    else if (plugin.yamlHandler.configYaml.getBoolean("use_biggest", true))
      rows = rowsP1;
    else {
      rows = rowsP2;
    }

    p1inv = new ItemTradeInventory(plugin, p2.getName(), rows);
    p2inv = new ItemTradeInventory(plugin, p1.getName(), rows);
  }

  public void acceptTrade(Player player) throws PlayerNotFoundExeption
  {
    if (player.equals(p1))
      p1acc = true;
    else if (player.equals(p2))
      p2acc = true;
    else {
      throw new PlayerNotFoundExeption();
    }
    languageHandler.sendMessage(player, "trade.accept.self");
    languageHandler.sendMessage(getOtherPlayer(player), "trade.accept.other", new LanguageHandler.MessageArgument("%playername%", player.getName()));
    getTradingInventory(player).accept();
    getTradingInventory(getOtherPlayer(player)).acceptOther();
    if ((p1acc) && (p2acc))
      finishTrade();
  }

  public void denyTrade(Player player) throws PlayerNotFoundExeption
  {
    if (player.equals(p1))
      p1acc = false;
    else if (player.equals(p2))
      p2acc = false;
    else {
      throw new PlayerNotFoundExeption();
    }
    languageHandler.sendMessage(player, "trade.deny.self");
    languageHandler.sendMessage(getOtherPlayer(player), "trade.deny.other", new LanguageHandler.MessageArgument("%playername%", player.getName()));
    getTradingInventory(player).deny();
    getTradingInventory(getOtherPlayer(player)).denyOther();
  }

  protected void cancelAccept(Player player) throws PlayerNotFoundExeption {
    if (player.equals(p1))
      p1acc = false;
    else if (player.equals(p2))
      p2acc = false;
    else {
      throw new PlayerNotFoundExeption();
    }
    languageHandler.sendMessage(player, "trade.offer-changed", new LanguageHandler.MessageArgument("%playername%", getOtherPlayer(player).getName()));
    getTradingInventory(player).deny();
    getTradingInventory(getOtherPlayer(player)).denyOther();
  }

  public void refuse(Player player) throws PlayerNotFoundExeption {
    if ((!player.equals(p1)) && (!player.equals(p2))) {
      throw new PlayerNotFoundExeption();
    }
    languageHandler.sendMessage(player, "trade.refuse.self");
    languageHandler.sendMessage(getOtherPlayer(player), "trade.refuse.other", new LanguageHandler.MessageArgument("%playername%", player.getName()));
    stopTrading();
  }

  public boolean hasAccepted(Player player) throws PlayerNotFoundExeption
  {
    if (player.equals(p1))
      return p1acc;
    if (player.equals(p2)) {
      return p2acc;
    }
    throw new PlayerNotFoundExeption();
  }

  public Player getOtherPlayer(Player player) throws PlayerNotFoundExeption
  {
    if (player.equals(p1))
      return p2;
    if (player.equals(p2)) {
      return p1;
    }
    throw new PlayerNotFoundExeption();
  }

  public ItemTradeInventory getTradingInventory(Player player) throws PlayerNotFoundExeption
  {
    if (player.equals(p1))
      return p1inv;
    if (player.equals(p2)) {
      return p2inv;
    }
    throw new PlayerNotFoundExeption();
  }

  public void startTrading()
  {
    p1.openInventory(p1inv.getInventory());
    p2.openInventory(p2inv.getInventory());
    languageHandler.sendMessage(p1, "trade.start", new LanguageHandler.MessageArgument("%playername%", p2.getName()));
    languageHandler.sendMessage(p2, "trade.start", new LanguageHandler.MessageArgument("%playername%", p1.getName()));
    languageHandler.sendMessage(p1, "trade.help");
    languageHandler.sendMessage(p2, "trade.help");
    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 5L);
  }

  private void finishTrade()
  {
    p1.getInventory().addItem(new ItemStack[] { p1.getItemOnCursor() });
    p1.setItemOnCursor(new ItemStack(0));

    p2.getInventory().addItem(new ItemStack[] { p2.getItemOnCursor() });
    p2.setItemOnCursor(new ItemStack(0));

    giveOffers();
    plugin.activeTrades.remove(p1);
    plugin.activeTrades.remove(p2);
    p1.closeInventory();
    p2.closeInventory();

    p1.updateInventory();
    p2.updateInventory();
  }

  public void stopTrading()
  {
    p1.getInventory().addItem(new ItemStack[] { p1.getItemOnCursor() });
    p1.setItemOnCursor(new ItemStack(0));

    p2.getInventory().addItem(new ItemStack[] { p2.getItemOnCursor() });
    p2.setItemOnCursor(new ItemStack(0));

    revertOffers();
    plugin.activeTrades.remove(p1);
    plugin.activeTrades.remove(p2);
    p1.closeInventory();
    p2.closeInventory();

    p1.updateInventory();
    p2.updateInventory();
  }

  public void forceStopTrading()
  {
    p1.getInventory().addItem(new ItemStack[] { p1.getItemInHand() });
    p1.setItemInHand(new ItemStack(0));

    p2.getInventory().addItem(new ItemStack[] { p2.getItemInHand() });
    p2.setItemInHand(new ItemStack(0));

    languageHandler.sendMessage(p1, "trade.error");
    languageHandler.sendMessage(p2, "trade.error");
    stopTrading();
  }

  protected void giveOffers()
  {
    for (ItemStack item : p1inv.getOthersItems()) {
      if (item != null) {
        p1.getInventory().addItem(new ItemStack[] { item });
      }
    }

    for (ItemStack item : p2inv.getOthersItems())
      if (item != null)
        p2.getInventory().addItem(new ItemStack[] { item });
  }

  protected void revertOffers()
  {
    for (ItemStack item : p1inv.getOwnItems()) {
      if (item != null) {
        p1.getInventory().addItem(new ItemStack[] { item });
      }
    }

    for (ItemStack item : p2inv.getOwnItems())
      if (item != null)
        p2.getInventory().addItem(new ItemStack[] { item });
  }

  public void run()
  {
    ItemStack[] p1OfferedItems = p1inv.getOwnItems();
    ItemStack[] p2PendingItems = p2inv.getOthersItems();

    if (!Arrays.equals(p1OfferedItems, p2PendingItems)) {
      p2inv.setPendingItems(p1OfferedItems);
      try {
        cancelAccept(p2);
      } catch (PlayerNotFoundExeption exception) {
        forceStopTrading();
        exception.printStackTrace();
      }
    }

    ItemStack[] p2OfferedItems = p2inv.getOwnItems();
    ItemStack[] p1PendingItems = p1inv.getOthersItems();

    if (!Arrays.equals(p2OfferedItems, p1PendingItems)) {
      p1inv.setPendingItems(p2OfferedItems);
      try {
        cancelAccept(p1);
      } catch (PlayerNotFoundExeption exception) {
        forceStopTrading();
        exception.printStackTrace();
      }
    }
  }

  public int getAllowedGuiRows(Player player) {
    if (player.hasPermission("trade.gui.rows.6"))
      return 6;
    if (player.hasPermission("trade.gui.rows.5"))
      return 5;
    if (player.hasPermission("trade.gui.rows.4"))
      return 4;
    if (player.hasPermission("trade.gui.rows.3"))
      return 3;
    if (player.hasPermission("trade.gui.rows.2")) {
      return 2;
    }
    return plugin.yamlHandler.configYaml.getInt("default-rows", 4);
  }
}