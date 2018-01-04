package input;
import main.GameClient;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import draw.GameCanvas;
import entity.Entity;
import networking.packets.*;


public class MouseInput implements MouseListener
{
	public static boolean mouseRightHeld = false;
	public static boolean mouseLeftHeld = false;
	public static int itemClickSlot = -1;
	public static boolean swapping;

	public void mousePressed (MouseEvent e)
	{
		int button = e.getButton();
		int x = e.getX();		
		int y = e.getY();
		//GameClient.chatbox.addNewMessage("Click on screen at "+x+", "+y);

		if (y < GameCanvas.interfaceY) //click on world, not interface
		{
			
			PacketSendClick packet = new PacketSendClick(); //tell server to send global message
			packet.x = (short)getMouseEffectiveX(x);
			packet.y = (short)getMouseEffectiveY(y);
			//GameClient.chatbox.addNewMessage("Click on world at "+packet.x+", "+packet.y);
			if (GameClient.mapMode && e.getButton()== MouseEvent.BUTTON1){
				GameClient.rooms.get(GameClient.roomId).roomTiles[packet.x/16][packet.y/16].solid = !KeyInput.clearMode;
				mouseLeftHeld = true;
			}
			packet.left = (MouseEvent.BUTTON1 == button);
			GameClient.network.client.sendUDP(packet);
			mouseRightHeld = (MouseEvent.BUTTON3 == button);
			if (button != MouseEvent.BUTTON1)
				GameClient.interfaceId=0;
		}
		if (button == MouseEvent.BUTTON1){
			if (y < GameCanvas.interfaceY){
				Entity clickedOn = null;
				double distance = Integer.MAX_VALUE;
				x = getMouseEffectiveX(x);
				y= getMouseEffectiveY(y);
				for (Entity entity: GameClient.roomEntities.values()){ //clicking on entity
					if(entity.collision(x,y)){
						double tempDist = Math.sqrt((x-entity.x)*(x-entity.x)+(y-entity.y)*(y-entity.y));
						if (tempDist < distance){
							clickedOn = entity;
							distance = tempDist;
						}
					}
				}
				if (clickedOn!= null && clickedOn.id != GameClient.playerId){
					GameClient.interfaceId=0;
					PacketInteract packet = new PacketInteract();
					packet.interactId = clickedOn.id;
					packet.interactType = 1;
					GameClient.network.client.sendUDP(packet);
				}
			}
			else if (x > 809 && y > 534){
				byte i = 0;
				if (x > 844)
					i++;
				if (y > 567)
					i+=2;
				if (y > 600)
					i+=2;
				if (y > 633)
					i+=2;
				GameClient.tabInterface = i;
			}
			else
			{
				switch (GameClient.tabInterface){
				case 0:
					if (x>=568 && x <=790 && y >= 535){ //clicking on item in inventory
						short slot = (short)((x-568)/32 + ((y-531)/32)*7);
						if (GameClient.items[slot]==0) //null item
							return;
						itemClickSlot = slot;
					}
					break;
				case 1:
					short slot=0;
					if (y>534 && y<569){//first row
						if (x>568 && x<607) slot = 28;
						else if (x>659 && x<700) slot = 31;
						else if (x>753 && x<793) slot = 30;
					}
					else if (y>576 && y<614){//second row
						if (x>568 && x<607) slot = 31;
						else if (x>617 && x<656) slot = 32;
						else if (x>659 && x<700) slot = 33;
						else if (x>704 && x<743) slot = 34;
						else if (x>753 && x<793) slot = 35;
					}
					else if (y>618 && y<655){//third row
						if (x>568 && x<607) slot = 36;
						else if (x>659 && x<700) slot = 37;
						else if (x>753 && x<793) slot = 38;
					}
					if (slot!=0){
						PacketUseItem packet = new PacketUseItem();
						packet.itemSlot = slot;
						GameClient.network.client.sendUDP(packet);
					}
						
					break;
				}
				
			}
				
		}
	}
	

	public void mouseReleased(MouseEvent e){
		int x = e.getX();		
		int y = e.getY();
		mouseRightHeld = false;
		mouseLeftHeld = false;
		if (GameClient.tabInterface == 0){
			if (itemClickSlot > -1){
				short slot = (short)((x-568)/32 + ((y-531)/32)*7);
				
				if (swapping == false){ //use item
					PacketUseItem packet = new PacketUseItem();
					packet.itemSlot = slot;
					GameClient.network.client.sendUDP(packet);
				}
				else if (slot > -1 && x>=568 && x <=790 && y < 659){ //swapping among inventory
					PacketSwapSlots packet = new PacketSwapSlots();
					packet.slot1 = (short) itemClickSlot;
					packet.slot2 = slot;
					GameClient.network.client.sendUDP(packet);
				}
				swapping = false;
				itemClickSlot = -1;
			}
		}
	}
	public void mouseClicked(MouseEvent e){}
	public void mouseExited(MouseEvent e){mouseRightHeld = false;}
	public void mouseEntered (MouseEvent e){}
	
	public static int getMouseEffectiveX(int x)
	{
		return x+GameClient.roomEntities.get(GameClient.playerId).x- (GameCanvas.WIDTH/2);
	}
	public static int getMouseEffectiveY(int y)
	{
		return y+GameClient.roomEntities.get(GameClient.playerId).y- (GameCanvas.interfaceY/2);
	}
}