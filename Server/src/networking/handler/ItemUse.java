package networking.handler;

import networking.packets.*;
import main.*;

import com.esotericsoftware.kryonet.Connection;

import entity.Item;
import entity.Player;

import java.lang.Math;

public class ItemUse{
	
	public static void handle(Connection c, PacketUseItem packet){
		Player p = GameServer.players.get(c.getID());
		if (p.items[packet.itemSlot].itemId <= 0)
			return;

		short equipSlot = (short) p.items[packet.itemSlot].getEquipSlot();
		if (packet.itemSlot < 28){ //inventory
			if (equipSlot > 0){ //equip
				for (int i = 0; i < Item.STAT_LENGTH; i++){
					int oldStat = p.stat[i];
					p.stat[i]-= p.items[equipSlot].getStat(i);
					p.stat[i]+= p.items[packet.itemSlot].getStat(i);
							
					if (oldStat != p.stat[i]){//need to send new stat to client
						PacketSendStat statPacket = new PacketSendStat();
						statPacket.statIndex = (byte)i;
						statPacket.stat = (short)p.stat[i];
						statPacket.entityId = p.roomIndex;
						p.c.sendTCP(statPacket);
					}
				}
				PacketSwapSlots swapPacket = new PacketSwapSlots();
				swapPacket.slot1 = packet.itemSlot;
				swapPacket.slot2 = (short) p.items[packet.itemSlot].getEquipSlot();
				SwapSlots.handle(c, swapPacket);
			}
		}
		else if (packet.itemSlot < 50){ //unequip
			short unequipSlot = p.items[packet.itemSlot].getEmptySlot();
			if (unequipSlot == -1){
				p.sendMessage("You have no room in your inventory to unequip this item.");
			}
			else{
				for (int i = 0; i < Item.STAT_LENGTH; i++){
					int oldStat = p.stat[i];
					p.stat[i] -= p.items[equipSlot].getStat(i); //get rid of bonuses
					if (oldStat != p.stat[i]){//need to send new stat to client
						PacketSendStat statPacket = new PacketSendStat();
						statPacket.statIndex = (byte)i;
						statPacket.stat = (short)p.stat[i];
						statPacket.entityId = p.roomIndex;
						p.c.sendTCP(statPacket);
					}
				}
				PacketSwapSlots swapPacket = new PacketSwapSlots();
				swapPacket.slot1 = packet.itemSlot;
				swapPacket.slot2 = unequipSlot;
				SwapSlots.handle(c, swapPacket);
			}
		}
	}
}