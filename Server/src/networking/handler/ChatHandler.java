package networking.handler;

import networking.packets.*;
import main.*;

import com.esotericsoftware.kryonet.Connection;

import entity.Entity;
import entity.Player;


public class ChatHandler
{
	public static void handle(Connection c, PacketSendMessage packet)
	{
		Player p = GameServer.players.get(c.getID());
		try{
		if (packet.message.startsWith("/")) //commands
		{
			packet.message = packet.message.toLowerCase();
			if (packet.message.startsWith("coords",1)){
				p.sendMessage("Coords: ("+p.x+","+p.y+")");
			}
			else if (packet.message.startsWith("players",1)){
				p.sendMessage("There are "+GameServer.playerCount+" players connected."); 
			}
			else if (packet.message.startsWith("printvisible",1)){
				for (Entity e: p.visibleEntities.values()){
					System.out.println("Entity "+e.roomIndex+" is visible");
				}

			}
			else if (packet.message.startsWith("item",1)){
				int item = Integer.parseInt(packet.message.substring(6));
				if (!p.addItem(item,1))
					p.sendMessage("Not enough room to add this item.");
			}
			else if (packet.message.startsWith("clearinv",1)){
				for (int i = 0; i < 28; i++){
					p.removeItemSlot(i, 1); //needs to be updated to itemsN later
				}
			}
			else if (packet.message.startsWith("speed",1)){
				p.movementSpeed = Integer.parseInt(packet.message.substring(7));
				p.sendMessage("Your movement speed is now "+p.movementSpeed+".");
			}
		}
		
		else{
			for (Player p2: GameServer.players.values()){
				if (p2.room == p.room && p2.visibleEntities.get(p.roomIndex)!= null){
					p2.sendMessageOverhead(p.username+": "+packet.message, p.roomIndex);
				}
			}
		}}catch(Exception e){}
	}
}