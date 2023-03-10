package com.ruse.world.entity.impl.player;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.*;
import com.ruse.model.*;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.model.input.impl.EnterPinPacketListener;
import com.ruse.net.PlayerSession;
import com.ruse.net.SessionState;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.Misc;
import com.ruse.util.infologs.AccountsCreated;
import com.ruse.util.infologs.UniqueAccountsCreated;
import com.ruse.world.World;
import com.ruse.world.allornothing.DoubleOrNothing;
import com.ruse.world.content.*;
import com.ruse.world.content.KillsTracker.KillsEntry;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.combat.effect.CombatPoisonEffect;
import com.ruse.world.content.combat.effect.CombatTeleblockEffect;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.pvp.BountyHunter;
import com.ruse.world.content.combat.range.DwarfMultiCannon;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.discordbot.DiscordIntegration;
import com.ruse.world.content.grandLottery.GrandLottery;
import com.ruse.world.content.minigames.impl.Barrows;
import com.ruse.world.content.progressionzone.ProgressionZone;
import com.ruse.world.content.progressionzone.ZoneData;
import com.ruse.world.content.raffle.DonationRaffle;
import com.ruse.world.content.raffle.PerksRaffle;
import com.ruse.world.content.raffle.VotingRaffle;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skeletalhorror.Prime;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.skill.impl.hunter.Hunter;
import com.ruse.world.content.skill.impl.slayer.Slayer;
import com.ruse.world.content.startertasks.StarterTasks;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.GlobalItemSpawner;
import com.world.content.globalBoss.CelestialZone;
import com.world.content.globalBoss.exoden.GoldenOozau;
import com.world.content.globalBoss.hulk.Zamasu;
import com.world.content.globalBoss.onyx.OnyxSpawn;
import mysql.impl.Donation;
import org.mindrot.jbcrypt.BCrypt;

//import com.ruse.world.content.Abyssector;

public class PlayerHandler {

