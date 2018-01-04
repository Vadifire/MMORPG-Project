package main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import map.Room;
import networking.Login;
import networking.Network;
import entity.*;
import draw.*;

public class GameClient
{

	public static Chatbox chatbox;
	public static boolean loggedIn = false;
	public static int roomId = 0;
	public static String username;
	public static int playerId;
	
	public static boolean mapMode = false;
	public static byte tabInterface;
	public static int interfaceId;
	public static String interfaceString;
	public static int interfaceInt;
	public static int[] items = new int[250];
	
	public static int CLIENT_VERSION = 2;

	public static Network network = new Network();
	public static GameCanvas gc;
	public static ConcurrentHashMap<Integer,Entity> roomEntities = new ConcurrentHashMap<Integer,Entity>(); 
	public static Map<Integer,Room> rooms = new HashMap<Integer,Room>(); 
	
	public static Login login;
	
	public static void main (String args[])
	{
		System.out.println("Client Started");
		
		Entity.initEntityImages();
		Item.initItemImages();
		Room.loadRooms();
		login = new Login();
		network.connect();
	}
	


}
