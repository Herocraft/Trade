package me.Josvth.Trade.Handlers;

import java.util.regex.Matcher;
import me.Josvth.Trade.Exeptions.PlayerNotFoundExeption;
import me.Josvth.Trade.Trade;
import me.Josvth.Trade.TradingInventories.CurrencyTradeInventory;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CurrencyTradeHandler extends TradeHandler
{
  public CurrencyTradeHandler(Trade instance, Player player1, Player player2)
  {
    super(instance, player1, player2);
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

    ItemStack small = new ItemStack(plugin.yamlHandler.configYaml.getInt("economy.items.small.type", 371), plugin.yamlHandler.configYaml.getInt("economy.items.small.amount", 1));
    ItemStack medium = new ItemStack(plugin.yamlHandler.configYaml.getInt("economy.items.medium.type", 371), plugin.yamlHandler.configYaml.getInt("economy.items.medium.amount", 1));
    ItemStack large = new ItemStack(plugin.yamlHandler.configYaml.getInt("economy.items.large.type", 371), plugin.yamlHandler.configYaml.getInt("economy.items.large.amount", 1));

    p1inv = new CurrencyTradeInventory(plugin, p2.getName(), rows, small, medium, large, plugin.yamlHandler.configYaml.getInt("economy.values.small", 1), plugin.yamlHandler.configYaml.getInt("economy.values.medium", 10), plugin.yamlHandler.configYaml.getInt("economy.values.large", 50));
    p2inv = new CurrencyTradeInventory(plugin, p1.getName(), rows, small, medium, large, plugin.yamlHandler.configYaml.getInt("economy.values.small", 1), plugin.yamlHandler.configYaml.getInt("economy.values.medium", 10), plugin.yamlHandler.configYaml.getInt("economy.values.large", 50));
  }

  public void addCurrency(Player player, int amount)
    throws PlayerNotFoundExeption
  {
    String formatAmount = Matcher.quoteReplacement(plugin.economyHandler.format(Math.abs(amount)));

    if (!plugin.economyHandler.has(player.getName(), amount)) {
      languageHandler.sendMessage(player, "trade.currency.no-balance", new LanguageHandler.MessageArgument("%amount%", formatAmount));
      return;
    }

    CurrencyTradeInventory currencyTradeInventory = getTradingInventory(player);

    if ((amount < 0) && (currencyTradeInventory.getCurrency() < Math.abs(amount))) {
      languageHandler.sendMessage(player, "trade.currency.remove.cant", new LanguageHandler.MessageArgument("%amount%", formatAmount));
      return;
    }

    EconomyResponse response = plugin.economyHandler.withdrawPlayer(player.getName(), amount);

    int newCurrency = currencyTradeInventory.getCurrency() + amount;

    currencyTradeInventory.setCurrency(newCurrency);

    getTradingInventory(getOtherPlayer(player)).setOthersCurrency(newCurrency);

    if (amount > 0) {
      languageHandler.sendMessage(player, "trade.currency.add.self", 
        new LanguageHandler.MessageArgument[] { 
        new LanguageHandler.MessageArgument("%amount%", formatAmount), 
        new LanguageHandler.MessageArgument("%balance%", Matcher.quoteReplacement(plugin.economyHandler.format(response.balance))) });

      languageHandler.sendMessage(getOtherPlayer(player), "trade.currency.add.other", 
        new LanguageHandler.MessageArgument[] { 
        new LanguageHandler.MessageArgument("%playername%", player.getName()), 
        new LanguageHandler.MessageArgument("%amount%", formatAmount) });
    }
    else {
      languageHandler.sendMessage(player, "trade.currency.remove.self", 
        new LanguageHandler.MessageArgument[] { 
        new LanguageHandler.MessageArgument("%amount%", formatAmount), 
        new LanguageHandler.MessageArgument("%balance%", Matcher.quoteReplacement(plugin.economyHandler.format(response.balance))) });

      languageHandler.sendMessage(getOtherPlayer(player), "trade.currency.remove.other", 
        new LanguageHandler.MessageArgument[] { 
        new LanguageHandler.MessageArgument("%playername%", player.getName()), 
        new LanguageHandler.MessageArgument("%amount%", formatAmount) });
    }

    super.cancelAccept(getOtherPlayer(player));
  }

  public CurrencyTradeInventory getTradingInventory(Player player)
    throws PlayerNotFoundExeption
  {
    if (player.equals(p1))
      return (CurrencyTradeInventory)p1inv;
    if (player.equals(p2)) {
      return (CurrencyTradeInventory)p2inv;
    }
    throw new PlayerNotFoundExeption();
  }

  protected void giveOffers()
  {
    plugin.economyHandler.depositPlayer(p1.getName(), ((CurrencyTradeInventory)p1inv).getOthersCurrency());
    plugin.economyHandler.depositPlayer(p2.getName(), ((CurrencyTradeInventory)p2inv).getOthersCurrency());

    super.giveOffers();
  }

  protected void revertOffers()
  {
    plugin.economyHandler.depositPlayer(p1.getName(), ((CurrencyTradeInventory)p1inv).getCurrency());
    plugin.economyHandler.depositPlayer(p2.getName(), ((CurrencyTradeInventory)p2inv).getCurrency());

    super.revertOffers();
  }
}