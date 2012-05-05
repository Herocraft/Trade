package me.Josvth.Trade.Inventories;

import java.util.ArrayList;

import me.Josvth.Trade.Trade;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TradeInventory implements InventoryHolder{

	int size;

	ArrayList<Integer> left_slots;
	ArrayList<Integer> right_slots;
	ArrayList<Integer> seperator_slots;
	int accept_slot;
	int refuse_slot;

	ItemStack seperator_item;
	ItemStack accept_item;
	ItemStack pending_item;
	ItemStack refuse_item;

	Inventory inventory;

	public TradeInventory(Trade instance, String name1, String name2, int rows){

		if (rows < 2) rows = 2;

		if (rows == 2){
			accept_slot = 4;
			refuse_slot = 9;
		}else{
			accept_slot = 4 + (rows - 3) * 5;
			refuse_slot = accept_slot + 10;
		}

		size = rows * 9;

		left_slots = new ArrayList<Integer>(rows*4);
		right_slots = new ArrayList<Integer>(rows*4);
		seperator_slots = new ArrayList<Integer>((rows*4)-2);

		for(int slot = 0; slot < size; slot++){

			if(slot == accept_slot || slot == refuse_slot) break;

			int collum = slot % 9;

			if(collum < 4){
				left_slots.add(slot);
			}else if(collum > 4){
				right_slots.add(slot);
			}else{
				seperator_slots.add(slot);
			}

		}

		inventory = instance.getServer().createInventory(this, size, generateTitle(name1, name2));

		String[] accept_item_string = instance.getConfig().getString("accept_item").split("[:]");
		String[] refuse_item_string = instance.getConfig().getString("refuse_item").split("[:]");
		String[] pending_item_string = instance.getConfig().getString("pending_item").split("[:]");
		String[] seperator_item_string = instance.getConfig().getString("seperator_item").split("[:]");
		
		try {
			if(accept_item_string.length == 1){
				accept_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0,Short.decode(accept_item_string[1]));
			}else{
				accept_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0);
			}
		} catch (NumberFormatException e) {
			instance.logWarning("Could not parse accept item from config. Using default!");
			accept_item = new ItemStack(35, 0, (short) 5);
		}

		try {
			if(refuse_item_string.length == 1){
				refuse_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0,Short.decode(accept_item_string[1]));
			}else{
				refuse_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0);
			}
		} catch (NumberFormatException e) {
			instance.logWarning("Could not parse refuse item from config. Using default!");
			refuse_item = new ItemStack(35, 0, (short) 14);
		}
		
		try {
			if(pending_item_string.length == 1){
				pending_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0,Short.decode(accept_item_string[1]));
			}else{
				pending_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0);
			}
		} catch (NumberFormatException e) {
			instance.logWarning("Could not parse pending item from config. Using default!");
			pending_item = new ItemStack(35, 0, (short) 8);
		}
		
		try {
			if(seperator_item_string.length == 1){
				seperator_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0,Short.decode(accept_item_string[1]));
			}else{
				seperator_item = new ItemStack(Integer.parseInt(accept_item_string[0]),0);
			}
		} catch (NumberFormatException e) {
			instance.logWarning("Could not parse seperate item from config. Using default!");
			seperator_item = new ItemStack(280, 0);
		}
		
		for(int slot : seperator_slots){
			inventory.setItem(slot, seperator_item);
		}

		inventory.setItem(accept_slot, accept_item);
		inventory.setItem(refuse_slot, refuse_item);
		
	}

	public static final String generateTitle(String name1, String name2){
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
	
	public int getSize() {
		return size;
	}

	public ArrayList<Integer> getLeftSlots() {
		return left_slots;
	}

	public ArrayList<Integer> getRightSlots() {
		return right_slots;
	}

	public ArrayList<Integer> getSeperatorSlots() {
		return seperator_slots;
	}

	public int getAcceptSlot() {
		return accept_slot;
	}

	public int getRefuseSlot() {
		return refuse_slot;
	}

	public ItemStack getSeperatorItem() {
		return seperator_item;
	}

	public ItemStack getAcceptItem() {
		return accept_item;
	}

	public ItemStack getPendingItem() {
		return pending_item;
	}

	public ItemStack getRefuseItem() {
		return refuse_item;
	}
	
	public boolean canUseSlot(int slot){		
		return left_slots.contains(slot);
	}

	public boolean isAcceptSlot(int slot){
		return slot == accept_slot;
	}

	public boolean isRefuseSlot(int slot) {
		return slot == refuse_slot;
	}
	
	public void setPending() {
		inventory.setItem(accept_slot, pending_item);		
	}
	
	public void resetPending(){
		inventory.setItem(accept_slot, accept_item);	
	}
}
