package me.Josvth.Trade.TradingInventories;

import java.util.ArrayList;
import java.util.Iterator;
import me.Josvth.Trade.Trade;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CurrencyTradeInventory extends ItemTradeInventory
{
  int smallCurrency = 1;
  int mediumCurrency = 10;
  int largeCurrency = 50;

  ItemStack smallCurrencyItem = new ItemStack(371, 1);
  ItemStack mediumCurrencyItem = new ItemStack(266, 10);
  ItemStack largeCurrencyItem = new ItemStack(41, 50);
  int smallCurrencySlot;
  int mediumCurrencySlot;
  int largeCurrencySlot;
  final int smallCurrencyAmountSlot = 0;
  final int mediumCurrencyAmountSlot = 1;
  final int largeCurrencyAmountSlot = 2;

  final int smallCurrencyAmountSlotOther = 6;
  final int mediumCurrencyAmountSlotOther = 7;
  final int largeCurrencyAmountSlotOther = 8;

  int ownCurrency = 0;
  int othersCurrency = 0;
  public static final int MAX_CURRENCY = 3904;

  public CurrencyTradeInventory(Trade instance, String otherPlayerName, int rows)
  {
    super(instance, otherPlayerName, rows);

    setupCurrencyInventory();
  }

  public CurrencyTradeInventory(Trade instance, String otherPlayerName, int rows, ItemStack smallCurrencyItem, ItemStack meduimCurrencyItem, ItemStack largeCurrencyItem, int smallCurrency, int mediumCurrency, int largeCurrency)
  {
    super(instance, otherPlayerName, rows);

    this.smallCurrency = smallCurrency;
    this.mediumCurrency = mediumCurrency;
    this.largeCurrency = largeCurrency;

    this.smallCurrencyItem = smallCurrencyItem;
    mediumCurrencyItem = meduimCurrencyItem;
    this.largeCurrencyItem = largeCurrencyItem;

    setupCurrencyInventory();
  }

  protected void setupInventory()
  {
    ownSlots = new ArrayList(4 * (rows - 2));
    otherSlots = new ArrayList(4 * (rows - 2));
    seperatorSlots = new ArrayList(rows - 1);

    for (int slot = 9; slot < (rows - 1) * 9; slot++)
    {
      int collum = slot % 9;
      if (collum < 4) {
        ownSlots.add(Integer.valueOf(slot));
      }
      if (collum == 4) {
        seperatorSlots.add(Integer.valueOf(slot));
      }
      if (collum > 4) {
        otherSlots.add(Integer.valueOf(slot));
      }
    }

    seperatorSlots.add(Integer.valueOf(rows * 9 - 5));

    for (Integer slot : seperatorSlots) {
      inventory.setItem(slot, seperatorItem);
    }

    inventory.setItem(3, acceptItem);
    inventory.setItem(4, refuseItem);
    inventory.setItem(5, pendingItem);
  }

  private void setupCurrencyInventory()
  {
    smallCurrencySlot = ((rows - 1) * 9);
    mediumCurrencySlot = ((rows - 1) * 9 + 1);
    largeCurrencySlot = ((rows - 1) * 9 + 2);

    inventory.setItem((rows - 1) * 9, smallCurrencyItem);
    inventory.setItem((rows - 1) * 9 + 1, mediumCurrencyItem);
    inventory.setItem((rows - 1) * 9 + 2, largeCurrencyItem);
  }

  public boolean canAdd(int amount)
  {
    return ownCurrency + amount <= 3904;
  }

  public int getCurrency() {
    return ownCurrency;
  }

  public int setCurrency(int amount)
  {
    ownCurrency = amount;

    int overflow = ownCurrency - 3904;

    int large = (int)Math.floor(ownCurrency / largeCurrency);

    if (large > 0)
      inventory.setItem(2, new ItemStack(largeCurrencyItem.getType(), large));
    else {
      inventory.clear(2);
    }

    int medium = (int)Math.floor((ownCurrency - largeCurrency * large) / mediumCurrency);
    if (medium > 0)
      inventory.setItem(1, new ItemStack(mediumCurrencyItem.getType(), medium));
    else {
      inventory.clear(1);
    }

    int small = (int)Math.floor(ownCurrency - largeCurrency * large - mediumCurrency * medium) / smallCurrency;

    if (small > 0)
      inventory.setItem(0, new ItemStack(smallCurrencyItem.getType(), small));
    else {
      inventory.clear(0);
    }

    return overflow;
  }

  public int getOthersCurrency() {
    return othersCurrency;
  }

  public int setOthersCurrency(int amount)
  {
    othersCurrency = amount;

    int overflow = othersCurrency - 3904;

    int large = (int)Math.floor(othersCurrency / largeCurrency);

    if (large > 0)
      inventory.setItem(8, new ItemStack(largeCurrencyItem.getType(), large));
    else {
      inventory.clear(8);
    }

    int medium = (int)Math.floor((othersCurrency - largeCurrency * large) / mediumCurrency);

    if (medium > 0)
      inventory.setItem(7, new ItemStack(mediumCurrencyItem.getType(), medium));
    else {
      inventory.clear(7);
    }

    int small = (int)Math.floor(othersCurrency - largeCurrency * large - mediumCurrency * medium) / smallCurrency;

    if (small > 0)
      inventory.setItem(6, new ItemStack(smallCurrencyItem.getType(), small));
    else {
      inventory.clear(6);
    }

    return overflow;
  }

  public int isCurrencySlot(int slot)
  {
    if (slot == smallCurrencySlot) return smallCurrency;
    if (slot == mediumCurrencySlot) return mediumCurrency;
    if (slot == largeCurrencySlot) return largeCurrency;
    return 0;
  }
}