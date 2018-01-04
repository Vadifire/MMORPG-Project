package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import networking.packets.*;
import networking.*;
import entity.*;

import com.esotericsoftware.kryonet.Server;

public class GameServer
{
	
	public final static int SERVER_VERSION = 2;	

	public static Engine engine; //main runnable Engine that processes all game updates.

	public static Server server;
	public static final int PORT = 27960;
	public static ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>();
	
	public static int playerCount;
	
	public static void main(String[] args) throws IOException{
		playerCount = 0;
		server = new Server();
		server.getKryo().register(PacketAddEntity.class);
		server.getKryo().register(PacketRemoveEntity.class);
		server.getKryo().register(PacketSendMessage.class);
		server.getKryo().register(PacketSendClick.class);
		server.getKryo().register(PacketSendKey.class);
		server.getKryo().register(PacketSendButton.class);
		server.getKryo().register(PacketUpdateEntityLoc.class);
		server.getKryo().register(PacketLogin.class);
		server.getKryo().register(PacketUpdateSlot.class);
		server.getKryo().register(PacketUseItem.class);
		server.getKryo().register(PacketSwapSlots.class);
		server.getKryo().register(PacketInteract.class);
		server.getKryo().register(PacketShowInterface.class);
		server.getKryo().register(PacketSendStat.class);


		server.bind(PORT, PORT);
		server.start();
		server.addListener(new ServerListener());
		System.out.println("The Server is running on port "+PORT);
		
		engine = new Engine(); //main Engine thread
		System.out.println("The Engine is running");
	}

}