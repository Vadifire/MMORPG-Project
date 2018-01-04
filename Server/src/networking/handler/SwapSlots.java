package networking.handler; 

import networking.packets.*;
import main.*;

import com.esotericsoftware.kryonet.Connection;

import entity.Item;
import entity.Player;

import java.lang.Math;

public class SwapSlots{
	
	public static void handle(Connection c, PacketSwapSlots packet){
		Player p = GameServer.players.get(c.getID());
				
		PacketUpdateSlot packetUpdate = new PacketUpdateSlot()	;
		packetUpdate.slot = (short)packet.slot2;
		packetUpdate.item = (short)p.items[packet.slot1].itemId;
		GameServer.server.sendToTCP(p.c.getID(),packetUpdate);
		
		packetUpdate.slot = (short)packet.slot1;
		packetUpdate.item = (short)p.items[packet.slot2].itemId;
		GameServer.server.sendToTCP(p.c.getID(),packetUpdate);
		
		int tempItemId = p.items[packet.slot2].itemId;
		p.items[packet.slot2] = p.items[packet.slot1];
		p.items[packet.slot1] = new Item(p,tempItemId);

	}
}