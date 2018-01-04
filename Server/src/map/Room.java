package map;

import networking.packets.*;
import entity.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import main.GameServer;

public class Room
{
	public ConcurrentHashMap<Integer, Entity> roomEntities = new ConcurrentHashMap<Integer, Entity>();
	public int playerCount;
	public int roomId;
	public int nextFreeIndex;
	public int roomX, roomY, width, height;
	
	public Room(int id)
	{
		roomId = id;
		playerCount = 0;
		nextFreeIndex = 1;
		loadRoom();
	}
	
	public void enterRoom(Player p)
	{	
		p.room = roomId;
		p.roomIndex = nextFreeIndex;
		System.out.println("Player "+p.c.getID()+" has entered room "+roomId+".");;
 		
		for (Entity e: roomEntities.values()) //must add all already existing room entities to client
		{
			if (p.inVisionRange(e) && !p.visibleEntities.containsKey(e.roomIndex))
			{
				PacketAddEntity packet = new PacketAddEntity();
				packet.x = (int) e.x; packet.y = (int) e.y; packet.type = e.type; packet.id = e.roomIndex;
				p.c.sendTCP(packet);
				p.visibleEntities.put(e.roomIndex, e);
			}
		}
		roomEntities.put(nextFreeIndex, p);
 		nextFreeIndex++;
 		
		PacketAddEntity packet = new PacketAddEntity(); 
		packet.type = 1;
		packet.id = p.roomIndex;
		packet.x = (int)p.x;
		packet.y = (int)p.y;
		p.c.sendTCP(packet);
		p.visibleEntities.put(p.roomIndex, p);
	
	}
	public void exitRoom(Player p)
	{		
		System.out.println("Player "+p.c.getID()+" has left room "+roomId+".");
		p.updateLoc(-Integer.MAX_VALUE, -Integer.MAX_VALUE); //get player out of everyone's vision for clients
		
		for (Entity e : p.visibleEntities.values()) //must remove all entities from this client
		{
			PacketRemoveEntity packet = new PacketRemoveEntity();
			packet.id = e.roomIndex;
			p.c.sendTCP(packet);
		}
		
		p.visibleEntities.clear(); //clear all entities
		roomEntities.remove(p.roomIndex);
	}
	
	public void updateRoom()
	{
		for(Entity e : roomEntities.values()){
			e.update();
		}
		for(Entity e : roomEntities.values()){
			e.processCombat();
		}
	}
	
	public void addNPC(int type, int x, int y){
		NPC n = new NPC();
		n.type = type;
		n.x=n.x=x;
		n.y=n.y=y;
		n.room = roomId;
		n.roomIndex = nextFreeIndex;
		n.name = n.getName();
		roomEntities.put(nextFreeIndex, n);
		nextFreeIndex++;
	}
		
	public void loadRoom(){
		switch (roomId){
		case 0: // room 0

			addNPC(3,114,1398);
			addNPC(4,469,1248);

			break;
		}
		
	}
}