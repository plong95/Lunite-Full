package com.ruse.world.content.discordbot.command.impl;

import com.ruse.GameSettings;
import com.ruse.util.Misc;
import com.ruse.util.StringUtils;
import com.ruse.world.World;
import com.ruse.world.content.AfkStallSystem;
import com.ruse.world.content.DoubleXPSkillEvent;
import com.ruse.world.content.GokuSystem;
import com.ruse.world.content.skeletalhorror.Prime;
import com.world.content.globalBoss.exoden.GoldenOozau;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

/*
 * @project Vanity-Server
 * @author Patrity - https://github.com/Patrity
 * Created on - 4/13/2020
 */
public class Players  extends ListenerAdapter {
     
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        int players = GameSettings.players + World.getPlayers().size();
        eb.setTitle("Players");
        eb.setDescription("There are currently " + players + " players online!");

        eb.setThumbnail(e.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(new Color(0x296d98));
        e.getChannel().sendMessage(eb.build()).queue();
    }
}
