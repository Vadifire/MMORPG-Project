package networking.packets;

public class PacketSendStat
{
	public short stat;
	public byte statIndex;
	public int entityId;
	/*
	 * 0 = melee prowess
	 * 1 = range prowess
	 * 2 = mage prowess
	 * 3 = melee pen
	 * 4 = range pen
	 * 5 = mage pen
	 * 6 = melee resist
	 * 7 = range resist
	 * 8 = melee resist
	 * 9 = attack speed
	 * 10 = crit chance
	 * 11 = CDR
	 * 12 = speed
	 * 13 = health
	 * ...
	 * >=20 reserved for stats
	 * 
	 */
}
