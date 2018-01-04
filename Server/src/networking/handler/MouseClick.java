package networking.handler;

import networking.packets.*;
import main.*;
import com.esotericsoftware.kryonet.Connection;
import entity.Player;
import java.lang.Math;

public class MouseClick
{
	public static void handle(Connection c, PacketSendClick packet)
	{
		Player p = GameServer.players.get(c.getID());
		if (packet.left) //Issue interact command ???? might not be used
		{
			
		}			
		else //Issue movement command
		{
			p.interactEntityId = -1; //cancel any queue
			p.agroId = -1; //cancel combat
			p.moveTo(packet.x, packet.y);
		}
	}
}