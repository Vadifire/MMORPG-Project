package networking.packets;

public class PacketUpdateSlot
{
	public short slot; //0-27 reserved for inv, 28-49 reserved for equipment, 50-249 for bank
	public short item; //item id, 0 for default of nothing
}
