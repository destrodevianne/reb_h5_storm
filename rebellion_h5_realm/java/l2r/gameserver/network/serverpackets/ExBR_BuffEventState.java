package l2r.gameserver.network.serverpackets;

public class ExBR_BuffEventState extends L2GameServerPacket
{
	private int _type; // 1 - %, 2 - npcId
	private int _value; // depending on type: for type 1 - % value; for type 2 -
	// 20573-20575
	private int _state; // 0-1
	private int _endtime; // only when type 2 as unix time in seconds from 1970
	
	public ExBR_BuffEventState(int type, int value, int state, int endtime)
	{
		_type = type;
		_value = value;
		_state = state;
		_endtime = endtime;
	}
	
	@Override
	protected void writeImpl()
	{//TODO TEST
		writeEx(0xDB);
		writeD(_type);
		writeD(_value);
		writeD(_state);
		writeD(_endtime);
	}
}