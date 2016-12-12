package l2r.gameserver.skills.effects;

import l2r.gameserver.model.Effect;
import l2r.gameserver.stats.Env;
import l2r.gameserver.stats.Stats;

public class EffectCombatPointHealOverTime extends Effect
{
	public EffectCombatPointHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isHealBlocked())
			return true;
		
		double addToCp = Math.max(0, Math.min(calc(), _effected.calcStat(Stats.CP_LIMIT, null, null) * _effected.getMaxCp() / 100. - _effected.getCurrentCp()));
		if(addToCp > 0)
			_effected.setCurrentCp(_effected.getCurrentCp() + addToCp);
		
		return true;
	}
}