package entity;

import com.esotericsoftware.kryonet.Connection;

import java.lang.Math;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.*;
import networking.handler.EntityInteract;
import networking.packets.*;

public class Player extends Entity
{
	
	//Location

	public int type = 2; //looks like multiplayer by default
	public int interactEntityId = -1;
	public byte interactType = -1;
	public int nextLine;
	
	public ConcurrentHashMap<Integer, Entity> visibleEntities = new ConcurrentHashMap<Integer, Entity>();
	
	public Item[] items = new Item[250];

		
	//Account
	public String username;
	public String password;
	public Connection c;
	public boolean loggedIn = false;
	
	public boolean addItem(int item, int amount){ //assumes amount is 0 atm
		Item i = new Item(this,item);
		int slot = i.getEmptySlot();
		if (slot == -1)
			return false;
		items[slot] = i;
		PacketUpdateSlot packet = new PacketUpdateSlot();
		packet.slot = (short)slot; packet.item = (short)item;
		GameServer.server.sendToTCP(c.getID(),packet);
		return true;
	}
	
	public boolean removeItem(int item, int amount){ //assumes amount is 0 atm
		int slot = -1;
		for (int i = 0; i < 28; i++){
			if (items[i].itemId == item){
				slot = i;
				items[i] = new Item(this,0); //clears item
				PacketUpdateSlot packet = new PacketUpdateSlot();
				packet.slot = (short)slot; packet.item = 0;
				GameServer.server.sendToTCP(c.getID(),packet);
				return true;
			}
		}
		return false;	
	}
	
	public boolean removeItemSlot(int slot,int amount){ //assumes amount is 0 atm
		if (items[slot].itemId == 0){
			return false;
		}
		items[slot] = new Item(this,0); //clears slot
		PacketUpdateSlot packet = new PacketUpdateSlot();
		packet.slot = (short)slot; packet.item = 0;
		GameServer.server.sendToTCP(c.getID(),packet);
		return true;
	}
	
	public void showInterface(byte id, String s, int anInt)
	{
		PacketShowInterface packet = new PacketShowInterface(); //Add this player for all new players
		packet.interfaceString = s;
		packet.interfaceId= id;
		packet.interfaceInt = anInt;
		c.sendTCP(packet);
	}
	
	public void sendMessage(String s)
	{
		PacketSendMessage packet = new PacketSendMessage(); //Add this player for all new players
		packet.message = s;
		c.sendTCP(packet);
	}
	
	public void sendMessageOverhead(String s, int id)
	{
		PacketSendMessage packet = new PacketSendMessage(); //Add this player for all new players
		packet.message = s;
		packet.id = id;
		c.sendTCP(packet);
	}
	
	public void processCombat(){
		try{
			if (agroId >= 0)//in combat
			{
				Entity e = Engine.rooms.get(room).roomEntities.get(agroId);
				if (e.x-autoRange-e.width/2-width/2 > x){
					x+= Math.min(unspentX,(e.x-autoRange-width/2-e.width/2)-x);
				}
				else if (e.x+autoRange+width/2+e.width/2 < x){
					x-= Math.min(unspentX, x-(e.x+autoRange+width/2+e.width/2));
				}
				
				
				if (e.y-autoRange-e.height/2-height/2 > y){
					y+= Math.min(unspentY,(e.y-autoRange-height/2-e.height/2)-y);
				}
				else if (e.y+autoRange+height/2+e.height/2 < y){
					y-= Math.min(unspentY, y-(e.y+autoRange+height/2+e.height/2));
				}

				if (!inVisionRange(e)){
					agroId=-1;
					updateLoc(x,y);
					return;
				}
				if(!inRange(e,0))
					moveToEntity(e, 0);
				if (System.currentTimeMillis()>cooldown[0]){//auto attack
						
					if (inRange(e,autoRange))
					{
						int damage = 50;
						if (damage > e.stat[13]){
							agroId = -1;//out of combat after death
						}
						e.appendDamage(damage);
						cooldown[0] = System.currentTimeMillis()+1000;
					}
				}
			}
		}
		catch (Exception e){
			agroId = -1; //most likely caused by other dcing
		}
		updateLoc(x,y);
	}
	
	public void update() //perform actions that player must do upon update.
	{
		if (!loggedIn)
			return;
		if (moving)
		{
			if (Math.sqrt((x-moveX)*(x-moveX)+(y-moveY)*(y-moveY)) <= (movementSpeed*Engine.SERVER_TICK)/1000)
			{
				moving = false;
				if (interactEntityId > -1){
					EntityInteract.interact(this);
				} 
				unspentX = Math.abs(xVector-(moveX-x));
				unspentY = Math.abs(yVector-(moveY-y));
				x = moveX;
				y = moveY;
			}
			else{
				unspentX=unspentY=0;
				x+=xVector;
				y+=yVector;
			}
		}
	}
}