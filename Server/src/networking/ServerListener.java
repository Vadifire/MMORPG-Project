package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import entity.Player;
import entity.PlayerSave;
import networking.packets.*;
import main.*;
import networking.handler.*;
public class ServerListener extends Listener
{
	public void connected(Connection c){
		Player player = new Player();
		player.c = c;
		
		GameServer.players.put(c.getID(), player);
		System.out.println("Connection received from "+c.getRemoteAddressTCP()+", id: "+c.getID());
		GameServer.playerCount++;

		PacketSendKey packet = new PacketSendKey();  //Client will use this to get connection # in array.
		packet.key = c.getID();
		c.sendTCP(packet);
	}
	
	//Packet Handler
	public void received(Connection c, Object o){
		try{
			if (o instanceof PacketSendClick)
				MouseClick.handle(c, (PacketSendClick)o);
			else if (o instanceof PacketUseItem)
				ItemUse.handle(c, (PacketUseItem)o);
			else if (o instanceof PacketInteract)
				EntityInteract.handle(c, (PacketInteract)o);
			else if (o instanceof PacketSwapSlots)
				SwapSlots.handle(c, (PacketSwapSlots)o);
			else if (o instanceof PacketSendKey)
				KeyInput.handle(c, (PacketSendKey)o);
			else if(o instanceof PacketSendMessage) 
				ChatHandler.handle(c, (PacketSendMessage)o);
			else if (o instanceof PacketLogin)
				Login.handle(c, (PacketLogin)o);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void disconnected(Connection c){		
		Player p = GameServer.players.get(c.getID()); //Disconnecting forces room exit
		if (p.loggedIn){
			PlayerSave.save(p);
			PacketSendMessage packet2 = new PacketSendMessage(); 
			packet2.message = "\n"+p.username+" has left the game.";
			GameServer.server.sendToAllExceptTCP(c.getID(), packet2);
		}
		try{
			Engine.rooms.get(p.room).exitRoom(p);
		}catch(Exception e){}
		GameServer.players.remove(c.getID());

		System.out.println("Connection dropped: "+c.getRemoteAddressTCP()+", id: "+c.getID());
		GameServer.playerCount--;
		
	}
}