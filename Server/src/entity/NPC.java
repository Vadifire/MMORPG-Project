package entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class NPC extends Entity{

	public static final int MAX_NPCS = 10;
	static String npcName[] = new String[MAX_NPCS];

	
	public String getName(){
		return npcName[type];
	}
	
	
	public static String getRandomAdvice(){
		return "Never play this game.";
	}
	
	public static void loadNPCs(){
		BufferedReader input = null;
		String line = null;
		
		try {
			input = new BufferedReader(new FileReader(new File("data/npcs.cfg")));
	
			for (int i = 1; i<MAX_NPCS; i++){
				try{
					line = input.readLine().trim();
					int end = line.indexOf(44);
					String name = line.substring(0, end);
					npcName[i] = name;
					
				}
				catch (Exception e){
					//System.out.println("Failed to load NPC id"+i+".");
				}
				
			}
			input.close();
		}
		catch (Exception e) {
			System.out.println("Failed to load NPC config."); 
			}
	}
	
	
}