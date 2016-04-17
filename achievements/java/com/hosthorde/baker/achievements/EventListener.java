package com.hosthorde.baker.achievements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


//import net.minecraft.server.NBTTagCompund;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

public class EventListener implements Listener{
	private final achievements plugin;
	private Databiased db;

	public EventListener(achievements m) {
		this.plugin = m;
		m.getServer().getPluginManager().registerEvents(this, m);
		this.db = m.getConnection();
		if (db == null) {
			m.getLogger().info("This is really stupid");
		} else {
			m.getLogger().info("DB IZ NOT NULL OK");
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent evt) {

		Player player = evt.getPlayer();
		player.sendMessage("You picked up an item. Is it speshul item tho? Probs not");
		PlayerInventory inventory = player.getInventory();
		String playerName = player.getName();

		Boolean winner = evt.getItem().getItemStack().containsEnchantment(Enchantment.ARROW_DAMAGE) && (evt.getItem().getItemStack().getType() == Material.DIRT);
		//      Databiased db2 = new Databiased();
		player.sendMessage("hai friend, I have murdured " + db.getPlayerStat(playerName, "Currency") + " kittens.");

		if (winner) {
			player.sendMessage("You are the champion my acquaintance!");
			int currentMonies = db.getPlayerStat(playerName, "Currency");
			boolean MoniesAdd = db.setPlayerStat(playerName, "Currency", "" + (currentMonies + 2000));

			if(MoniesAdd) {
				player.sendMessage(playerName + " has successfully multiplicated the monies total by one! " + playerName + " now has monies total: " + db.getPlayerStat(playerName, "Currency"));
			} else {
				player.sendMessage(playerName + " has failed in their trial to multiplicate the monies total!");
			}
			World homeWorld = Bukkit.getWorld("world");
			Location home = new Location(homeWorld, 
					homeWorld.getSpawnLocation().getX(), 
					homeWorld.getSpawnLocation().getY(), 
					homeWorld.getSpawnLocation().getZ());
			player.teleport(home);

			try {

				WorldManager CopyCat = new WorldManager();
				CopyCat.copyWorld("TheWalls", "TestingWalls");
				player.sendMessage(playerName + " has copied the world. Hopefully. I am full of hope that the target folder will be full of files");

			} catch (Exception e) {

				player.sendMessage(playerName + " failed to doppelgang.");

			}
		} else {
			player.sendMessage("DID NOT GET.");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		String uuid = player.getUniqueId().toString().replace("-","");

		player.sendMessage("Hey, you are of existence in server at this time! your uuid is: " + uuid);

		plugin.getLogger().info("\n Hello, yes, we have a new player in the arena! \n \n");

		if (db.getPlayerStat(player.getName(), "needsCharityPrompt") == 1) {
			player.sendMessage("Hey, are you ready for being all the generous?");
		}

	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {

		LivingEntity justDied = e.getEntity();
		Player killer = null;

		if (justDied.getKiller() instanceof Player) {
			killer = justDied.getKiller();
			String statName = "";

			if (justDied instanceof Zombie) {
				killer.sendMessage("The zombie, he is dead. The monies shall flow.");
				statName = "zombieKills";

				String rewardInfo = addStatAndCheckReward(killer, statName, "zombie", 100, 1, 100, 26, 100);

				killer.sendMessage("Braaaainnns! " + rewardInfo + " Keep it up survivor!");
			}

			if (justDied instanceof Cow) {
				killer.sendMessage("The cow, it is vanquished");
				statName = "cowKills";

				String rewardInfo = addStatAndCheckReward(killer, statName, "cow", 50, 1, 50, 51, 75);

				killer.sendMessage("Toro! Toro! " + rewardInfo + " Keep on slaying (the cows)!");
			}
		}
	}

	public String addStatAndCheckReward(Player player, String statName, String statElement, 
			int rewardCap, int increment, int currencyBase, int currencyRandom, int pointIncrement) {
		String playerName = player.getName();
		try {
			int currentStat = db.getPlayerStat(playerName, statName);
			boolean statAdd = db.setPlayerStat(playerName, statName, Integer.toString(currentStat+increment));

			if (statAdd) {
				if ((currentStat + increment) % rewardCap == 0) {
					boolean charityAdd = db.incrementPlayerStat(playerName, "CharityPoints", pointIncrement);
					int moniesIncrement = (int)(Math.random()*currencyRandom)+currencyBase;
					boolean moniesAdd = db.incrementPlayerStat(playerName, "Currency", moniesIncrement);

					return "You just got " + moniesIncrement + " monies and " + pointIncrement + " charity points.";
				} else {
					return "You still have " + (rewardCap - (currentStat + increment)%rewardCap) + " " + statElement + "s to go.";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}

//    @EventHandler
//      public void onJoin(PlayerJoinEvent event) {
//          // Create the task and schedule to run it once, after 20 ticks
//          BukkitTask task = new CharityTask(this.plugin).runTaskLater(this.plugin, 20);
//      }

//   https://github.com/RathelmMC/MC-Jobs/blob/master/mcJobs/src/com/dmgkz/mcjobs/McJobs.java