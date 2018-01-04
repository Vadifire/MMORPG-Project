package entity;

import java.lang.Math;

import networking.packets.*;
import main.Engine;
import main.GameServer;

public class Entity
{
	public double x, y;
	public double unspentX, unspentY; //for combat following
	public int width = 32;
	public int height = 32;
	public int type = 2;
	public int room, roomIndex;
	
	public int stat[] = new int[14];
	public long cooldown[] = new long[5];
	public int combatLevel;
	public int agroId = -1;
	public int autoRange = 5;
	
	public int moveX;
	public int moveY;
	public boolean moving = false;
	public int movementSpeed;
	public double xVector;
	public double yVector;
	public String name;
	
	public Entity(){
		stat[13]=1000;//full health at start
		movementSpeed = 120;
	}
	
	public void update(){
		updateLoc(x,y);
	} //should be overwritten by specific entity 
	
	public void processCombat(){
	} //should be overwritten by specific entity 
	
	
	public void appendDamage(int damage){
		stat[13]-=damage;
		if (stat[13] <= 0){
			if (this instanceof Player){/*
				updateLoc(700,500);
				((Player)this).sendMessage("You died. :[");
				moving = false;
				agroId = -1;
				((Player)this).interactEntityId = -1;
				*/
			}
			stat[13] = 1000;
		}
		PacketSendStat packet = new PacketSendStat();
		packet.stat = (short)stat[13];
		packet.entityId = roomIndex;
		packet.statIndex = (byte)13;
		for (Player p : GameServer.players.values()){
			if (p.visibleEntities.containsKey(roomIndex)){
				p.c.sendTCP(packet);
			}
		}
	}
	
	public boolean inRange(Entity e, int range){
		if (x > e.x){
			if (x-range-e.width/2-width/2 > e.x)
				return false;
		}
		else if (x < e.x){
			if (x+range+e.width/2+width/2 < e.x)
				return false;
		}
		if (y > e.y){
			if (y-range-e.height/2-height/2 > e.y)
				return false;
		}
		else if (y < e.y){
			if (y+range+e.height/2+height/2 < e.y)
				return false;
		}
		return true;
	}
		
	public boolean inVisionRange(Entity e)
	{
		return (Math.abs(x-e.x) < 480 && Math.abs(y-e.y) < 380); //vision range specified here
	}
	
	public void moveToEntity(Entity e, int range){

		double xRange = range+width/2+e.width/2;
		double yRange = range+height/2+e.height/2;

		double rad = Math.atan2(e.y-y,e.x-x);
		int goX = (int) (e.x-(Math.cos(rad)*xRange));
		int goY = (int) (e.y-(Math.sin(rad)*yRange));
		moveTo(goX,goY);
	}
	
	public void moveTo(int xVar, int yVar){
		moving = true;
		moveX = xVar;
		moveY = yVar;
		double rad = Math.atan2(moveY-y,moveX-x);
		xVector = (Math.cos(rad)*movementSpeed*Engine.SERVER_TICK)/1000;
		yVector = (Math.sin(rad)*movementSpeed*Engine.SERVER_TICK)/1000;
	}
	
	public void updateLoc(double x, double y) //Notifies all interested clients when an entity enters/exists vision range.
	{
		this.x = x;
		this.y = y;
		boolean contains, inRange;
		for (Player p2: GameServer.players.values()){ //For every player, p2
			contains = p2.visibleEntities.containsKey(roomIndex);
			inRange = inVisionRange(p2) && p2.room==room;
			if (!contains && inRange){ //The guy that just moved is added
				System.out.println("Adding entity "+roomIndex+" for "+p2.roomIndex+".");
				p2.visibleEntities.put(roomIndex,this);
				PacketAddEntity packet = new PacketAddEntity();
				packet.id = roomIndex; packet.x = (int) x; packet.y = (int) y; packet.type = type;
				if(p2==this){
					packet.type = 1;
				}
				GameServer.server.sendToTCP(p2.c.getID(), packet);
				if (roomIndex != p2.roomIndex && this instanceof Player){ //if we're updating a players location, we also need to add what we've ran into to our client
					Player p = (Player)this;
					((Player)this).visibleEntities.put(p2.roomIndex,p2);
					PacketAddEntity packet2 = new PacketAddEntity();
					packet2.id = p2.roomIndex; packet2.x = (int) p2.x; packet2.y = (int) p2.y; packet2.type = p2.type;
					p.visibleEntities.put(p2.roomIndex,p2);
					GameServer.server.sendToTCP(p.c.getID(), packet2);
				}
			}
			else if (contains && inRange){ //The guy that just moved has changed locations
				PacketUpdateEntityLoc packet = new PacketUpdateEntityLoc();
				packet.id = roomIndex; packet.x = (int) x; packet.y = (int) y;
				GameServer.server.sendToUDP(p2.c.getID(), packet);
				
			}
			else if (contains && !inRange){ //The guy that just moved is leaving players range of vision
				System.out.println("Removing entity "+roomIndex+" for "+p2.roomIndex+".");
				p2.visibleEntities.remove(roomIndex,this);
				PacketRemoveEntity packet = new PacketRemoveEntity();
				packet.id = roomIndex;
				GameServer.server.sendToTCP(p2.c.getID(), packet);
				if (roomIndex != p2.roomIndex && this instanceof Player){ //if we're updating a players location, we also need to remove to our client
					Player p = (Player)this;
					((Player)this).visibleEntities.put(p2.roomIndex,p2);
					PacketRemoveEntity packet2 = new PacketRemoveEntity();
					packet2.id = p2.roomIndex;
					p.visibleEntities.remove(p2.roomIndex,p2);
					GameServer.server.sendToTCP(p.c.getID(), packet2);
				}
			}
			
		}
	}
}