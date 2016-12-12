/*
 * Copyright Mazaffaka Project (c) 2013.
 */

package npc.model.events.SearchRaidBoss;

import events.SearchRaidBoss.SearchRaidBoss_Zaken;
import l2r.gameserver.model.Creature;
import l2r.gameserver.model.Skill;
import l2r.gameserver.model.instances.MonsterInstance;
import l2r.gameserver.templates.npc.NpcTemplate;

public class SearchRaidBoss_ZakenInstance extends MonsterInstance
{
	/**
     *
     */
	private static final long serialVersionUID = -8306474484707851916L;
	
	public SearchRaidBoss_ZakenInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		i = 10;
		if (attacker.getActiveWeaponInstance() != null)
			switch (attacker.getActiveWeaponInstance().getItemId())
			{
			// Хроно оружие наносит больший урон
				case 4202: // Chrono Cithara
				case 5133: // Chrono Unitus
				case 5817: // Chrono Campana
				case 7058: // Chrono Darbuka
				case 8350: // Chrono Maracas
					i = 100;
					break;
				default:
					i = 10;
			}
		
		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		Creature topdam = getAggroList().getTopDamager();
		if (topdam == null)
			topdam = killer;
		SearchRaidBoss_Zaken.freeSnowman(topdam);
		super.onDeath(killer);
	}
	
	@Override
	public boolean canChampion()
	{
		return false;
	}
}