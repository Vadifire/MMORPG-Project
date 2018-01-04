package map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import main.GameClient;
import draw.GameCanvas;

public class Room
{
	public int roomId;
	public int width, height;
	public Tile[][] roomTiles;
	public int TILE_SIZE = 16;
	BufferedImage roomImage;
	
	public Room(int id){
		roomId = id;
		try {
		    roomImage = ImageIO.read(new File("data/maps/room"+roomId+".png"));
		    height = roomImage.getHeight();
		    width = roomImage.getWidth();
			roomTiles = new Tile[width/TILE_SIZE][height/TILE_SIZE];
		} catch (IOException e) {e.printStackTrace();}

	}
	public void populateTiles()
	{
		for (int i = 0; i < width/TILE_SIZE; i++)
			for (int j = 0; j < height/TILE_SIZE; j++)
				roomTiles[i][j] = new Tile(i*TILE_SIZE,j*TILE_SIZE);
	}
	
	public void draw (Graphics g)
	{	
		g.drawImage(roomImage, 0, 0
				, GameCanvas.WIDTH, GameCanvas.interfaceY
				, (GameClient.roomEntities.get(GameClient.playerId).x-GameCanvas.WIDTH/2) 
				, (GameClient.roomEntities.get(GameClient.playerId).y-GameCanvas.interfaceY/2) 
				, (GameClient.roomEntities.get(GameClient.playerId).x+GameCanvas.WIDTH/2) 
				, (GameClient.roomEntities.get(GameClient.playerId).y+GameCanvas.interfaceY/2) 
				, null);
	}

	
	public static void loadRooms()
	{
		Room r0 = new Room(0);
		r0.populateTiles();
		r0.load();
		GameClient.rooms.put(0, r0);
	}
	
	public void save()
	{
		BufferedWriter output = null;
		try {
			File file = new File("data/tiles/room"+roomId+".tiles");
			output = new BufferedWriter(new FileWriter(file));

			for (int j = 0; j < height/TILE_SIZE; j++){
				String line = "";
				for (int i = 0; i < width/TILE_SIZE; i++){
					if (roomTiles[i][j].solid == true)
						line+="1";
					else
						line+="0";
				}
				line+="\n";
				output.write(line);
			}


		} catch ( Exception e ) {
			e.printStackTrace();
	    } 
	    
		if ( output != null ){
			try {
	        	output.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void load()
	{
		BufferedReader input = null;
		try {
			File file = new File("data/tiles/room"+roomId+".tiles");
			input = new BufferedReader(new FileReader(file));
			
			for (int j = 0; j < height/TILE_SIZE; j++){
				try{
					String line = input.readLine();
					for (int i = 0; i < width/TILE_SIZE; i++){
						try{
							if (line.charAt(i)==49) //49 is ascii for 1
								roomTiles[i][j].solid = true;
							}catch(Exception e){}
					}
				}catch(Exception e){}
			}
			

			
		} catch ( Exception e ) { //New save / corrupted save
	    } 
	    
		if ( input != null ){
			try {
	        	input.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	
}