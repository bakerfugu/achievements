package com.hosthorde.baker.achievements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class WorldManager {

	public WorldManager() {}

	public File getWorldFolder(String source) {

		// The world to copy
		World sourceWorld = Bukkit.getWorld(source);
		File sourceFolder = sourceWorld.getWorldFolder();

		System.out.println("World folder found");

		return sourceFolder;

	}

	public boolean unload(String world) {

		try {
			World toUnload = Bukkit.getWorld(world);
			return Bukkit.getServer().unloadWorld(toUnload, true);
		} catch (Exception e) {
			System.out.println("Failed to unload the world");
			return false;
		}

	}

	public boolean load(String world) {

		try {
			Bukkit.getServer().createWorld(new WorldCreator(world));
			return true;
		} catch (Exception e) {
			System.out.println("Failed to load the world");
			return false;
		}

	}

	public void kickPlayers(String world) {

		int count = 0;

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {

			System.out.println("Looking at player #" + count);

			if (player.getWorld().getName().equalsIgnoreCase(world)) {

				System.out.println("Player WAS in " + world);

				player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
			} else {
				System.out.println("Player was NOT in " + world + "He was in " + player.getWorld().getName());
			}

			count++;
		}

	}

	public void copyWorld(String source, String target) {

		File sourceWorld = getWorldFolder(source);
		File targetWorld = getWorldFolder(target);

		kickPlayers(source);
		kickPlayers(target);

		unload(target);

		deleteWorldHelper(targetWorld, targetWorld);
		copyWorldHelper(sourceWorld, targetWorld);

		load(target);

	}

	public boolean deleteWorldHelper(File path, File topMost) {
		if(path.exists()) {
			File files[] = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteWorldHelper(files[i], topMost);
				} else {
					files[i].delete();
				}
			}
		}
		if (path.getName().equalsIgnoreCase(topMost.getName())) {
			return true;
		} else {
			return(path.delete());
		}
	}

	public void copyWorldHelper(File source, File target){

		try {
			ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
			if(!ignore.contains(source.getName())) {
				if(source.isDirectory()) {
					if(!target.exists())
						target.mkdirs();
					String files[] = source.list();
					for (String file : files) {
						File srcFile = new File(source, file);
						File destFile = new File(target, file);
						copyWorldHelper(srcFile, destFile);
					}
				} else {
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0)
						out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {

		}
	}

}