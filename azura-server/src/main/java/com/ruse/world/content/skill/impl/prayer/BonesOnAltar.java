package com.ruse.world.content.skill.impl.prayer;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.impl.EnterAmountOfBonesToSacrifice;
import com.ruse.world.content.AchievementsOLD;
import com.ruse.world.content.AchievementsOLD.AchievementDataOLD;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.entity.impl.player.Player;

public class BonesOnAltar {

	public static void openInterface(Player player, int itemId) {
		player.getSkillManager().stopSkilling();
		player.setSelectedSkillingItem(itemId);
		player.setInputHandling(new EnterAmountOfBonesToSacrifice());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(itemId).getName())
				.sendInterfaceModel(1746, itemId, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to offer?");
	}

	public static void offerAllBones(Player player) {
		offerBones(player, player.getInventory().getAmount(player.getSelectedSkillingItem()));
	}

	public static void offerBones(final Player player, final int amount) {
		final int boneId = player.getSelectedSkillingItem();
		player.getSkillManager().stopSkilling();
		final BonesData currentBone = BonesData.forId(boneId);
		if (currentBone == null)
			return;
		player.getPacketSender().sendInterfaceRemoval();
		player.setCurrentTask(new Task(2, player, true) {
			int amountSacrificed = 0;

			@Override
			public void execute() {
				if (amountSacrificed >= amount) {
					stop();
					return;
				}
				if (!player.getInventory().contains(boneId)) {
					player.getPacketSender()
							.sendMessage("You have run out of " + ItemDefinition.forId(boneId).getName() + ".");
					stop();
					return;
				}
				if (player.getInteractingObject() != null) {
					player.setPositionToFace(player.getInteractingObject().getPosition().copy());
					player.getInteractingObject().performGraphic(new Graphic(624));
				}
				Achievements.doProgress(player, Achievements.Achievement.PRAYER_RITUAL);
				Achievements.doProgress(player, Achievements.Achievement.PRAYER_DEVOTION);
				if (currentBone.equals(BonesData.FROSTDRAGON_BONES)) {
					Achievements.doProgress(player, Achievements.Achievement.BURY_ALOT);
				}
				if (currentBone == BonesData.BIG_BONES)
					AchievementsOLD.finishAchievement(player, AchievementDataOLD.BURY_A_BIG_BONE);
				else if (currentBone == BonesData.FROSTDRAGON_BONES) {
					AchievementsOLD.doProgress(player, AchievementDataOLD.BURY_25_FROST_DRAGON_BONES);
					AchievementsOLD.doProgress(player, AchievementDataOLD.BURY_500_FROST_DRAGON_BONES);
				}
				amountSacrificed++;
				player.getInventory().delete(boneId, 1);
				player.performAnimation(new Animation(713));
				if (player.getRights().isMember()) {
					player.getSkillManager().addExperience(Skill.PRAYER, (int) (currentBone.getBuryingXP() * 2.5));
					return;
				} else
					player.getSkillManager().addExperience(Skill.PRAYER, (int) (currentBone.getBuryingXP() * 2));
			}

			@Override
			public void stop() {
				setEventRunning(false);
				player.getPacketSender().sendMessage("You have pleased Crimson with your "
						+ (amountSacrificed == 1 ? "sacrifice" : "sacrifices") + ".");
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}
}
