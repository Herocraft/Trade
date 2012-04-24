package me.Josvth.Trade;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TradeInventory implements InventoryHolder{
	
	public static final ItemStack INVENTORY_SPERATOR = new ItemStack(280);
	public static final ItemStack ACCEPT_ITEM = new ItemStack(35,1,(short)1, (byte)5);
	public static final ItemStack ACCEPT_ITEM_PENDING = new ItemStack(35,1,(short)1, (byte)8);
	public static final ItemStack REFUSE_ITEM = new ItemStack(35,1,(short)1, (byte)5);
	
	static final Integer[] LEFT_SLOTS = {0,1,2,3,9,10,11,12,18,19,20,21};
	static final Integer[] RIGHT_SLOTS = {5,6,7,8,14,15,16,17,23,24,25,26};
	static final Integer[] SEPERATOR_SLOTS = {13};
	static final int ACCEPT_SLOT = 22;
	static final int REFUSE_SLOT = 4;
	
	Inventory inventory;
	
	public TradeInventory(Trade instance, String name1, String name2){
				
		inventory = instance.getServer().createInventory(this, 27, generateTitle(name1, name2));
		
		for(int slot : SEPERATOR_SLOTS){
			inventory.setItem(slot, INVENTORY_SPERATOR);
		}
	
		inventory.setItem(ACCEPT_SLOT, ACCEPT_ITEM);
		
	}
	
	private String generateTitle(String name1, String name2){
		String title = "    " + name1;
		while (title.length() + name2.length() < 32){
			title += " ";
		}
		return title += name2;
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
}
