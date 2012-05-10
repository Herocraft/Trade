package me.Josvth.Trade.TradingInventories;

import java.util.ArrayList;

import me.Josvth.Trade.Trade;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StandardInventory implements TradingInventory{
	
	Inventory inventory;
	
	ArrayList<Integer> ownSlots;
	ArrayList<Integer> otherSlots;
	ArrayList<Integer> seperatorSlots;
	
	ItemStack acceptItem = new ItemStack(35, 0, (short) 5);
	ItemStack refuseItem = new ItemStack(35, 0, (short) 14);
	ItemStack pendingItem = new ItemStack(35, 0, (short) 8);
	ItemStack seperatorItem = new ItemStack(280, 0);
	
	int acceptSlot;
	int refuseSlot;
	int statusSlot;
	
	int size;
	
	public StandardInventory(Trade instance, String otherPlayerName, int rows){
		
		size = rows * 9;
		
		inventory = instance.getServer().createInventory(this, size, generateTitle(otherPlayerName));
		
		acceptSlot = 3;
		refuseSlot = 4;
		statusSlot = 5;
		
		ownSlots = new ArrayList<Integer>(rows*4-1);
		otherSlots = new ArrayList<Integer>(rows*4-1);
		seperatorSlots = new ArrayList<Integer>(rows-1);
		
		for(int slot = 0; slot < rows * 9; slot++){
			if(slot != acceptSlot && slot != refuseSlot && slot != statusSlot){
				int collum = slot % 9;
				if(collum < 4){
					ownSlots.add(slot);
				}
				if(collum == 4){
					seperatorSlots.add(slot);
				}
				if(collum > 4){
					otherSlots.add(slot);
				}
			}
		}
		
		for(int slot : seperatorSlots){
			inventory.setItem(slot, seperatorItem);
		}
		
		inventory.setItem(acceptSlot, acceptItem);
		inventory.setItem(refuseSlot, refuseItem);
		inventory.setItem(statusSlot, pendingItem);
		
	}
	
	private static String generateTitle(String playerName){
		String title = "     You";
		while (title.length() + playerName.length() < 32){
			title += " ";
		}
		return title += playerName;
	}
	
	public Inventory getInventory() {
		return getInventory();
	}

	public void accept() {
		inventory.setItem(acceptSlot, pendingItem);
	}

	public void deny() {
		inventory.setItem(acceptSlot, acceptItem);		
	}

	public ItemStack getAcceptItem() {
		return acceptItem;
	}

	@Override
	public ItemStack getRefuseItem() {
		return refuseItem;
	}

	public ItemStack[] getOfferedItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(int slot : ownSlots){
			items.add(inventory.getItem(slot));
		}
		return items.toArray(new ItemStack[items.size()]);
	}

	public ItemStack[] getPendingItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(int slot : otherSlots){
			items.add(inventory.getItem(slot));
		}
		return items.toArray(new ItemStack[items.size()]);
	}

	public void setPendingItems(ItemStack[] items) {
		int i = 0;
		for(int slot : otherSlots){
			inventory.setItem(slot, items[i]);
			i++;
		}
	}

	public int getSize() {
		return size;
	}

	public boolean isAcceptSlot(int slot) {
		return slot == acceptSlot;
	}

	public boolean isRefuseSlot(int slot) {
		return slot == refuseSlot;
	}

	public boolean canUseSlot(int slot) {
		return ownSlots.contains(slot);
	}
}
