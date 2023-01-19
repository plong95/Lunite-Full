package com.ruse.model;

//import com.ruse.model.container.impl.Bank;
//import com.ruse.model.container.impl.Bank;
//import com.ruse.world.content.PlayerPanel;
//import com.ruse.world.content.Achievements.AchievementData;
//import com.ruse.world.content.dialogue.DialogueManager;
//import com.ruse.world.content.dialogue.impl.Tutorial;
import com.ruse.world.content.skill.impl.old_dungeoneering.UltimateIronmanHandler;
import com.ruse.world.entity.impl.player.Player;
import org.checkerframework.checker.units.qual.C;
//import com.ruse.world.content.skill.impl.slayer.SlayerMaster;
//import com.ruse.world.content.skill.impl.slayer.SlayerTasks;

public enum Faction {

	ROGUE, CORRUPTED, FABLED, HOLY;

	public static void set(Player player, Faction newFaction, boolean death) {
		player.getPacketSender().sendInterfaceRemoval();
		if (newFaction != player.getFaction() || death) {
		}
		player.setFaction(newFaction);
		if (!death) {
			player.getPacketSender()
					.sendMessage(
							"You've set your Faction to @red@" + newFaction.name().toLowerCase().replaceAll("_", " ") + ".")
					.sendMessage("If you wish to change it, please talk to the town crier at home.");
		} else {
			player.getPacketSender().sendMessage("Your account progress has been reset.");
		}
		if (player.newPlayer()) {
			player.setPlayerLocked(true);

		} else {
			player.setPlayerLocked(false);
		}
	}
}





/*public static int getDifficultyModifier(Player player, Skill skill) { TODO - Add special bonuses to each faction here
		Faction d = player.getFaction();
		if (d == ROGUE)
			//return skill.getFunExperienceModifier();
		if (d == CORRUPTED)
			//return skill.getEasyExperienceModifier();
		if (d == FABLED)
			//return skill.getRegularExperienceModifier();
		if (d == HOLY)
			return skill.getHardExperienceModifier();
		return 1;
	}*/