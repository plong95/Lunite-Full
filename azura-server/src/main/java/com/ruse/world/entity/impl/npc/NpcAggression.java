package com.ruse.world.entity.impl.npc;

import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.strategy.impl.Nex;
import com.ruse.world.content.globalBoss.GlobalBoss;
import com.ruse.world.content.skill.impl.dungeoneering.Dungeoneering;
import com.ruse.world.entity.impl.player.Player;

/**
 * Handles the behavior of aggressive {@link Npc}s around players within the
 * <code>NPC_TARGET_DISTANCE</code> radius.
 * 
 * @author lare96
 */
public final class NpcAggression {

	/**
	 * Time that has to be spent in a region before npcs stop acting aggressive
	 * toward a specific player.
	 */
	public static final int NPC_TOLERANCE_SECONDS = 300; // 5 mins

	public static void target(Player player) {

		if (player.isPlayerLocked() || player.isGroupIronmanLocked())
			return;

		boolean dung = Dungeoneering.doingDungeoneering(player)
				|| player.getLocation() == Location.DEATH_SANCTUM;

		// Loop through all of the aggressive npcs.
		for (NPC npc : player.getLocalNpcs()) {
			dung = Dungeoneering.doingDungeoneering(player)
					|| player.getLocation() == Location.DEATH_SANCTUM
					|| player.getLocation() == Location.ANIMA_CHAMBERS;

			if (npc == null || npc.getConstitution() <= 0)
				continue;
			if (npc.getId() >= 10019 && npc.getId() <= 10021 && player.getLocation() == Location.WAVE_MINIGAME)
				dung = true;

			NPCFacing.updateFacing(player, npc);

			if (!(dung && npc.getId() != 11226) && !npc.getDefinition().isAggressive()) {
				continue;
			}

			if (npc.getSpawnedFor() != null && npc.getSpawnedFor() != player)
				continue;

			if (!npc.findNewTarget()) {
				if (npc.getCombatBuilder().isAttacking() || npc.getCombatBuilder().isBeingAttacked()) {
					continue;
				}
			}

			/** GWD **/
		
			// Check if the entity is within distance.
			if (Locations.goodDistance(npc.getPosition(), player.getPosition(), npc.getAggressionDistance())
					|| npc instanceof GlobalBoss) {

				if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000)
						
						&& player.getLocation() != Location.DAGANNOTH_DUNGEON && !dung) {
					break;
				}

				boolean multi = Location.inMulti(player);

				if (player.isTargeted()) {
					if (!player.getCombatBuilder().isBeingAttacked()) {
						player.setTargeted(false);
					} else if (!multi) {
						break;
					}
				}

				if (player.getSkillManager().getCombatLevel() > (npc.getDefinition().getCombatLevel() * 2)
						&& player.getLocation() != Location.WILDERNESS && !dung) {
					continue;
				}

				if(Location.ignoreFollowDistance(npc)  ||
	 npc.getDefaultPosition().getDistance(player.getPosition()) < 7 +
				 npc.getMovementCoordinator().getCoordinator().getRadius() || dung) {
				if (Location.ignoreFollowDistance(npc) || npc instanceof GlobalBoss
						|| npc.getDefaultPosition().getDistance(player.getPosition()) < 7
								+ npc.getMovementCoordinator().getCoordinator().getRadius()
						|| dung) {

					if (CombatFactory.checkHook(npc, player)) {
						player.setTargeted(true);
						npc.getCombatBuilder().attack(player);
						npc.setFindNewTarget(false);
						break;
					}
				}
			}
		}
	}
	}
}
