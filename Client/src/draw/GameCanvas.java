package draw;
import java.awt.*;

import input.KeyInput;
import input.MouseInput;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import main.GameClient;
import map.Room;
import networking.packets.*;
import entity.*;

public class GameCanvas extends Canvas implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3148900585522462853L;
	public static final int WIDTH = 890;
	public static final int HEIGHT = 670;
	private boolean running = false;
	private Thread gameThread;
	public  static final int interfaceY = 524;
	public BufferedImage inventoryImage, iconsImage, equipmentImage,statsImage;
	public String fps = "0";
	public static boolean drawFps = false;
	public static boolean actionString;

	
	public synchronized void start()
	{
		try{
			inventoryImage = ImageIO.read(new File("data/interface/inv.png"));
			equipmentImage = ImageIO.read(new File("data/interface/equipment.png"));
			iconsImage = ImageIO.read(new File("data/interface/icons.png"));
			statsImage = ImageIO.read(new File("data/interface/stats.png"));
		}catch(Exception e){}

		if (running)
			return;
		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}
	public void run()
	{
		long lastTime = System.nanoTime();
		double amountOfInputTicks = 5;
		double ns = 1000000000 / amountOfInputTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				inputTick();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if (GameClient.mapMode && MouseInput.mouseLeftHeld){
				Point point = getMousePosition();
				try{
					GameClient.rooms.get(GameClient.roomId).roomTiles[(MouseInput.getMouseEffectiveX((int)point.getX()))/16][(int) MouseInput.getMouseEffectiveY((int)point.getY())/16].solid = !KeyInput.clearMode;
				}catch (Exception e){}
			}
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				fps = "FPS: "+frames;
				frames = 0;
				updates = 0;
			}
		}
	}
	
	private void inputTick() 
	{
		if (MouseInput.mouseRightHeld)//process mouse hold every 200 ms
		{
			Point point = getMousePosition();
			PacketSendClick packet = new PacketSendClick();
			packet.x = (short)MouseInput.getMouseEffectiveX((int)point.getX());
			packet.y = (short)MouseInput.getMouseEffectiveY((int)point.getY());
			packet.left = false;
			GameClient.network.client.sendUDP(packet);
		}
	}	
	
	private void render()
	{
		try{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			this.createBufferStrategy(3);
			return;
		}
		Room room = GameClient.rooms.get(GameClient.roomId);
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.black); 
		g.fillRect(0,0,WIDTH, HEIGHT);
		room.draw(g);
		
		if (GameClient.mapMode){
			g.setColor(Color.red);
			for (int i = 0; i < room.width/room.TILE_SIZE; i++){
				for (int j = 0; j < room.height/room.TILE_SIZE; j++){
					if (room.roomTiles[i][j].solid){
						g.drawRect(getScreenX(i*16), getScreenY(j*16), 16, 16);
					}
				}
			}
		}
		
		for (Entity e : GameClient.roomEntities.values())
			e.draw(g);
		for (Entity e : GameClient.roomEntities.values())
			e.drawHealth(g);
		for (Entity e : GameClient.roomEntities.values()) //draw overhead after
		{
			if (e.lastSaidTime+3500>System.currentTimeMillis()){
				g.setFont(new Font("Century Gothic", Font.BOLD, 14));
				g.setColor(Color.yellow);
				int center = (int)(g.getFontMetrics().getStringBounds(e.lastSaid, g).getWidth()/2);
				g.drawString(""+e.lastSaid, GameCanvas.getScreenX(e.x-center),
						GameCanvas.getScreenY(e.y-e.height));
			}
		}
		
		//interface
		GameClient.chatbox.drawInterface(g);
		g.setColor(Color.white);
		
		switch (GameClient.tabInterface){
		case 0:
			g.drawImage(inventoryImage,556,interfaceY,null);
			for (int i = 0; i < 28; i++)
				Item.drawInv(g, i);
			break;
		case 1:
			g.drawImage(equipmentImage,556,interfaceY,null);
			for (int i = 28; i < 39; i++)
				Item.drawEquip(g, i);
			break;
		case 3:
			Entity p = GameClient.roomEntities.get(GameClient.playerId);
			g.setColor(Color.white);
			g.drawImage(statsImage,556,interfaceY,null);
			g.setFont(new Font("Arial", Font.BOLD, 13));
			g.drawString("*Prowess*",572,544);
			g.drawString("Melee: "+p.stat[0],572,562);
			g.drawString("Range: "+p.stat[1],572,580);
			g.drawString("Mage: "+p.stat[2],572,598);
			g.drawString("Crit %: "+p.stat[10],572,616);
			
			g.drawString("*Resistance*",692,544);
			g.drawString("Melee: "+p.stat[6],692,562);
			g.drawString("Range: "+p.stat[7],692,580);
			g.drawString("Mage: "+p.stat[8],692,598);
			
			g.drawString("Speed: "+p.stat[12],572, 639);
			g.drawString("CDR: "+p.stat[11],692, 639);
			g.drawString("AS: "+p.stat[9 ],572, 657);
			//g.drawString("CDR: 999",692, 657);
			break;
		}

		g.drawImage(iconsImage,802,interfaceY,null);

		try{ //should be done in a move mouse listener later, on a 30-60hz refresh
		Point point = getMousePosition();
		int x = (int)point.getX();
		int y = (int)point.getY();
			if (y < GameCanvas.interfaceY){
				Entity clickedOn = null;
				double distance = Integer.MAX_VALUE;
				x = MouseInput.getMouseEffectiveX(x);
				y= MouseInput.getMouseEffectiveY(y);
				for (Entity entity: GameClient.roomEntities.values()){
					if(entity.collision(x,y)){
						double tempDist = Math.sqrt((x-entity.x)*(x-entity.x)+(y-entity.y)*(y-entity.y));
						if (tempDist < distance){
							clickedOn = entity;
							distance = tempDist;
						}
					}
				}
				if (clickedOn!= null && clickedOn.id != GameClient.playerId){
					g.setColor(Color.white);
					g.setFont(new Font("Arial", Font.BOLD, 13));
					String as = clickedOn.getActionString();
					g.drawString(as.substring(0,1).toUpperCase()+as.substring(1)+" "+clickedOn.getName(), 2, 11);
					g.setColor(Color.yellow);
					g.drawString(clickedOn.getActionString(), (int)point.getX(), (int)point.getY());
				}
			}
			else if (GameClient.tabInterface==0){//inventory
				Item.drawClickString(g, x, y);
			}
		}catch(Exception e){}
		
		if(GameCanvas.drawFps)
		{
			g.setFont(new Font("Britanic", Font.BOLD, 14));
			g.setColor(Color.yellow);
			g.drawString(fps, 820, 12);
		}
		
		//End of Draw
		g.dispose();
		bs.show();}
		catch(Exception e){e.printStackTrace(); try {gameThread.sleep(500);} catch (InterruptedException e1) {}}//failed to draw, try again in .5s}
	}
	
	public static int getScreenX(int x)
	{
		return x-GameClient.roomEntities.get(GameClient.playerId).x+WIDTH/2;
	}
	public static int getScreenY(int y)
	{
		return y-GameClient.roomEntities.get(GameClient.playerId).y+(interfaceY)/2;
	}
	
	public static void createGameWindow()
	{
		GameClient.gc = new GameCanvas();
		new Window(WIDTH,HEIGHT,"Game",GameClient.gc);
	}
}