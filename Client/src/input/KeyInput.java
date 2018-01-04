package input;
import main.GameClient;

import java.awt.event.*;

import draw.GameCanvas;
import networking.packets.*;


public class KeyInput implements KeyListener
{
	public static boolean clearMode = false;
	
	public void keyTyped (KeyEvent e) //Key press followed by key release
	{
		if (GameClient.chatbox.typing&& GameClient.interfaceId != 1)
		{
			char c = e.getKeyChar();
			if ((int)c == 8)
			{
				if (GameClient.chatbox.currentMessage.length() > 0) //backspace
				{
					GameClient.chatbox.currentMessage = 
						GameClient.chatbox.currentMessage.substring(0,GameClient.chatbox.currentMessage.length()-1);
				}
			}
			else if (GameClient.chatbox.currentMessage.length() <= 100 && (int)c >31 )
				GameClient.chatbox.currentMessage+=c;
		}

		
	}
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		switch (keyCode){

		case KeyEvent.VK_ESCAPE:
			if (GameClient.chatbox.typing){
				GameClient.chatbox.typing = false;
				GameClient.chatbox.currentMessage="";
			}
			break;
		
		case KeyEvent.VK_ENTER:
			if (GameClient.interfaceId != 0)
				break;
			if (GameClient.chatbox.typing) //submit message
			{
				if (GameClient.chatbox.currentMessage.equalsIgnoreCase("/fps"))
				{
					GameCanvas.drawFps = !GameCanvas.drawFps;
				}
				else if (GameClient.chatbox.currentMessage.equalsIgnoreCase("/mapmode"))
				{
					GameClient.mapMode = !GameClient.mapMode;
					GameClient.chatbox.addNewMessage("Map mode set to "+GameClient.mapMode);
				}
				else if (GameClient.chatbox.currentMessage.equalsIgnoreCase("/clientcoords"))
				{
					GameClient.chatbox.addNewMessage("Client Coords: ("+GameClient.roomEntities.get(GameClient.playerId).x+", "+GameClient.roomEntities.get(GameClient.playerId).y+")");
				}
				else if (!GameClient.chatbox.currentMessage.equals(""))
				{
					PacketSendMessage packet = new PacketSendMessage(); //tell server to send global message
					packet.message = GameClient.chatbox.currentMessage;
					GameClient.network.client.sendTCP(packet);
				}
				GameClient.chatbox.currentMessage="";

			}
			GameClient.chatbox.typing = !GameClient.chatbox.typing;
			break;
			
		case KeyEvent.VK_SHIFT:
			clearMode = true;
			break;
		case KeyEvent.VK_F1:case KeyEvent.VK_F2:case KeyEvent.VK_F3:case KeyEvent.VK_F4:case KeyEvent.VK_F5:case KeyEvent.VK_F6:
			GameClient.tabInterface=(byte) (keyCode-112);
			break;
		case KeyEvent.VK_1:case KeyEvent.VK_2:case KeyEvent.VK_3:case KeyEvent.VK_4:case KeyEvent.VK_5:case KeyEvent.VK_6:case KeyEvent.VK_7:
			if (!GameClient.chatbox.typing){
				PacketUseItem packet = new PacketUseItem(); //use item hotkey
				packet.itemSlot = (short)(keyCode-49);
				GameClient.network.client.sendUDP(packet);
			}
			break;
		case KeyEvent.VK_F9:
			if (GameClient.mapMode){
				GameClient.rooms.get(GameClient.roomId).save();
				break;
			}
		}
	}
	public void keyReleased(KeyEvent e)
	{
		
		int keyCode = e.getKeyCode();

		switch (keyCode){
		case KeyEvent.VK_SPACE:
			if (GameClient.interfaceId == 1){ //next line of chat
				//GameClient.interfaceId = 0; //temporary
				PacketSendKey packet = new PacketSendKey();
				packet.key = KeyEvent.VK_SPACE;
				GameClient.network.client.sendTCP(packet);
			}
			break;
		case KeyEvent.VK_SHIFT:
			clearMode = false;
			break;
		}
	}
}