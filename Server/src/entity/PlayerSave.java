package entity;

import java.io.*;

import main.GameServer;
import entity.Item;
import networking.packets.PacketSendStat;
import networking.packets.PacketUpdateSlot;

public class PlayerSave {
	
	public static void save(Player p)
	{
		try {
			File file = new File("data/saves/"+(p.username.toLowerCase())+".sav");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(p.password+"\n");
			output.write(p.room+"\n");
			output.write((int)(p.x)+"\n");
			output.write((int)(p.y)+"\n");
        	output.close();
			
			File itemFile = new File("data/itemsaves/"+(p.username.toLowerCase())+".isav");
			output = new BufferedWriter(new FileWriter(itemFile));
			for (int i = 0; i<p.items.length; i++){
				output.write(p.items[i].itemId+"\n");
			}
        	output.close();

		} catch ( Exception e ) {
			e.printStackTrace();
	    } 
	    
	}
	
	public static void load(Player p)
	{
		try {
			File file = new File("data/saves/"+(p.username.toLowerCase())+".sav");
			BufferedReader input = new BufferedReader(new FileReader(file));
			p.password = input.readLine(); //password
			p.room = Integer.parseInt(input.readLine());
			p.x= p.x= Integer.parseInt(input.readLine());
			p.y= p.y= Integer.parseInt(input.readLine());
			input.close();
		} catch ( Exception e ) { //New save / corrupted save
			//e.printStackTrace();
			p.x = p.x = 700;
			p.y = p.y = 500;
			p.room = 0;
	    } 
		try{ //load items
			File file = new File("data/itemsaves/"+(p.username.toLowerCase())+".isav");
			BufferedReader input = new BufferedReader(new FileReader(file));
			for (int i = 0; i < p.items.length; i++){
				try{
					p.items[i] = new Item(p,Integer.parseInt(input.readLine()));
				}
				catch(Exception e){
					p.items[i] = new Item(p,0); // empty slot
				}

				PacketUpdateSlot packet = new PacketUpdateSlot();
				packet.slot = (short)i; packet.item = (short)p.items[i].itemId;
				GameServer.server.sendToTCP(p.c.getID(),packet);
			}
			input.close();
		} 
		catch (Exception e) {			
			for (int i = 0; i < p.items.length; i++){
				p.items[i] = new Item(p,0); // empty slot
			}
		}
	    
	}

}
