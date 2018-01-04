package networking.handler;

import networking.packets.*;
import main.*;

import com.esotericsoftware.kryonet.Connection;

import entity.Item;
import entity.Player;
import entity.PlayerSave;

public class Login{
	
	public static void handle(Connection c, PacketLogin packet){
		Player p = GameServer.players.get((packet.loginInt - packet.loginInt % 1000)/1000); //ignore first 3 digits (client version)
		
		if (packet.loginInt%1000 != GameServer.SERVER_VERSION)
		{
			PacketLogin packet5 = new PacketLogin(); 
			packet5.loginInt = 4; //notify client that client is outdated 
			c.sendTCP(packet5);
			return;
		}
		
		p.username = packet.username;
		for (Player all : GameServer.players.values())
		{
			try{
			if (all.username.equalsIgnoreCase(p.username) && all.loggedIn)
			{
				PacketLogin packet5 = new PacketLogin(); 
				packet5.loginInt = 3; //notify client that user is already logged in
				c.sendTCP(packet5);
				return;
			}}catch(Exception e){}
		}
		
		PlayerSave.load(p);
		
		if (p.password == null || p.password.equalsIgnoreCase(packet.password)) //success
		{
			p.username = packet.username;
			p.password = packet.password; // in case new user, need to register password
			
			PacketSendMessage packet4 = new PacketSendMessage(); //Add this p for all new ps
			packet4.message = "\n"+p.username+" has joined the game.";
			GameServer.server.sendToAllExceptTCP(c.getID(), packet4);
			
			PlayerSave.save(p);

			PacketLogin packet5 = new PacketLogin(); 
			packet5.loginInt = 1; //notify client of success
			c.sendTCP(packet5);
			p.loggedIn = true;
			
			p.sendMessage("You have successfully connected.");
			Engine.rooms.get(p.room).enterRoom(p);
			
			for (int i = 28; i < 50; i++){ //apply stats of items equipped
				if (p.items[i].itemId != 0){
					for (int j = 0; j < Item.STAT_LENGTH; j++){
						p.stat[j] += p.items[i].getStat(j);
					}
				}
			}
			for (int i = 0; i < p.stat.length; i++){ //load stats
				if (i!=13){
					PacketSendStat packetStat = new PacketSendStat();
					packetStat.entityId = p.roomIndex;
					packetStat.statIndex = (byte)i;
					packetStat.stat = (short)p.stat[i];
					c.sendTCP(packetStat);
				}
			}
		}
		else
		{
			p.room = -1;
			PacketLogin packet5 = new PacketLogin(); 
			packet5.loginInt = 2; //notify client that password is invalid
			c.sendTCP(packet5);
		}
	}
}