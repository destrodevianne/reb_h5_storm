package events.SearchRaidBoss;

import l2r.commons.threading.RunnableImpl;
import l2r.gameserver.Announcements;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.Msg;
import l2r.gameserver.data.xml.holder.NpcHolder;
import l2r.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2r.gameserver.model.Creature;
import l2r.gameserver.model.GameObjectsStorage;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.SimpleSpawner;
import l2r.gameserver.model.actor.listener.CharListenerList;
import l2r.gameserver.model.instances.NpcInstance;
import l2r.gameserver.network.serverpackets.RadarControl;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.network.serverpackets.components.ChatType;
import l2r.gameserver.scripts.Functions;
import l2r.gameserver.scripts.ScriptFile;
import l2r.gameserver.templates.npc.NpcTemplate;
import l2r.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

public class SearchRaidBoss_QueenAnt extends Functions implements ScriptFile, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(SearchRaidBoss_Antharas.class);
	
	private static ScheduledFuture<?> _snowmanShoutTask;
	private static ScheduledFuture<?> _saveTask;
	private static ScheduledFuture<?> _sayTask;
	private static ScheduledFuture<?> _eatTask;
	
	public static SnowmanState _snowmanState;
	
	private static NpcInstance _snowman;
	private static Creature _thomas;
	
	public static enum SnowmanState
	{
		CAPTURED,
		KILLED,
		SAVED
	}
	
	private static final int INITIAL_SAVE_DELAY = 330 * 60 * 1000; // 10 мин
	private static final int SAVE_INTERVAL = 60 * 60 * 1000; // 60 мин
	private static final int SNOWMAN_SHOUT_INTERVAL = 1 * 60 * 1000; // 1 мин
	private static final int THOMAS_EAT_DELAY = 30 * 60 * 1000; // 30 мин
	private static final int МилаяКоровка = 19387;
	private static final int QueenAnt = 70000;
	
	private static boolean _active = false;
	
	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (isActive())
		{
			_active = true;
			_log.info("Loaded Event: SearchRaidBoss_QueenAnt [state: activated]");
			_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
			_snowmanState = SnowmanState.SAVED;
		}
		else
			_log.info("Loaded Event: SearchRaidBoss_QueenAnt [state: deactivated]");
	}
	
	/**
	 * Читает статус эвента из базы.
	 */
	private static boolean isActive()
	{
		return IsActive("SearchRaidBoss_QueenAnt");
	}
	
	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if (!player.getAccessLevel().isGm())
			return;
		
		/* FIXME */
		if (Boolean.FALSE)
		{
			player.sendMessage("Event is currently disabled");
			return;
		}
		
		if (SetActive("SearchRaidBoss_QueenAnt", true))
		{
			System.out.println("Event 'SearchRaidBoss_QueenAnt' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SearchRaidBoss_QueenAnt.AnnounceEventStarted", null);
			if (_saveTask == null)
				_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
			_snowmanState = SnowmanState.SAVED;
		}
		else
			player.sendMessage("Event 'SearchRaidBoss_QueenAnt' already started.");
		
		_active = true;
		
		show("admin/events/events.htm", player);
	}
	
	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if (!player.getAccessLevel().isGm())
			return;
		if (SetActive("SearchRaidBoss_QueenAnt", false))
		{
			if (_snowman != null)
				_snowman.deleteMe();
			if (_thomas != null)
				_thomas.deleteMe();
			System.out.println("Event 'SearchRaidBoss_QueenAnt' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SearchRaidBoss_QueenAnt.AnnounceEventStoped", null);
			if (_saveTask != null)
			{
				_saveTask.cancel(false);
				_saveTask = null;
			}
			if (_sayTask != null)
			{
				_sayTask.cancel(false);
				_sayTask = null;
			}
			if (_eatTask != null)
			{
				_eatTask.cancel(false);
				_eatTask = null;
			}
			_snowmanState = SnowmanState.SAVED;
		}
		else
			player.sendMessage("Event 'SearchRaidBoss_QueenAnt' not started.");
		
		_active = false;
		
		show("admin/events/events.htm", player);
	}
	
	@Override
	public void onReload()
	{
		if (_saveTask != null)
			_saveTask.cancel(false);
		_saveTask = null;
		if (_sayTask != null)
			_sayTask.cancel(false);
		_sayTask = null;
		_snowmanState = SnowmanState.SAVED;
	}
	
	@Override
	public void onShutdown()
	{
		
	}
	
	public void locateSnowman()
	{
		Player player = getSelf();
		if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;
		
		if (_snowman != null)
		{
			// Убираем и ставим флажок на карте и стрелку на компасе
			player.sendPacket(new RadarControl(2, 2, _snowman.getLoc()), new RadarControl(0, 1, _snowman.getLoc()));
			player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(_snowman.getLoc()).addString("Ищите Antaras в "));
		}
		else
			player.sendPacket(Msg.YOUR_TARGET_CANNOT_BE_FOUND);
	}
	
	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SearchRaidBoss_QueenAnt.AnnounceEventStarted", null);
	}
	
	private static Location getRandomSpawnPoint()
	{
		return new Location(110904, 187416, -3660);
	}
	
	// Индюк захватывает снеговика
	public void captureSnowman()
	{
		Location spawnPoint = getRandomSpawnPoint();
		
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SearchRaidBoss_QueenAnt.AnnounceSnowmanCaptured", null, ChatType.CRITICAL_ANNOUNCE);
			player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(spawnPoint).addString("Ищите Снеговика в "));
			// Убираем и ставим флажок на карте и стрелку на компасе
			player.sendPacket(new RadarControl(2, 2, spawnPoint), new RadarControl(0, 1, spawnPoint));
		}
		
		// Спауним снеговика
		NpcTemplate template = NpcHolder.getInstance().getTemplate(МилаяКоровка);
		if (template == null)
		{
			System.out.println("WARNING! events.SearchRaidBoss_QueenAnt.captureSnowman template is null for npc: " + МилаяКоровка);
			Thread.dumpStack();
			return;
		}
		
		SimpleSpawner sp = new SimpleSpawner(template);
		sp.setLoc(spawnPoint);
		sp.setAmount(1);
		sp.setRespawnDelay(0);
		_snowman = sp.doSpawn(true);
		
		if (_snowman == null)
			return;
		
		// Спауним Томаса
		template = NpcHolder.getInstance().getTemplate(QueenAnt);
		if (template == null)
		{
			System.out.println("WARNING! events.SearchRaidBoss_QueenAnt.captureSnowman template is null for npc: " + QueenAnt);
			Thread.dumpStack();
			return;
		}
		
		Location pos = Location.findPointToStay(_snowman, 100, 120);
		
		sp = new SimpleSpawner(template);
		sp.setLoc(pos);
		sp.setAmount(1);
		sp.setRespawnDelay(0);
		_thomas = sp.doSpawn(true);
		
		if (_thomas == null)
			return;
		
		_snowmanState = SnowmanState.CAPTURED;
		
		// Если по каким-то причинам таск существует, останавливаем его
		if (_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(false);
			_snowmanShoutTask = null;
		}
		_snowmanShoutTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShoutTask(), 1, SNOWMAN_SHOUT_INTERVAL);
		
		if (_eatTask != null)
		{
			_eatTask.cancel(false);
			_eatTask = null;
		}
		_eatTask = executeTask("events.SearchRaidBoss_QueenAnt.SearchRaidBoss_QueenAnt", "eatSnowman", new Object[0], THOMAS_EAT_DELAY);
	}
	
	// Индюк захавывает снеговика
	public static void eatSnowman()
	{
		if (_snowman == null || _thomas == null)
			return;
		
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SearchRaidBoss_QueenAnt.AnnounceSnowmanKilled", null, ChatType.CRITICAL_ANNOUNCE);
		
		_snowmanState = SnowmanState.KILLED;
		
		if (_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(false);
			_snowmanShoutTask = null;
		}
		
		_snowman.deleteMe();
		_thomas.deleteMe();
	}
	
	// Индюк умер, освобождаем снеговика
	public static void freeSnowman(Creature topDamager)
	{
		if (_snowman == null || topDamager == null || !topDamager.isPlayable())
			return;
		
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SearchRaidBoss_QueenAnt.AnnounceSnowmanSaved", null, ChatType.CRITICAL_ANNOUNCE);
		
		_snowmanState = SnowmanState.SAVED;
		
		if (_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(false);
			_snowmanShoutTask = null;
		}
		if (_eatTask != null)
		{
			_eatTask.cancel(false);
			_eatTask = null;
		}
		
		Player player = topDamager.getPlayer();
		Functions.npcSayCustomMessage(_snowman, "scripts.events.SearchRaidBoss_QueenAnt.SnowmanSayTnx", player.getName());
		addItem(player, 20034, 3); // Revita-Pop
		addItem(player, 20338, 1); // Rune of Experience Points 50% 10 Hour Expiration Period
		addItem(player, 20344, 1); // Rune of SP 50% 10 Hour Expiration Period
		
		ThreadPoolManager.getInstance().execute(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				_snowman.deleteMe();
			}
			
		});
	}
	
	public class ShoutTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (!_active || _snowman == null || _snowmanState != SnowmanState.CAPTURED)
				return;
			
			Functions.npcShoutCustomMessage(_snowman, "scripts.events.SearchRaidBoss_QueenAnt.SnowmanShout");
		}
	}
	
	public class SaveTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (!_active || _snowmanState == SnowmanState.CAPTURED)
				return;
			
			captureSnowman();
		}
	}
}
