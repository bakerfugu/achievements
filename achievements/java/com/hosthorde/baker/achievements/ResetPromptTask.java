package com.hosthorde.baker.achievements;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetPromptTask extends BukkitRunnable {

	private final JavaPlugin plugin;
	private Databiased db;
	
	public ResetPromptTask(JavaPlugin plugin, Databiased db) {
		this.plugin = plugin;
		this.db = db;
	}

	public void run() {
		// What you want to schedule goes here
		plugin.getLogger().info("HEY");
		boolean success = db.setGlobally("needsCharityPrompt", "TRUE");

		if (success) {
			Bukkit.broadcastMessage("Hey everyone! Your reward cooldown for the /donate command is over!\n" + 
		"Use it to donate a small amount of money to one of three charities and get 50 gold!");
		} else {
			Bukkit.broadcastMessage("Error connecting to databiased");
		}
	}
	
	public void endTask() {
		cancel();
	}
}
