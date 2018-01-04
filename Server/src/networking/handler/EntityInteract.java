package networking.handler;

import networking.packets.*;
import main.*;
import map.Room;

import com.esotericsoftware.kryonet.Connection;

import entity.Entity;
import entity.NPC;
import entity.Player;

import java.lang.Math;

public class EntityInteract{
	
	
	public static void handle(Connection c, PacketInteract packet){
		Player p = GameServer.players.get(c.getID());
		Room r = Engine.rooms.get(p.room);
		Entity e = r.roomEntities.get(packet.interactId);
		
		p.moveToEntity(e, 0);
		
		if (e instanceof Player){//PVP
			p.agroId = e.roomIndex;
		}
		else{//npc
			p.interactType = packet.interactType;
			p.interactEntityId = e.roomIndex;
			if (p.inRange(e, 0)){
				interact(p);
			}
		}
	}
	
	public static void interact(Player p){ //Once distance is close
		p.moving = false;
		Entity e = Engine.rooms.get(p.room).roomEntities.get(p.interactEntityId);
		p.interactEntityId = -1;
		switch (e.type){
		case 3: //Kris
			p.showInterface((byte)1,p.username+": Hello there. What's your name?",1);
			p.nextLine = 1;
			break;
		case 4: //Goleth
			p.showInterface((byte)1,"Goleth: I love me some Wukong.",4);
			p.nextLine = 0;
			break;
		}
	}
}