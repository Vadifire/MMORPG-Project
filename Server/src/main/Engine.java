package main;

import map.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import entity.Item;
import entity.NPC;

public class Engine implements Runnable 
{
	
	public static ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();

	public static final int SERVER_TICK = 100;
		
    /**
     * The class thread.
     */
    private Thread engineThread = new Thread(this);
    /**
     * Set true if the engine should run.
     */
    private boolean engineRunning;
	
	public Engine() 
	{
		Item.loadItemData();
		NPC.loadNPCs();
		rooms.put(0,new Room(0)); //Add default room 0 for testing
		engineRunning = true;
        engineThread.start();
    }
	
	
    @Override
	public void run() 
	{
        long curTime;        

        while (engineRunning) {
            curTime = System.currentTimeMillis(); //record time at beginning

			for (Room r : rooms.values())
			{
				r.updateRoom(); //update all rooms every tick
			}
            try {
				long sleepTime = SERVER_TICK - (System.currentTimeMillis() - curTime);
				if (sleepTime < 0)
					System.out.println("NOT ENOUGH TIME TO SLEEP! GAME SLOWED DOWN!");
                Thread.sleep(sleepTime);
            } catch (Exception e) {
            }
        }
    }
	
	
}