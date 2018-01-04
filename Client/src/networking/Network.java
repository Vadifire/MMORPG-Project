package networking;

import java.io.IOException;

import main.GameClient;
import draw.Chatbox;
import draw.GameCanvas;
import entity.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import networking.packets.*;

public class Network extends Listener {

	public Client client;
	String ip = "";
	int port = 27960;
	
	public void connect(){
		client = new Client();
		client.getKryo().register(PacketAddEntity.class);
		client.getKryo().register(PacketRemoveEntity.class);
		client.getKryo().register(PacketSendMessage.class);
		client.getKryo().register(PacketSendClick.class);
		client.getKryo().register(PacketSendKey.class);
		client.getKryo().register(PacketSendButton.class);
		client.getKryo().register(PacketUpdateEntityLoc.class);
		client.getKryo().register(PacketLogin.class);
		client.getKryo().register(PacketUpdateSlot.class);
		client.getKryo().register(PacketUseItem.class);
		client.getKryo().register(PacketSwapSlots.class);
		client.getKryo().register(PacketInteract.class);
		client.getKryo().register(PacketShowInterface.class);
		client.getKryo().register(PacketSendStat.class);

		client.addListener(this);
		
		client.start();
		try {
			client.connect(5000, ip, port, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Packet Handler
	public void received(Connection c, Object o){
		if(o instanceof PacketSendMessage){
			PacketSendMessage packet = (PacketSendMessage) o;
			if (packet.id > 0){
				GameClient.roomEntities.get(packet.id).lastSaid = 
						packet.message.substring(packet.message.indexOf(":")+1);
				GameClient.roomEntities.get(packet.id).lastSaidTime = System.currentTimeMillis();
			}
			if (GameClient.chatbox != null)
				GameClient.chatbox.addNewMessage(packet.message);
		}
		else if (o instanceof PacketUpdateEntityLoc){
			PacketUpdateEntityLoc packet = (PacketUpdateEntityLoc) o;
			Entity e = GameClient.roomEntities.get(packet.id);
			if (e!= null){
					e.lastTime = e.newTime;
					e.lastX = e.newX;
					e.lastY = e.newY;
					e.newTime = System.currentTimeMillis();
					e.newX = packet.x;
					e.newY = packet.y;
				}
		}
		else if (o instanceof PacketSendStat){
			PacketSendStat packet = (PacketSendStat) o;
			Entity e = GameClient.roomEntities.get(packet.entityId);
			if (packet.statIndex == 13){ //health, taking and applying damage
				int healthLoss = e.stat[13]-packet.stat;
				e.lastDamage=System.currentTimeMillis();
			}
			e.stat[packet.statIndex] = packet.stat;
		}
		else if (o instanceof PacketAddEntity){
			PacketAddEntity packet = (PacketAddEntity) o;
			Entity e = new Entity(packet.type);
			if (packet.type == 1)
				GameClient.playerId = packet.id;
			e.x=e.newX=e.lastX = packet.x;
			e.y=e.newY=e.lastX = packet.y;
			e.lastTime=e.newTime=System.currentTimeMillis();
			e.id = packet.id;
			System.out.println("I was told to add an entity of type "+packet.type+" with id "+packet.id);
			GameClient.roomEntities.put(packet.id, e);
		}
		else if (o instanceof PacketRemoveEntity){
			PacketRemoveEntity packet = (PacketRemoveEntity) o;
			System.out.println("I was told to remove entity id "+packet.id);
			GameClient.roomEntities.remove(packet.id);
		}
		else if (o instanceof PacketShowInterface){
			PacketShowInterface packet = (PacketShowInterface)o;
			GameClient.interfaceId = packet.interfaceId;
			GameClient.interfaceString = packet.interfaceString;			
			GameClient.interfaceInt = packet.interfaceInt;
			if (GameClient.interfaceId  == 1){
				Chatbox.orderNPCString();
			}

		}
		else if (o instanceof PacketLogin){
			PacketLogin packet = (PacketLogin) o;
			switch (packet.loginInt){
				case 1: //successful login
					GameClient.loggedIn = true;
					GameClient.login.dispose();
					GameClient.chatbox = new Chatbox();
					GameCanvas.createGameWindow();
					break;
				
				case 2: //invalid password
					Login.status.setText("Invalid password.");
					break;
					
				case 3:
					Login.status.setText("You are already connected.");
					break;
					
				case 4:
					Login.status.setText("Client is oudated - seek update.");
					break;
				
			}
		}
		else if (o instanceof PacketSendKey){
			PacketSendKey packet = (PacketSendKey)o;
			GameClient.playerId = packet.key; //used to set player id
		}
		else if (o instanceof PacketUpdateSlot){
			PacketUpdateSlot packet = (PacketUpdateSlot)o;
			GameClient.items[packet.slot] = packet.item;
		}
	}
}
