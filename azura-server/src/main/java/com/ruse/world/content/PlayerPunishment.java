package com.ruse.world.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ruse.engine.GameEngine;
import com.ruse.util.Misc;
import lombok.Getter;

public class PlayerPunishment {

	private static final String BAN_DIRECTORY = "./data/saves/";
	private static final String MUTE_DIRECTORY = "./data/saves/";

	public static ArrayList<String> IPSBanned = new ArrayList<String>();
	public static ArrayList<String> IPSMuted = new ArrayList<String>();
	public static ArrayList<String> AccountsBanned = new ArrayList<String>();
	public static ArrayList<String> AccountsMuted = new ArrayList<String>();
	public static ArrayList<String> AccountsTempMuted = new ArrayList<String>();

	public static void init() {
		initializeList(BAN_DIRECTORY, "IPBans", IPSBanned);
		initializeList(BAN_DIRECTORY, "Bans", AccountsBanned);
		initializeList(MUTE_DIRECTORY, "IPMutes", IPSMuted);
		initializeList(MUTE_DIRECTORY, "Mutes", AccountsMuted);
		initializeList(MUTE_DIRECTORY, "TempMutes", AccountsTempMuted);
	}

	public static void initializeList(String directory, String file, ArrayList<String> list) {
		try {
			Misc.createFilesIfNotExist("" + directory + "" + file + ".txt", false);
			BufferedReader in = new BufferedReader(new FileReader("" + directory + "" + file + ".txt"));
			String data = null;
			while ((data = in.readLine()) != null) {
				list.add(data);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addBannedIP(String IP) {
		if (!IPSBanned.contains(IP)) {
			addToFile("" + BAN_DIRECTORY + "IPBans.txt", IP);
			IPSBanned.add(IP);
		}
	}

	public static void addMutedIP(String IP) {
		if (!IPSMuted.contains(IP)) {
			addToFile("" + MUTE_DIRECTORY + "IPMutes.txt", IP);
			IPSMuted.add(IP);
		}
	}

	public static void ban(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		if (!AccountsBanned.contains(p)) {
			addToFile("" + BAN_DIRECTORY + "Bans.txt", p);
			AccountsBanned.add(p);
		}
	}

	public static void mute(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		if (!AccountsMuted.contains(p)) {
			addToFile("" + MUTE_DIRECTORY + "Mutes.txt", p);
			AccountsMuted.add(p);
		}
	}

	public static void tempMute(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		if (!AccountsTempMuted.contains(p)) {
			addToFile("" + MUTE_DIRECTORY + "TempMutes.txt", p);
			AccountsTempMuted.add(p);
		}
		long start = System.currentTimeMillis();
	}

	public static boolean banned(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsBanned.contains(player);
	}

	public static boolean tempMuted(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsTempMuted.contains(player);
	}

	public static boolean muted(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsMuted.contains(player);
	}

	public static boolean IPBanned(String IP) {
		return IPSBanned.contains(IP);
	}

	public static boolean IPMuted(String IP) {
		return IPSMuted.contains(IP);
	}

	public static void unban(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		deleteFromFile("" + BAN_DIRECTORY + "Bans.txt", player);
		AccountsBanned.remove(player);
	}

	public static void unmute(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		deleteFromFile("" + MUTE_DIRECTORY + "Mutes.txt", player);
		AccountsMuted.remove(player);
	}

	public static void unTempMute(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		deleteFromFile(MUTE_DIRECTORY + "TempMutes.txt", player);
		AccountsTempMuted.remove(player);
	}

	public static void reloadIPBans() {
		AccountsBanned.clear();
		initializeList(BAN_DIRECTORY, "Bans", AccountsBanned);
		IPSBanned.clear();
		initializeList(BAN_DIRECTORY, "IPBans", IPSBanned);
	}

	public static void reloadIPMutes() {
		IPSMuted.clear();
		initializeList(MUTE_DIRECTORY, "IPMutes", IPSMuted);
	}

	public static void deleteFromFile(String file, String name) {
		GameEngine.submit(() -> {
			try {
				BufferedReader r = new BufferedReader(new FileReader(file));
				ArrayList<String> contents = new ArrayList<String>();
				while (true) {
					String line = r.readLine();
					if (line == null) {
						break;
					} else {
						line = line.trim();
					}
					if (!line.equalsIgnoreCase(name)) {
						contents.add(line);
					}
				}
				r.close();
				BufferedWriter w = new BufferedWriter(new FileWriter(file));
				for (String line : contents) {
					w.write(line, 0, line.length());
					w.newLine();
				}
				w.flush();
				w.close();
			} catch (Exception e) {
			}
		});
	}

	public static void addToFile(String file, String data) {
		GameEngine.submit(() -> {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				try {
					out.newLine();
					out.write(data);
				} finally {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static class Jail {
		private static final List<String> jailedPlayers = new ArrayList<>();

		public static boolean jailPlayer(String name) {
			return jailedPlayers.add(Misc.formatPlayerName(name.toLowerCase()));
		}

		public static boolean unJail(String name) {
			return jailedPlayers.remove(Misc.formatPlayerName(name.toLowerCase()));
		}

		public static boolean isJailed(String name) {
			return jailedPlayers.contains(Misc.formatPlayerName(name.toLowerCase()));
		}
	}

}
