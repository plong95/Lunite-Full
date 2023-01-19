package com.ruse.world.content;

import com.ruse.model.Position;
//import com.ruse.tools.discord.DiscordConstant;
//import com.ruse.tools.discord.DiscordManager;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 *
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 * @author Crimson reworked 6/2/2017, thanks RuneUnity
 *
 */
public class Wildywyrm extends NPC {

	private static long massMessageTimer = 0;

	/**
	 *
	 */
	public static boolean wyrmAlive = false;
	public static int rng = 0,
			// testINTERVAL = 1*1000,
			INTERVAL = 30 * 60 * 2000, NPC_ID = 3334;
	/**
	 *
	 */
	public static final WildywyrmLocation[] LOCATIONS = {
			new WildywyrmLocation(3289, 3895, 0, "Demonic Ruins ::pvp", "::PVP -> Multi"),
			new WildywyrmLocation(3289, 3895, 0, "Demonic Ruins ::pvp", "::PVP -> Multi") };

	/**
	 *
	 */
	private static Wildywyrm current;
	public static String playerPanelFrame;

	/**
	 *
	 * @param position
	 */
	public Wildywyrm(Position position) {

		super(NPC_ID, position);

		// setConstitution(96500/3); //7,650
		setDefaultConstitution(107500);

	}

	/**
	 *
	 */
	public static void initialize() {

		if (massMessageTimer == 0 || (System.currentTimeMillis() - massMessageTimer >= INTERVAL)) {
			// // System.out.println("massMessageTimer = "+massMessageTimer);
			// // System.out.println("currentTimeMs = "+System.currentTimeMillis());
			// // System.out.println("spawn wyrm called");
			massMessageTimer = System.currentTimeMillis();
			spawn();

		}

	}

	public static void sendHint(Player player) {
		// player.getPacketSender().sendMessage("<col=1e56ad><img=5> [WildyWyrm]@bla@
		// The WildyWyrm is roaming "+LOCATIONS[rng].getLocation()+"!");
	}

	public static String getPlayerPanelHint() {
		return LOCATIONS[rng].getPlayerPanelHint();
	}

	/**
	 *
	 */
	public static void spawn() {
		if (wyrmAlive == true) { // checks if its already alive to avoid duplicates
			// // System.out.println("spawn failed - wyrm is already alive");

			return;
		}
		/*
		 * if(getCurrent() != null) { // System.out.print("spawn failed"); return; }
		 */

		rng = Misc.randomMinusOne(LOCATIONS.length);
		WildywyrmLocation location = LOCATIONS[rng];
		Wildywyrm instance = new Wildywyrm(location.copy());

		// // System.out.println(instance.getPosition());

		World.register(instance);
		setCurrent(instance);
		// // System.out.print("spawned.");
		wyrmAlive = true;
		World.sendMessage(
				"<col=1e56ad><img=5> [WildyWyrm]@bla@ The WildyWyrm has appeared " + location.getLocation() + "!");
		// new DiscordManager(DiscordConstant.EVENTS_CHANNEL, "Wildywyrm", "The
		// WildyWyrm has appeared "+location.getLocation()+"!").log1();
		World.getPlayers().forEach(p -> PlayerPanel.refreshPanel(p));

	}

