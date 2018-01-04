package networking.handler;

import networking.packets.*;
import main.*;
import map.Room;

import com.esotericsoftware.kryonet.Connection;

import entity.Entity;
import entity.NPC;
import entity.Player;

import java.awt.event.KeyEvent;
import java.lang.Math;

public class KeyInput{
	
	
	public static void handle(Connection c, PacketSendKey packet){
		Player p = GameServer.players.get(c.getID());

		switch (packet.key){
		case KeyEvent.VK_SPACE:
			nextLine(p);
			break;
		}
		
	}
	
	public static void nextLine(Player p){ //Once distance is close
		switch (p.nextLine){
		
		case 1: 
			p.showInterface((byte)1,"Kristopher: My name is Kristopher, but you can call me Kris.",3);
			p.nextLine++;
			break;
		case 2: 
			p.showInterface((byte)1,p.username+": Nice to meet you Kristopher. My name's "+p.username+
					". Do you have any advice for a new player?",1);
			p.nextLine++;
			break;
		case 3: 
			p.showInterface((byte)1,"Kristopher: Yeah, actually. "+NPC.getRandomAdvice(),3);
			p.nextLine++; //done
			break;
		case 4: 
			p.showInterface((byte)1,p.username+": Thanks for the advice Kris!",1);
			p.nextLine = 0;
			break;
		default:
			p.showInterface((byte)0,null,0);
			break;
		}

	}
}