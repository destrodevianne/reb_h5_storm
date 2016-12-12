package l2r.gameserver.model.entity.events.objects;

import l2r.gameserver.model.entity.events.GlobalEvent;

import java.io.Serializable;

public interface InitableObject extends Serializable
{
	void initObject(GlobalEvent e);
}
