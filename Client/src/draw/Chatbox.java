package draw;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Entity;
import main.GameClient;

public class Chatbox
{
	public boolean typing;
	public String currentMessage;
	public String[] messageArray = new String[7]; //index 0 is message at top
	int messageCount;
	public BufferedImage interfaceImage[] = new BufferedImage[10];

	
	public Chatbox()
	{
		typing = false;
		currentMessage = "";
		messageCount = -1;
		for (int i = 0; i < messageArray.length; i++)
			messageArray[i] = "";
    	try {
    		for (int i =0; i<Byte.MAX_VALUE;i++)
    		interfaceImage[i] = ImageIO.read(new File("data/interface/interface"+i+".png"));
		} catch (IOException e) {} //load until no file
	}
	
	public void addNewMessage(String s)
	{
		if (messageCount == messageArray.length-1) // chat is full, must move up
			for (int i = 1; i < messageArray.length; i++)
				messageArray[i-1] = messageArray[i];
			
		if (messageCount < messageArray.length-1) //chat not full yet, 
			messageCount++;
		messageArray[messageCount] = s;
	}
	
	public void drawInterface(Graphics g)
	{
		switch (GameClient.interfaceId){
		case 1:
			drawNPCChat(g);
			break;
		default: //case 0 and cases that don't effect chat.
			drawChat(g);
			break;
		}
	}
	
	public void drawChat(Graphics g)
	{
			g.drawImage(interfaceImage[0], 0, GameCanvas.interfaceY, null);

			g.setColor(Color.white);
			g.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			int yDisp = 15; //displacement between chat
			
			for (int i = 0; i < GameClient.chatbox.messageArray.length; i++)
			{
				g.drawString(GameClient.chatbox.messageArray[i],14,GameCanvas.interfaceY+24+yDisp*i);
			}
			if (GameClient.chatbox.typing == false)
				g.drawString("Press Enter to type...",16,GameCanvas.interfaceY+134);
			else
				g.drawString(GameClient.username+": "+GameClient.chatbox.currentMessage,16,GameCanvas.interfaceY+134);
	}
	
	
	public static String NPCLines[] = {"","","","",""};
	public static int drawX[] = new int[5];
	
	public static void orderNPCString(){
		Graphics g = GameClient.gc.getBufferStrategy().getDrawGraphics();
		
		g.setFont(new Font("Times New Roman", Font.PLAIN, 16)); //needed for formatting
		
		String s = GameClient.interfaceString;
		String printS = new String(s);
		int line = 0;
	
		while (s.length() > 0 && line <4)
		{
			int lastSpace = s.length()-1;
			printS = new String(s);
			int stringLength = (int) (g.getFontMetrics().getStringBounds(printS, g).getWidth());
			while (stringLength > 510){
				lastSpace = printS.lastIndexOf(' ');
				printS = printS.substring(0, lastSpace);
				stringLength = (int) (g.getFontMetrics().getStringBounds(printS, g).getWidth());
			}
			line++;
			s = s.substring(lastSpace+1, s.length());
			
			int center = (int)(g.getFontMetrics().getStringBounds(printS, g).getWidth()/2);
			drawX[line] = 270-center;
			NPCLines[line] = printS;
			
			if (line ==1){
				int nameEnd = printS.indexOf(':');
				if (nameEnd > 0){
					printS = printS.substring(0, nameEnd);
					drawX[0] = 270-center;
					NPCLines[0] = printS;
				}
				else
					NPCLines[0]="";
			}
		}
		for (int i = line+1; i <NPCLines.length; i++)
			NPCLines[i]="";
	}
	
	public void drawNPCChat(Graphics g){
		g.drawImage(interfaceImage[1], 0, GameCanvas.interfaceY, null);
		if (GameClient.interfaceInt>0){
			g.drawImage(Entity.entityImage[GameClient.interfaceInt],20,640,null);
		}
		

		g.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		g.setColor(Color.BLUE);
		for (int i = 1; i < NPCLines.length; i++){
			g.drawString(NPCLines[i],drawX[i],GameCanvas.interfaceY+8+25*i);
		}
		g.setColor(new Color(0xBB,0x33,0));
		g.drawString(NPCLines[0],drawX[0], GameCanvas.interfaceY+33);
		g.setColor(Color.red);
		g.drawString("Press space to continue...",193,GameCanvas.interfaceY+133);
	}

}