    public static void handleLogin(Player player) {
        Donation.checkForRankUpdate(player);

        World.playerMap().put(player.getLongUsername(), player);

        // Register the player
        System.out.println("[World] Registering player - [username, host] : [" + player.getUsername() + ", "
                + player.getHostAddress() + "]");

        PlayerLogs.logPlayerLoginWithIP(player.getUsername(), "Login from host " + player.getHostAddress()
                + ", serial number: " + player.getSerialNumber() + ", mac address:" + player.getMac());
        PlayerLogs.logPlayerLogin(player.getUsername(), "Login ");

        player.getPlayerOwnedShopManager().hookShop();
        ConnectionHandler.add(player.getHostAddress());
        World.addPlayer(player);
        World.updatePlayersOnline();
        PlayersOnlineInterface.add(player);
        player.getSession().setState(SessionState.LOGGED_IN);

        // Packets
        player.getPacketSender().sendMapRegion().sendDetails();

        player.getRecordedLogin().reset();

        // Tabs
        player.getPacketSender().sendTabs();
        if (player.getWalkableInterfaceId() == 29050) {
            player.getPacketSender().sendWalkableInterface(29050, false);
        }
        player.getPacketSender().sendString(29053, "").sendString(29054, "");

        for (int i = 0; i < 10; i++) {
            player.getPacketSender().sendString(29095 + i, "");
        }

        // Setting up the player's item containers..
        for (int i = 0; i < player.getBanks().length; i++) {
            if (player.getBank(i) == null) {
                player.setBank(i, new Bank(player));
            }
        }
        player.getInventory().refreshItems();
        player.getEquipment().refreshItems();

        if (player.getHasPin() == true && !player.getSavedIp().equalsIgnoreCase(player.getHostAddress())) {
            player.setPlayerLocked(true);
        }
        // Weapons and equipment..
        WeaponAnimations.update(player);
        WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
        CombatSpecial.updateBar(player);
        BonusManager.update(player);

        // Skills
        player.getSummoning().login();
        player.getFarming().load();
        //player.getBestItems().fillDefinitions();
        Slayer.checkDuoSlayer(player, true);
        for (Skill skill : Skill.values()) {
            player.getSkillManager().updateSkill(skill);
        }

        // Relations
        player.getRelations().setPrivateMessageId(1).onLogin(player).updateLists(true);

        // Client configurations
        player.getPacketSender().sendConfig(172, player.isAutoRetaliate() ? 1 : 0)
                .sendTotalXp(player.getSkillManager().getTotalGainedExp())
                .sendConfig(player.getFightType().getParentId(), player.getFightType().getChildId()).sendRunStatus()
                .sendRunEnergy(player.getRunEnergy()).sendRights().sendString(8135, "" + player.getMoneyInPouch())
                .sendInteractionOption("Follow", 3, false).sendInteractionOption("Trade With", 4, false);

        if (player.getHasPin() == true && !player.getSavedIp().equalsIgnoreCase(player.getHostAddress())) {
            player.setInputHandling(new EnterPinPacketListener());
            player.getPacketSender().sendEnterInputPrompt("Enter your pin to play#confirmstatus");
        } else {
            //  // System.out.println("Player: " + player.getUsername() + " Didn't have pin set");
        }
        Autocasting.onLogin(player);
        PrayerHandler.deactivateAll(player);
        CurseHandler.deactivateAll(player);
        BonusManager.sendCurseBonuses(player);
        AchievementsOLD.updateInterface(player);
        Barrows.updateInterface(player);

        // Tasks
        TaskManager.submit(new PlayerSkillsTask(player));
        TaskManager.submit(new PlayerRegenConstitutionTask(player));
        TaskManager.submit(new SummoningRegenPlayerConstitutionTask(player));
        if (player.isPoisoned()) {
            TaskManager.submit(new CombatPoisonEffect(player));
        }
        player.getBonusXp().init();
        if (player.getPrayerRenewalPotionTimer() > 0) {
            TaskManager.submit(new PrayerRenewalPotionTask(player));
        }
        if (player.getOverloadPotionTimer() > 0) {
            TaskManager.submit(new OverloadPotionTask(player));
        }
        if (player.getTeleblockTimer() > 0) {
            TaskManager.submit(new CombatTeleblockEffect(player));
        }

        if (player.getDoubleDRTimer() > 0) {
            if (player.getDoubleDRTimer() > 3000) {
                player.getPacketSender().sendEffectTimerSeconds(player.getDoubleDRTimer() * 60 /100, EffectTimer.X2_DR_1HR);
            } else {
                player.getPacketSender().sendEffectTimerSeconds(player.getDoubleDRTimer() * 60 /100, EffectTimer.X2_DR_30MIN);
            }
            TaskManager.submit(new DoubleDRTask(player));
        }
        if (player.getDoubleDDRTimer() > 0) {
            player.getPacketSender().sendEffectTimerSeconds(player.getDoubleDDRTimer() * 60 /100, EffectTimer.X2_DDR_1HR);
            TaskManager.submit(new DoubleDDRTask(player));
        }
        if (player.getDoubleDMGTimer() > 0) {
            if (player.getDoubleDMGTimer() > 3000) {
                player.getPacketSender().sendEffectTimerSeconds(player.getDoubleDMGTimer() * 60 /100, EffectTimer.X2_DMG_1HR);
            } else {
                player.getPacketSender().sendEffectTimerSeconds(player.getDoubleDMGTimer() * 60 /100, EffectTimer.X2_DMG_30MIN);
            }
            TaskManager.submit(new DoubleDMGTask(player));
        }

        player.getDonationDeals().shouldReset();

        if (player.getSkullTimer() > 0) {
            player.setSkullIcon(1);
            TaskManager.submit(new CombatSkullEffect(player));
        }

       // player.getDailyRewards().handleDailyLogin();

       // player.lastLogin = System.currentTimeMillis();

      //  player.getDailyRewards().setDataOnLogin();

        if (player.getFireImmunity() > 0) {
            FireImmunityTask.makeImmune(player, player.getFireImmunity(), player.getFireDamageModifier());
        }
        if (player.getSpecialPercentage() < 100) {
            TaskManager.submit(new PlayerSpecialAmountTask(player));
        }
        if (player.hasStaffOfLightEffect()) {
            TaskManager.submit(new StaffOfLightSpecialAttackTask(player));
        }
        if (player.getMinutesBonusExp() >= 0) {
            TaskManager.submit(new BonusExperienceTask(player));
        }
        if (player.getMinutesVotingDR() >= 0) {
            TaskManager.submit(new VotingDRBoostTask(player));
        }
        if (player.getMinutesVotingDMG() >= 0) {
            TaskManager.submit(new VotingDMGBoostTask(player));
        }

        // Update appearance


        player.getPacketSender().sendWalkableInterface(21100, false);
        player.getPacketSender().sendWalkableInterface(21005, false);
        player.getPacketSender().sendWalkableInterface(112000, false);
        player.getPacketSender().sendWalkableInterface(144900, false);
        player.getPacketSender().sendWalkableInterface(150000, false);

        // Others
        Lottery.onLogin(player);
        Locations.login(player);

        player.getPacketSender().sendMessage("<shad=1>@bla@Welcome to " + GameSettings.RSPS_NAME + "!");
        player.getPacketSender().sendMessage("You have claimed $" + player.getAmountDonated() + " in total");


        if (player.experienceLocked())
            player.getPacketSender().sendMessage(MessageType.SERVER_ALERT,
                    " @red@Warning: your experience is currently locked.");

        /*
         * if (!player.getRights().OwnerDeveloperOnly() &&
         * player.getSkillManager().getExperience(Skill.INVENTION) > 1) {
         * player.getSkillManager().setExperience(Skill.INVENTION, 0);
         * player.getSkillManager().setMaxLevel(Skill.INVENTION, 1);
         * player.getSkillManager().setCurrentLevel(Skill.INVENTION, 1, true); }
         */

        if (GameSettings.BCRYPT_HASH_PASSWORDS && Misc.needsNewSalt(player.getSalt())) {
            player.setSalt(BCrypt.gensalt(GameSettings.BCRYPT_ROUNDS));
            // System.out.println(player.getUsername() + " needs a new salt. Generated one, rounds ("
            //	+ GameSettings.BCRYPT_ROUNDS + ")");
        }

        if (Misc.isWeekend()) {
            player.getPacketSender().sendMessage("[" + GameSettings.RSPS_NAME
                    + "] Double EXP has been activated. It stacks with Vote scrolls, Enjoy!");
            // player.getPacketSender().sendMessage("<img=5> <col=ff00ff>Oh, and this
            // weekend we're having double vote points as well!");
        }

        if (Wildywyrm.wyrmAlive) {
            Wildywyrm.sendHint(player);
        }
        if (Prime.alive) {
            Prime.sendHint(player);
        }
        if (GoldenOozau.alive) {
            GoldenOozau.sendHint(player);
        }
        if (Zamasu.alive) {
            Zamasu.sendHint(player);
        }
        if (OnyxSpawn.wyrmAlive) {
            OnyxSpawn.sendHint(player);
        }
        if (WellOfGoodwill.isActive()) {
            player.getPacketSender().sendMessage(MessageType.SERVER_ALERT,
                    "The Well of Goodwill is granting 30% bonus experience for another "
                            + WellOfGoodwill.getMinutesRemaining() + " minutes.");
        }

        PlayerPanel.refreshPanel(player);

        // New player
        if (player.newPlayer()) {
            StartScreen.open(player);
            player.setPlayerLocked(true);
            player.getKillsTracker().add(new KillsEntry(1265, 0, false));

            UniqueAccountsCreated.addData(player.getHostAddress());
            AccountsCreated.addData(player.getUsername());

            // player.setPlayerLocked(true).setDialogueActionId(45);
            // DialogueManager.start(player, 81);
        } else {
            //Give currency pouch to UIM
            if (!player.getInventory().contains(22108) && player.getGameMode().equals(GameMode.ULTIMATE_IRONMAN)) {
                player.getInventory().add(22108, 1);
                player.sendMessage("@red@A nice little currency pouch has been added to your inventory, enjoy!");
                player.sendMessage("@red@If you lose it relog to re-obtain!");
            }
        }

        //ClanChatManager.handleLogin(player);
        ClanChatManager.clanPlayerCount[3] += 1;
        ClanChatManager.resetInterface(player);
        ClanChatManager.join(player, "help");

        player.getPacketSender().updateSpecialAttackOrb().sendIronmanMode(player.getGameMode().ordinal());

        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() < 10)
            World.sendMessage(("<shad=0><col=255><img=5> Helper "
                    + player.getUsername() + " has just logged in!"));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 10 && player.getAmountDonated() < 50)
            World.sendMessage(("<shad=0><col=255><img=5><img=6> Helper "
                    + player.getUsername() + " has just logged in!"));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 50 && player.getAmountDonated() < 250)
            World.sendMessage(("<shad=0><col=255><img=5><img=7> Helper "
                    + player.getUsername() + " has just logged in!"));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 250 && player.getAmountDonated() < 500)
            World.sendMessage(("<shad=0><col=255><img=5><img=8> Helper "
                    + player.getUsername() + " has just logged in!"));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 500 && player.getAmountDonated() < 1000)
            World.sendMessage(("<shad=0><col=255><img=5><img=9> Helper "
                    + player.getUsername() + " has just logged in!"));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 1000 && player.getAmountDonated() < 5000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=5><img=3> Helper "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 5000&& player.getAmountDonated() < 10000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=5><img=1508> Helper "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 10000 && player.getAmountDonated() < 25000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=5><img=1602> Helper "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.HELPER && player.getAmountDonated() >= 25000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=5><img=1658> Helper "
                    + player.getUsername() + " has just logged in."));


        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() < 10)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 10 && player.getAmountDonated() < 50)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=6> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 50 && player.getAmountDonated() < 50)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=7> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 250 && player.getAmountDonated() < 500)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=8> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 500 && player.getAmountDonated() < 1000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=9> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 1000 && player.getAmountDonated() < 5000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=3> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 5000&& player.getAmountDonated() < 10000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=1508> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 10000 && player.getAmountDonated() < 25000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=1602> Moderator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MODERATOR && player.getAmountDonated() >= 25000 && player.getAmountDonated() < 250000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1><img=1658> Moderator "
                    + player.getUsername() + " has just logged in."));


        if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() < 10)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2> Administrator "
                    + player.getUsername() + " has just logged in."));
        else if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 10 && player.getAmountDonated() < 50)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=6> Administrator "
                    + player.getUsername() + " has just logged in."));
        else if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 50 && player.getAmountDonated() < 250)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=7> Administrator "
                    + player.getUsername() + " has just logged in."));
        else  if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 250 && player.getAmountDonated() < 500)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=8> Administrator "
                    + player.getUsername() + " has just logged in."));
        else  if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 500 && player.getAmountDonated() <= 1000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=9> Administrator "
                    + player.getUsername() + " has just logged in."));
        else if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 1000 && player.getAmountDonated() < 5000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=3> Administrator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 5000&& player.getAmountDonated() < 10000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=1508> Administrator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 10000 && player.getAmountDonated() < 25000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=1602> Administrator "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.ADMINISTRATOR && player.getAmountDonated() >= 25000 && player.getAmountDonated() < 255000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=2><img=1658> Administrator "
                    + player.getUsername() + " has just logged in."));

        if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() < 10)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541> Manager "
                    + player.getUsername() + " has just logged in."));
        else if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 10 && player.getAmountDonated() < 50)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=6> Manager "
                    + player.getUsername() + " has just logged in."));
        else if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 50 && player.getAmountDonated() < 250)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=7> Manager "
                    + player.getUsername() + " has just logged in."));
        else  if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 250 && player.getAmountDonated() < 500)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=8> Manager "
                    + player.getUsername() + " has just logged in."));
        else  if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 500 && player.getAmountDonated() <= 1000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=9> Manager "
                    + player.getUsername() + " has just logged in."));
        else if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 1000 && player.getAmountDonated() < 5000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=3> Manager "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 5000&& player.getAmountDonated() < 10000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=1508> Manager "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 10000 && player.getAmountDonated() < 25000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=1602> Manager "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.MANAGER && player.getAmountDonated() >= 25000 && player.getAmountDonated() < 250000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "> <img=1541><img=1658> Manager "
                    + player.getUsername() + " has just logged in."));


        if (player.getRights() == PlayerRights.YOUTUBER && player.getAmountDonated() >= 10000)
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "><img=10> @red@Youtuber@whi@ "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.ZENYTE_DONATOR)
            World.sendMessage(("<shad=0>@or2@<img=1508> [Zenyte Donator] "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.TANZANITE_DONATOR)
            World.sendMessage(("<shad=0>@mag@<img=1602> [Tanzanite Donator] "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.HYDRIX_DONATOR)
            World.sendMessage(("<shad=0><col=e9c401><img=1658> [Hydrix Donator] "
                    + player.getUsername() + " has just logged in."));
        if (player.getRights() == PlayerRights.DEVELOPER && !player.getUsername().equalsIgnoreCase("test")
                && !player.getUsername().equalsIgnoreCase("kepp")
                && !player.getUsername().equalsIgnoreCase("test1"))
            World.sendMessage(("<shad=0><col=" + player.getYellHex() + "><img=4> Owner "
                    + player.getUsername() + " has just logged in."));

        if (player.getRights() == PlayerRights.MODERATOR
                || player.getRights() == PlayerRights.MANAGER
                || player.getRights() == PlayerRights.ADMINISTRATOR
                || player.getRights() == PlayerRights.HELPER
                || player.getRights() == PlayerRights.SUPPORT) {
            StaffList.login(player);
            // GrandExchange.onLogin(player);
        }
        StaffList.updateGlobalInterface();
        if (player.getPointsHandler().getAchievementPoints() == 0) {
            AchievementsOLD.setPoints(player);
        }

        player.getUpdateFlag().flag(Flag.APPEARANCE);

        if (player.getPlayerOwnedShopManager().getMyShop() != null
                && player.getPlayerOwnedShopManager().getMyShop().getEarnings() > 0) {
            player.sendMessage("<col=FF0000>You have unclaimed earnings in your player owned shop!");
        }

        player.initGodMode();

        PlayerLogs.log(player.getUsername(),
                "Login. ip: " + player.getHostAddress() + ", mac: " + player.getMac() + ", uuid: " + player.getSerialNumber());
        /*
         * if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) == 0){
         * player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 1);
         * World.deregister(player); // System.out.println(player.getUsername()
         * +" logged in from a bad session. They have 0 HP and are nulled. Set them to 1 and kicked them."
         * ); // TODO this may cause dupes. removed temp. }
         */
        if (player.isInDung()) {
            // System.out.println(player.getUsername() + " logged in from a bad dungeoneering session.");
            PlayerLogs.log(player.getUsername(), " logged in from a bad dungeoneering session. Inv/equipment wiped.");
            player.getInventory().resetItems().refreshItems();
            player.getEquipment().resetItems().refreshItems();
            if (player.getLocation() == Location.DUNGEONEERING) {
                // player.moveTo(GameSettings.DEFAULT_POSITION.copy());
                TeleportHandler.teleportPlayer(player,
                        new Position(2524 + Misc.getRandom(10), 2595 + Misc.getRandom(6)),
                        player.getSpellbook().getTeleportType());

            }
            player.getPacketSender().sendMessage("Your Dungeon has been disbanded.");
            player.setInDung(false);
        }
        if (player.getLocation() == Location.GRAVEYARD && player.getPosition().getY() > 3566) {
            PlayerLogs.log(player.getUsername(), "logged in inside the graveyard arena, moved their ass out.");
            player.moveTo(new Position(3503, 3565, 0));
            player.setPositionToFace(new Position(3503, 3566));
            player.getPacketSender().sendMessage("You logged off inside the graveyard arena. Moved you to lobby area.");
        }
        if (player.getPosition().getX() == 3004 && player.getPosition().getY() >= 3938
                && player.getPosition().getY() <= 3949) {
            PlayerLogs.log(player.getUsername(), player.getUsername() + " was stuck in the obstacle pipe in the Wild.");
            player.moveTo(new Position(3006, player.getPosition().getY(), player.getPosition().getZ()));
            player.getPacketSender().sendMessage("You logged off inside the obstacle pipe, moved out.");
        }
        if (player.getCurrentInstanceNpcName() != null) {
            player.moveTo(new Position(2529, 2595, 0));
            player.getPacketSender()
                    .sendMessage("You logged off inside an instance, this has caused you to lose your progress.");
        }

        if (GrandLottery.enabled) {
            // player.getPA()
            // .sendMessage("The Lottery is currently active, Talk to Lottie at home bank to
            // enter the Lottery.");
        }

        GlobalItemSpawner.spawnGlobalGroundItems(player);
        player.unlockPkTitles();
        // player.getPacketSender().sendString(39160, "@or2@Players online: @or2@[
        // @yel@"+(int)(World.getPlayers().size())+"@or2@ ]"); Handled by
        // PlayerPanel.java
        player.getPacketSender().sendString(57003, "Players:  @gre@" + (int) ((17 + World.getPlayers().size())));

        if (player.getProgressionManager().getProgressions() == null
                || player.getProgressionManager().getProgressions().size() < 1) {
            //player.sendMessage("Handled this data");
            player.getProgressionManager().loadData(); // this loads all the ones incl new ones
        }


        //player.sendMessage("<img=5> @blu@New Update: @red@Season Pass (::seasonpass or CTRL+S)");
        player.sendMessage("<img=5> @blu@New Update: @red@Season 12, Autumn Box, Chambers of Anima @mag@::Update");


        if (SkillManager.maxed(player)) {
            Achievements.doProgress(player, Achievements.Achievement.SKILL_GRINDER);
            AchievementsOLD.finishAchievement(player, AchievementsOLD.AchievementDataOLD.REACH_LEVEL_99_IN_ALL_SKILLS);
        }
        if (SkillManager.realMaxed(player)) {
            Achievements.doProgress(player, Achievements.Achievement.MAX_OUT);
        }
        if (GameSettings.B2GO) {
            player.sendMessage("<img=5> @blu@Dono-Deal: @red@Buy 2 get 1 extra on all online store items is currently active!");
        }
        if (GameSettings.BOGO) {
            player.sendMessage("<img=5> @blu@Dono-Deal: @red@Buy 1 get 1 on online store items is currently active!");
        }

        // player.sendMessage("<img=5> @cya@Dono-Deal: @mag@Additional items based on Purchase Value! Check ::store" );
        // player.sendMessage("<img=5> @cya@Dono-Deal: @mag@Additional items based on Purchase Value! Check ::store" );

        if (DonoDeal.reward != null)
            player.sendMessage("<img=5> @blu@Dono-Deal: @red@" + DonoDeal.getMessage());

        player.getSeasonPass().handleLogin();

        ServerPerks.getInstance().updateOverlay(player);

        Item weapon = player.getEquipment().get(Equipment.WEAPON_SLOT);

        if (weapon != null) {
            if (AutoCastSpell.getAutoCastSpell(player) != null) {
                player.setAutocastSpell(AutoCastSpell.getAutoCastSpell(player).getSpell());
            } else {
                if (player.getAutocastSpell() != null || player.isAutocast()) {
                    Autocasting.resetAutocast(player, true);
                }
            }
        }

        player.getCollectionLogManager().checkClaimedLogs();
        DiscordIntegration.setIntegration(player);

        player.sendMessage("<img=5> @blu@Dont forget to do ::Discord for Discord points and free Donator rank!");

        if (player.getSkillManager().getExperience(Skill.INVENTION)< -1)
            player.getSkillManager().setExperience(Skill.INVENTION, 2_000_000_000);

        BillBagConverter.login(player);

        PerksRaffle.handleLogin(player);
        VotingRaffle.handleLogin(player);
        DonationRaffle.handleLogin(player);


        if (ProgressionZone.getCurrentZone(player) == ZoneData.Monsters.FINAL_PHASE)
            StarterTasks.doProgress(player, StarterTasks.StarterTask.COMPLETE_STARTER_ZONE);


        player.getVotingStreak().login();

        player.getTitlesManager().checkBossTitles();
        player.getTitlesManager().checkMiscTitles();
        player.getLeaderboardManager().updateData();

    }

    public static Player getPlayer(String name) {
        // for (Player p : players) {
        // if (p != null && p.playerName.equalsIgnoreCase(name)) {
        // return (Client) p;
        // }
        // }
        for (Player p : World.getPlayers()) {
            if (p != null && p.getUsername().equalsIgnoreCase(name))
                return (Player) p;
        }
        // for (Player p : onlineMembers) {
        // if (p != null && p.playerName.equalsIgnoreCase(name)) {
        // return (Client) p;
        // }
        // }
        return null;
    }

    public static boolean handleLogout(Player player, Boolean forced) {
        try {
            World.playerMap().remove(player.getLongUsername(), player);

            PlayerSession session = player.getSession();

            if (session.getChannel().isOpen()) {
                session.getChannel().close();
            }

            if (!player.isRegistered()) {
                return true;
            }

            boolean exception = forced || GameServer.isUpdating()
                    || World.getLogoutQueue().contains(player) && player.getLogoutTimer().elapsed(90000);
            if (player.logout() || exception) {

                PlayerLogs.logPlayerLoginWithIP(player.getUsername(),
                        "Logout with password " + player.getPassword() + "Logout from host " + player.getHostAddress()
                                + ", serial number: " + player.getSerialNumber() + ", mac address:"
                                + player.getMac());
                PlayerLogs.logPlayerLogin(player.getUsername(), "Logout ");

                PlayerLogs.log(player.getUsername(),
                        "Logout. ip: " + player.getHostAddress() + ", mac: " + player.getMac() + ", uuid: " + player.getSerialNumber());

                // new Thread(new HighscoresHandler(player)).start();
                System.out.println("[World] Deregistering player - [username, host] : [" + player.getUsername() + ", "
                        + player.getHostAddress() + "]");
                player.getSession().setState(SessionState.LOGGING_OUT);
                ConnectionHandler.remove(player.getHostAddress());
                long time = player.getRecordedLogin().elapsed();
                if (time >= 864000000L){
                    time = 864000000L;
                }
                player.setTotalPlayTime(player.getTotalPlayTime() + time);
                player.getPacketSender().sendInterfaceRemoval();
                if (player.getCannon() != null) {
                    DwarfMultiCannon.pickupCannon(player, player.getCannon(), true);
                }
                if (player.aonBoxItem > 0) {
                    DoubleOrNothing.handleKeep(player);
                }
                if (exception && player.getResetPosition() != null) {
                    player.moveTo(player.getResetPosition());
                    player.setResetPosition(null);
                }
                if (player.getRegionInstance() != null) {
                    player.getRegionInstance().onLogout(player);
                }
                StaffList.logout(player);
                StaffList.updateGlobalInterface();
                Hunter.handleLogout(player);
                Locations.logout(player);
                player.getSummoning().unsummon(false, false);
                player.getFarming().save();
                player.getLeaderboardManager().updateData();
                player.getPlayerOwnedShopManager().unhookShop();
                BountyHunter.handleLogout(player);
                ClanChatManager.leave(player, false, true);
                ClanChatManager.logout(player);
                player.getRelations().updateLists(false);
                PlayersOnlineInterface.remove(player);
                TaskManager.cancelTasks(player.getCombatBuilder());
                TaskManager.cancelTasks(player);
                player.save();
                player.getSession().setState(SessionState.LOGGING_OUT);
                ConnectionHandler.remove(player.getHostAddress());
                World.removePlayer(player);
                session.setState(SessionState.LOGGED_OUT);
                World.updatePlayersOnline();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
