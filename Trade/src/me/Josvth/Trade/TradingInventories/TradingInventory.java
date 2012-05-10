package me.Josvth.Trade.TradingInventories;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface TradingInventory extends InventoryHolder{

	public void accept();

	public void deny();

	public ItemStack[] getOfferedItems();

	public ItemStack[] getPendingItems();

	public void setPendingItems(ItemStack[] items);

	public ItemStack getAcceptItem();

	public ItemStack getRefuseItem();
	
	public int getSize();

	public boolean isAcceptSlot(int slot);

	public boolean isRefuseSlot(int slot);

	public boolean canUseSlot(int slot);

	public void acceptOther();

	public void denyOther();
}
