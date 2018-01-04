package entity;

import input.MouseInput;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import networking.packets.PacketUseItem;
import main.GameClient;

public class Item {
	
	public static BufferedImage[] itemImage = new BufferedImage[10];
	
	
	public static void initItemImages()
	{
		for (int i = 1; i < itemImage.length; i++){
			try {
				itemImage[i] = ImageIO.read(new File("data/items/item"+i+".png"));
			} catch (IOException e) {}
		}
	}
	
	public static String getName(int itemId){
		switch(itemId){
		
		case 1:
			return "Iron sword";
		case 2:
			return "Diamond sword";
		default:
			return "Item";
		}
	}
	
	public static String getClickString(int itemId){
		switch (itemId){
		case 1:case 2:
			return "equip";
		default:
			return "use";
		}
	}
	
	public static void drawClickString(Graphics g,int x, int y){
		if (x > 790 || x < 568 || y < 531)
			return;
		short slot = (short)((x-568)/32 + ((y-531)/32)*7);

		int itemId = GameClient.items[slot];
		if (itemId != 0 && MouseInput.swapping == false && (MouseInput.itemClickSlot < 0 || MouseInput.itemClickSlot == slot)){
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 13));
			String clickS = getClickString(itemId);
			g.drawString(clickS.substring(0, 1).toUpperCase()+clickS.substring(1)+" "+getName(itemId), 2, 11);
			g.setFont(new Font("Arial", Font.PLAIN, 13));
			g.drawString(clickS, x, y);

		}
		else if (MouseInput.itemClickSlot>-1){
			MouseInput.swapping=true;
		}

	}
	
	public static void drawEquip(Graphics g, int slot){
		int id = GameClient.items[slot];
		if (id == 0) //empty slot
			return;
		int drawX, drawY;
		drawX = drawY = 0;
		switch(slot){
		case 28: drawX = 579; drawY = 541; break;
		case 29: drawX = 669; drawY = 541; break;
		case 30: drawX = 762; drawY = 541; break;
		case 31: drawX = 579; drawY = 585; break;
		case 32: drawX = 627; drawY = 585; break;
		case 33: drawX = 669; drawY = 585; break;
		case 34: drawX = 713; drawY = 585; break;
		case 35: drawX = 0; drawY = 585; break;
		case 36: drawX = 579; drawY = 627; break;
		case 37: drawX = 669; drawY = 627; break;
		case 38: drawX = 762; drawY = 627; break;
		}
		g.drawImage(itemImage[id],drawX, drawY,null);
	}

	public static void drawInv(Graphics g, int slot){
		int id = GameClient.items[slot];
		if (id == 0) //empty slot
			return;

		int drawX=571+(slot%7)*32;
		int drawY=537+(slot/7)*32;
		g.drawImage(itemImage[id],drawX, drawY,null);
	}
	
}
