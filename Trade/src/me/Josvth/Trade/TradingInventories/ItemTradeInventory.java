package me.Josvth.Trade.TradingInventories;

import java.util.ArrayList;
import java.util.Iterator;
import me.Josvth.Trade.Trade;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ItemTradeInventory
  implements InventoryHolder
{
  final Inventory inventory;
  ArrayList<Integer> ownSlots;
  ArrayList<Integer> otherSlots;
  ArrayList<Integer> seperatorSlots;
  ItemStack acceptItem = new ItemStack(35, 0, (short) 5);
  ItemStack refuseItem = new ItemStack(35, 0, (short) 14);
  ItemStack pendingItem = new ItemStack(35, 0, (short) 8);
  ItemStack seperatorItem = new ItemStack(280, 0);

  final int acceptSlot = 3;
  final int refuseSlot = 4;
  final int statusSlot = 5;
  final int size;
  final int rows;

  public ItemTradeInventory(Trade instance, String otherPlayerName, int guirows)
  {
    rows = guirows;
    size = (guirows * 9);

    inventory = instance.getServer().createInventory(this, size, generateTitle(otherPlayerName));

    setupInventory();
  }

  protected void setupInventory()
  {
    ownSlots = new ArrayList(rows * 4 - 1);
    otherSlots = new ArrayList(rows * 4 - 1);
    seperatorSlots = new ArrayList(rows - 1);

    for (int slot = 0; slot < rows * 9; slot++) {
      if ((slot != 3) && (slot != 4) && (slot != 5)) {
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
    }

    for (Integer slot : seperatorSlots) {
      inventory.setItem(slot, seperatorItem);
    }

    inventory.setItem(3, acceptItem);
    inventory.setItem(4, refuseItem);
    inventory.setItem(5, pendingItem);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void accept() {
    inventory.setItem(3, pendingItem);
  }

  public void deny() {
    inventory.setItem(3, acceptItem);
  }

  public void acceptOther() {
    inventory.setItem(5, acceptItem);
  }

  public void denyOther() {
    inventory.setItem(5, pendingItem);
  }

  public ItemStack getAcceptItem() {
    return acceptItem;
  }

  public ItemStack getRefuseItem() {
    return refuseItem;
  }

  public ItemStack[] getOwnItems() {
    ArrayList items = new ArrayList();
    for (Iterator localIterator = ownSlots.iterator(); localIterator.hasNext(); ) { int slot = ((Integer)localIterator.next()).intValue();
      items.add(inventory.getItem(slot));
    }
    return (ItemStack[])items.toArray(new ItemStack[items.size()]);
  }

  public ItemStack[] getOthersItems() {
    ArrayList items = new ArrayList();
    for (Iterator localIterator = otherSlots.iterator(); localIterator.hasNext(); ) { int slot = ((Integer)localIterator.next()).intValue();
      items.add(inventory.getItem(slot));
    }
    return (ItemStack[])items.toArray(new ItemStack[items.size()]);
  }

  public void setPendingItems(ItemStack[] items) {
    int i = 0;
    for (Iterator localIterator = otherSlots.iterator(); localIterator.hasNext(); ) { int slot = ((Integer)localIterator.next()).intValue();
      inventory.setItem(slot, items[i]);
      i++; }
  }

  public int getSize()
  {
    return size;
  }

  public boolean isAcceptSlot(int slot) {
    return slot == 3;
  }

  public boolean isRefuseSlot(int slot) {
    return slot == 4;
  }

  public boolean canUseSlot(int slot) {
    return ownSlots.contains(Integer.valueOf(slot));
  }

  private static String generateTitle(String playerName) {
    String title = "     You";
    while (title.length() + playerName.length() < 32) {
      title = title + " ";
    }
    return title += playerName;
  }
}