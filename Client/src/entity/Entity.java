package entity;

import input.MouseInput;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GameClient;
import networking.packets.PacketInteract;
import draw.GameCanvas;

public class Entity
{	
	public int x, y;
	public int width, height;
	public int newX, newY, lastX, lastY;
	public int stat[] = new int[14];
	public int id;
	public int type; //1 is main player, 2 is external player
	public long newTime;
	public long lastTime;
	public boolean moving = false;
	public boolean awaitingMovement = false;
	public String lastSaid;
	public long lastSaidTime;
	public long lastDamage;
	
	public static BufferedImage[] entityImage = new BufferedImage[10];
	
	public Entity(int type){
		this.type = type;
		width = entityImage[type].getWidth();
		height = entityImage[type].getHeight();
	}

	public static void initEntityImages()
	{
		for (int i = 1; i < entityImage.length; i++){
			try {
				entityImage[i] = ImageIO.read(new File("data/entity/entity"+i+".png"));
			} catch (IOException e) {}
		}
	}
	
	public boolean collision(int x, int y){
		if (x>=this.x-width/2 && x <=this.x+width/2 && y>=this.y-height/2 && y<this.y+height/2)
			return true;
		return false;
	}
	
	public String getName(){
		switch (type){
		case 1:
			return GameClient.username;
		case 2:
			return "Player";
		case 3:
			return "Kristopher";
		case 4:
			return "Goleth";
		default:
			return "Entity";
		}
	}
	
	public String getActionString(){
		switch (type){
		case 2:
			return "attack";
		default:
			return "talk-to";
		}
	}
	
	public void drawHealth(Graphics g){
		//draw health bar
		if (System.currentTimeMillis() < lastDamage+5000){
			int healthX = (stat[13])/25 ;
			g.setColor(Color.green);
			g.fillRect(GameCanvas.getScreenX(x)-20, GameCanvas.getScreenY(y-height+5), healthX, 4);
			g.setColor(Color.red);
			g.fillRect(GameCanvas.getScreenX(x)-20+healthX, GameCanvas.getScreenY(y-height+5), 40-healthX, 4);
		}
	}
	
	public void draw(Graphics g)
	{			
		if (GameCanvas.getScreenY(y)-entityImage[type].getHeight() >GameCanvas.interfaceY)
			return;
		double scaler = ((double)(System.currentTimeMillis()-newTime))/(double)((newTime-lastTime));
		if (scaler <= 1){
			x = (int)(lastX+(newX - lastX)*(scaler));
			y = (int)(lastY+(newY - lastY)*(scaler));
		}
		else{
			x = newX;
			y = newY;
		}
			
		if (type > 0){
			g.drawImage(entityImage[type],GameCanvas.getScreenX(x-width/2),GameCanvas.getScreenY(y-height/2),null);
		}

	}
}