	/**
	 *
	 * @param npc
	 */
	/*
	 * public static void handleDrop(NPC npc) {
	 * 
	 * //World.getPlayers().forEach(p -> p.getPacketSender().sendString(26707,
	 * "@or2@WildyWyrm: @gre@N/A"));
	 * 
	 * setCurrent(null);
	 * 
	 * if(npc.getCombatBuilder().getDamageMap().size() == 0) { return; }
	 * 
	 * Map<Player, Integer> killers = new HashMap<>();
	 * 
	 * for(Entry<Player, CombatDamageCache> entry :
	 * npc.getCombatBuilder().getDamageMap().entrySet()) {
	 * 
	 * if(entry == null) { continue; }
	 * 
	 * long timeout = entry.getValue().getStopwatch().elapsed();
	 * 
	 * if(timeout > CombatFactory.DAMAGE_CACHE_TIMEOUT) { continue; }
	 * 
	 * Player player = entry.getKey();
	 * 
	 * if(player.getConstitution() <= 0 || !player.isRegistered()) { continue; }
	 * 
	 * killers.put(player, entry.getValue().getDamage());
	 * 
	 * }
	 * 
	 * npc.getCombatBuilder().getDamageMap().clear();
	 * 
	 * List<Entry<Player, Integer>>result = sortEntries(killers); int count = 0;
	 * 
	 * for(Entry<Player, Integer> entry : result) {
	 * 
	 * Player killer = entry.getKey(); int damage = entry.getValue();
	 * 
	 * handleDrop(npc, killer, damage);
	 * 
	 * if(++count >= 5) { break; }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * public static void giveLoot(Player player, NPC npc, Position pos) {
	 * 
	 * int chance = Misc.getRandom(10); int superrare =
	 * SUPERRARELOOT[Misc.getRandom(SUPERRARELOOT.length - 1)];
	 * World.sendMessage("<col=FF0000>"+player.getUsername()+
	 * " received a loot from the Wildywyrm!");
	 * 
	 * 
	 * //player.getPacketSender().sendMessage("chance: "+chance);
	 * GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(995,
	 * 7500000 + Misc.getRandom(15000000)), pos, player.getUsername(), false, 150,
	 * true, 200));
	 * 
	 * if(chance > 9){ //super rare GroundItemManager.spawnGroundItem(player, new
	 * GroundItem(new Item(RARELOOT[Misc.getRandom(RARELOOT.length - 1)], 300 +
	 * Misc.getRandom(200)), pos, player.getUsername(), false, 150, true, 200));
	 * GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)], 200 +
	 * Misc.getRandom(200)), pos, player.getUsername(), false, 150, true, 200));
	 * GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(superrare),
	 * pos, player.getUsername(), false, 150, true, 200)); return; } if(chance > 7)
	 * { //rare GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(RARELOOT[Misc.getRandom(RARELOOT.length - 1)], 300 +
	 * Misc.getRandom(1600)), pos, player.getUsername(), false, 150, true, 200));
	 * GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(RARELOOT[Misc.getRandom(RARELOOT.length - 1)], 250 +
	 * Misc.getRandom(100)), pos, player.getUsername(), false, 150, true, 200));
	 * GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(RARELOOT[Misc.getRandom(RARELOOT.length - 1)], 200 +
	 * Misc.getRandom(200)), pos, player.getUsername(), false, 150, true, 200));
	 * return; } if(chance > 4) { //medium GroundItemManager.spawnGroundItem(player,
	 * new GroundItem(new Item(MEDIUMLOOT[Misc.getRandom(MEDIUMLOOT.length - 1)],
	 * 300 + Misc.getRandom(130)), pos, player.getUsername(), false, 150, true,
	 * 200)); GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(MEDIUMLOOT[Misc.getRandom(MEDIUMLOOT.length - 1)], 220 +
	 * Misc.getRandom(130)), pos, player.getUsername(), false, 150, true, 200));
	 * GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(MEDIUMLOOT[Misc.getRandom(MEDIUMLOOT.length - 1)], 240 +
	 * Misc.getRandom(130)), pos, player.getUsername(), false, 150, true, 200));
	 * return; } if(chance >= 0){ //common GroundItemManager.spawnGroundItem(player,
	 * new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)],
	 * 200 + Misc.getRandom(220)), pos, player.getUsername(), false, 150, true,
	 * 200)); GroundItemManager.spawnGroundItem(player, new GroundItem(new
	 * Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)], 200 +
	 * Misc.getRandom(230)), pos, player.getUsername(), false, 150, true, 200));
	 * return; }
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * /**
	 *
	 * @param npc
	 * 
	 * @param player
	 * 
	 * @param damage
	 */
	/*
	 * private static void handleDrop(NPC npc, Player player, int damage) { Position
	 * pos = npc.getPosition(); giveLoot(player, npc, pos); }
	 * 
	 * /**
	 *
	 * @param map
	 * 
	 * @return
	 */
	/*
	 * static <K, V extends Comparable<? super V>> List<Entry<K, V>>
	 * sortEntries(Map<K, V> map) {
	 * 
	 * List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());
	 * 
	 * Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
	 * 
	 * @Override public int compare(Entry<K, V> e1, Entry<K, V> e2) { return
	 * e2.getValue().compareTo(e1.getValue()); }
	 * 
	 * });
	 * 
	 * return sortedEntries;
	 * 
	 * }
	 */

	/**
	 *
	 * @return
	 */
	public static Wildywyrm getCurrent() {
		return current;
	}

	/**
	 *
	 * @param current
	 */
	public static void setCurrent(Wildywyrm current) {
		Wildywyrm.current = current;
	}

	/**
	 *
	 * @author Nick Hartskeerl <apachenick@hotmail.com>
	 *
	 */
	public static class WildywyrmLocation extends Position {

		/**
		 *
		 */
		private String location, hint;

		/**
		 *
		 * @param x
		 * @param y
		 * @param z
		 * @param location
		 */
		public WildywyrmLocation(int x, int y, int z, String location, String hint) {
			super(x, y, z);
			setLocation(location);
			setHint(hint);
		}

		/**
		 *
		 * @return
		 */

		String getLocation() {
			return location;
		}

		String getPlayerPanelHint() {
			return hint;
		}

		/**
		 *
		 * @param location
		 */
		public void setLocation(String location) {
			this.location = location;
		}

		public void setHint(String hint) {
			this.hint = hint;
		}

	}

}