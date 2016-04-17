package com.hosthorde.baker.achievements;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

public class achievements extends JavaPlugin {

	private Economy economy;
	public Date scheduledDate;
	ResetPromptTask task;

	FileConfiguration config;
	FileConfiguration sql = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "SQLauth.yml")); //This is the custom config
	File SQLfile;

	// add instance variable for connection
	private Databiased db;

	public Databiased getConnection() {
		return db;
	}

	@Override
	public void onEnable() {

		//createConfig();
		//getLogger().info("just tried to call method let's see if it works");

		getLogger().info("onEnable has been invoked!");
		getLogger().info("Testing achievements for enable!");

		//initiate connection birthing process

		//PluginFile SQLauth = new PluginFile(this, "sqlAuth.yml", "sqlAuth.yml");
		getLogger().info("SQLretriever. <-- New breed of dog");

		this.SQLfile = new File(this.getDataFolder(), "sqlAuth.yml");
		this.saveConfig();
		saveData();

		try {
			String username = sql.getString("SQLusername");
			String password = sql.getString("SQLpassword");
			String ip = sql.getString("SQLip");

			getLogger().info(username + " : " + password + " : " + ip);
			db = new Databiased(username, password, ip);
		} catch (Exception e) {
			getLogger().info("Error creating SQL server. You probably forgot the sqlAuth.yml file. It needs:\n\tusername: \n\tpassword: \n\tip: \nas fields.");  
		}
		new EventListener (this);

		//triggerPrompt()

	}

	public void triggerPrompt() {

		task = new ResetPromptTask(this, db);
		task.runTaskTimer(this, 100L, 400L);

	}

	public void stopPrompt() {
		task.endTask();
	}

	//	public void save(){
	//		try {
	//			SQLfile.save(ConfigFile);
	//		} catch (Exception ex) {
	//
	//		}
	//	}

	public void saveData(){
		sql = YamlConfiguration.loadConfiguration(SQLfile);
		try {
			sql.save(SQLfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
		}
	}

	private boolean donate(int pointsToDonate, Player player, String charityName) {
		String playerName = player.getName();
		if (pointsToDonate > 0 && pointsToDonate <= db.getPlayerStat(playerName, "charityPoints")) {
			boolean takeOutPoints = db.incrementPlayerStat(playerName, "charityPoints", -1*pointsToDonate);
			if (takeOutPoints) {
				boolean donate = db.incrementCharityStat(charityName, "amountDonated", pointsToDonate);
				if (donate) {
					int getReward = db.getPlayerStat(playerName, "needsCharityPrompt");
					player.sendMessage("Thank you for donating " + pointsToDonate + 
							" points to " + charityName + "!");
					if (getReward == 1) {
						boolean rewardGiven = db.incrementPlayerStat(playerName, "Currency", 50);
						if (rewardGiven) {
							player.sendMessage("Hey, it's you first donation for a little while so we gave you 50 monies!");
							db.setPlayerStat(playerName, "needsCharityPrompt", "0");
						}
					}
					boolean log = db.logCharityTransaction(playerName, "" + pointsToDonate, charityName);

					return log;
				} else {
					player.sendMessage("Whoops, could not donated, logging failure. Adminstrators are availible at this toll " +
							"free number: 1-800-NVR-MIND");
				}
			} else {
				player.sendMessage("Your arduous journey to donate points failed because of a third-party issue that we should not be blamed for");
			}
		}
		return false;
	}

	@Override
	public void onDisable() {
		// TODO Insert logic to be performed when the plugin is disabled
		getLogger().info("onDisable has been invoked!");

		// kill connections

		db.killConnection();

		// try { //using a try catch to catch connection errors (like wrong sql
		// password...)
		// if(connection!=null && connection.isClosed()){ //checking if
		// connection isn't null to
		// //avoid recieving a nullpointer
		// connection.close(); //closing the connection field variable.
		// }
		// }catch(Exception e){
		// e.printStackTrace();
		//
		// }
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("basic")) { // If the player
			// typed /basic then
			// do the
			// following...
			Player player = (Player) sender;
			Bukkit.getServer().broadcastMessage(player.getName() + " can't even.");
			player.sendMessage("ip: " + sql.getString("SQLip") + " other text help?");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("startTask")) {

			sender.sendMessage("You started the prompter task");
			triggerPrompt();
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("stopTask")) {

			sender.sendMessage("You canceled the prompter task");
			stopPrompt();
			return true;
		}


		if (cmd.getName().equalsIgnoreCase("donate")) {

			// /donate (help/list)
			// /donate <number>
			// /donate <number> <quantity>

			// check if number arg exists at all
			// if so, check if number exists as a valid integer
			//if so, check if it's within the range of chartyNamesArr
			// if so, retrieve that charity's name
			// AND DO THE BELOW
			// if NOT, error player.sendMessage("Must select valid charity")
			// if NOT, default to showing instruction to use donate

			// check if quantity arg exists at all
			// if so, check if it is a valid integer
			// if so, check if the player has that quantity of charitypoints
			// if so, donate that amount to the specified charity
			// if not, player.sendMessage("FOOL U MUST SPECIFY VALID QUANTITY TO DONATE")
			// if NOT, player.sendMessage("WOAH TOO MUCH U DONT HAVE IT LOL")
			// if NOT, then default to donating all

			Player player = (Player) sender;
			String playerName = player.getName();

			String charities = "";
			String[] charityNamesArr = db.getCharityNames();

			for (int i=0;i<charityNamesArr.length; i++) {
				charities += "    " + (i+1) + ": " + charityNamesArr[i] + "\n";
			}
			String help = "/donate <charityNumber OR help OR list> [quantity]\n" +
					"Charities you can donate to:\n" + charities;

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(help);
				} else {
					try {
						String charityName = charityNamesArr[Integer.parseInt(args[0])-1];

						if (args.length > 1) {
							try {
								int pointsToDonate = Integer.parseInt(args[1]);
								donate(pointsToDonate, player, charityName);
							} catch (Exception e) {
								sender.sendMessage("Whoops, make sure your formatting is ok and your numbers are within range:\n" + help);
							}
						} else {
							int pointsToDonate = db.getPlayerStat(playerName, "CharityPoints");
							donate(pointsToDonate, player, charityName);
							// donate all
						}
					} catch (Exception e) {
						sender.sendMessage("Whoops, make sure your formatting is ok and your numbers are within range:\n" + help);
					}
				}
				return true;
			}
		}




		if (cmd.getName().equalsIgnoreCase("superjump")) {

			Bukkit.getServer().broadcastMessage("HELLO MESSAGES 4 U! SUPERJUMP");
			this.getLogger().info("HEY SUPERJUMP");

			sender.sendMessage("You typed a jump command!");
			Player player = (Player) sender;

			Location location = player.getLocation();
			double yPos = location.getY();
			location.setY(yPos + 100);
			player.teleport(location);
			return true;
		} // If this has happened the function will return true.
		// If this hasn't happened the value of false will be returned.

		if (cmd.getName().equalsIgnoreCase("economy")) {
			economy = new Economy(100);
			sender.sendMessage("You have monies! " + economy.getQuantity()
					+ "! WOW!");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("testreward")) {
			int reward = 50;
			sender.sendMessage("You have monies! You had: "
					+ economy.getQuantity()
					+ " monies and you just got a reward of " + reward
					+ "! WOW!");
			economy.reward(reward);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("storageTest")) {
			Player player = (Player) sender;
			String playerName = player.getName();

			int monies = db.getPlayerStat(playerName, "Currency");

			sender.sendMessage(playerName
					+ " has many monies! Monies total at: " + monies);

			return true;
		}
		if (cmd.getName().equalsIgnoreCase("getStat")) {
			Player player = (Player) sender;

			String statToGet;
			String whichPlayer;

			try {
				statToGet = args[0];
				if (args.length > 1) {
					whichPlayer = args[1];
				} else {
					whichPlayer = player.getName();
				}
				try {
					int statResult = db.getPlayerStat(whichPlayer, statToGet);

					sender.sendMessage(whichPlayer + "'s " + statToGet
							+ " is equal to " + statResult + ", WOW!");
				} catch (Exception e) {
					sender.sendMessage(player.getName()
							+ " has failed in their trial to find stat: "
							+ statToGet + ".");
				}
			} catch (Exception e) {
				sender.sendMessage(player.getName() + " r u dumb? GIVE STAT");
			}

			return true;
		}

		if (cmd.getName().equalsIgnoreCase("resetWorld")) {
			Player player = (Player) sender;

			try {

				WorldManager CopyCat = new WorldManager();
				CopyCat.copyWorld(args[0], args[1]);
				sender.sendMessage(player.getName()
						+ " has copied the world. Hopefully. I am of hope that the target folder will be full of files");

			} catch (Exception e) {

				sender.sendMessage(player.getName() + " failed to doppelgang.");

			}

			return true;
		}
		if (cmd.getName().equalsIgnoreCase("setStat")) {
			Player player = (Player) sender;

			String statToSet = null;
			String newStatInt = null;
			String whichPlayer = player.getName();

			try {
				statToSet = args[0];
				newStatInt = args[1];
				if (args.length > 2) {
					whichPlayer = args[2];
				}
			} catch (Exception e) {
				sender.sendMessage(whichPlayer
						+ " r u dumb? Need stat and num!");
				// statToGet = "zombieKills";
				// newStat = db.getPlayerStat(playerNameName, statToGet);
			}

			try {
				boolean statResult = db.setPlayerStat(whichPlayer, statToSet,
						newStatInt);

				if (statResult) {
					sender.sendMessage(whichPlayer + "'s " + statToSet
							+ " is equal to "
							+ db.getPlayerStat(whichPlayer, statToSet) + ", WOW!");
				} else {
					sender.sendMessage("%#!@ having issuez, standbyplz. Setting of stat: "
							+ statToSet
							+ " is not of successful. But good news: We have puppies in the break room, check it out!");
				}

			} catch (Exception e) {
				sender.sendMessage(whichPlayer
						+ " trialed to change stat, DB bugged out, sry: "
						+ statToSet + ", but failed with miserableness.");
			}

			return true;
		}

		return false;

	}

	private void createConfig() {

		getLogger().info("Ok, method is called, try to create config now");

		try {
			getLogger().info("inside try catch");
			if (!getDataFolder().exists()) {
				getLogger().info("inside if statement with mkdirs");
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) {
				getLogger().info("Config.yml not found, creating!");
				saveDefaultConfig();
			} else {
				getLogger().info("Config.yml found, loading!");
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	// public String getConfigData() {

	// }

	// @EventHandler(priority=EventPriority.LOW)
	// public void onItemPickup(PlayerPickupItemEvent evt){
	// Player player = evt.getPlayer();
	// PlayerInventory inventory = player.getInventory();
	// ItemStack itemstack = new ItemStack(Material.DIRT, 20);
	// ItemStack newitem = new ItemStack(Material.DIAMOND, 20);
	// player.sendMessage("You picked up an item!");
	//
	// if (inventory.contains(itemstack)) {
	// inventory.addItem(newitem);
	// player.sendMessage("Yer a skilled digger, ain't cha! Have some diamonds");
	// }
	// }

}