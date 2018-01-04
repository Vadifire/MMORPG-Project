package entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Item {
	
	public int itemId;
	static final int MAX_ITEMS = 10; //1 above the highest id
	public static final int STAT_LENGTH = 13; // 1 above highest id
	
	static String itemName[] = new String[MAX_ITEMS];
	static short equipSlot[] = new short[MAX_ITEMS];
	static int stackLimit[] = new int[MAX_ITEMS];
	static boolean firstVowel[] = new boolean[MAX_ITEMS];
	static short[][] stat = new short[MAX_ITEMS][STAT_LENGTH];
	Player owner;

	public Item (Player p, int id)
	{
		owner = p;
		itemId = id;
	}
	public static void loadItemData()
	{		
		BufferedReader input = null;
		String line = null;
		try {
			input = new BufferedReader(new FileReader(new File("data/items.cfg")));
	
			for (int i = 1; i<MAX_ITEMS; i++){
				try{
					line = input.readLine().trim();
					int end = line.indexOf(44);
					String name = line.substring(0, end);
					if (name.startsWith("E") || name.startsWith("A") || name.startsWith("U") || name.startsWith("I") || name.startsWith("O"))
						firstVowel[i] = true;
					itemName[i] = name;
					
					line = line.substring(end+1);
					end = line.indexOf(44);
					stackLimit[i] = Integer.parseInt(line.substring(1,end));
					
					line = line.substring(end+1);
					end = line.indexOf(44);
					equipSlot[i] = Short.parseShort(line.substring(1,end));
					
					for (int j = 0; j < stat[0].length; j++){ //load equip stats
						line = line.substring(end+1);
						end = line.indexOf(44);
						stat[i][j] = Short.parseShort(line.substring(1,end));
					}
					
				}
				catch (Exception e){
					//System.out.println("Failed to load item id"+i+".");
				}
				
			}
			input.close();
		}
		catch (Exception e) {
			System.out.println("Failed to load item config."); 
			}
	}
	public short getEquipSlot()
	{
		return equipSlot[itemId];
	}
	
	public short getEmptySlot()
	{
		if (stackLimit[itemId] > 1){
			System.out.println("Stackable items not yet implemented!");
			return -1;
		}
		for (short i = 0; i <28; i++){
			if (owner.items[i].itemId == 0)
				return i;
		}
		return -1; //could not find a free slot
	}
	
	
	public String getItemName()
	{
		return itemName[itemId];
	}
	
	public int getStat(int statId)
	{
		return stat[itemId][statId];
	}
	
	public String getAnItemName()
	{
		if (firstVowel[itemId])
			return "an "+itemName[itemId].toLowerCase();
		return "a "+itemName[itemId].toLowerCase();
	}

}
