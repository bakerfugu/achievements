package com.hosthorde.baker.achievements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

public class Databiased {

	private final String ECON = "achievements-economy";
	private final String CPT = "achievements-cp-transactions";
	private final String CHT = "achievements-charities";
	private final String IGC = "achievements-igc-transactions";

	static Connection connection;

	public Databiased(String username, String password, String url) {

		boolean getDriver = false;
		boolean openConnection = false;

		try { //We use a try catch to avoid errors, hopefully we don't get any.
			Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
			getDriver = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try { //Another try catch to get any SQL errors (for example connections errors)
			connection = DriverManager.getConnection(url,username,password);
			//with the method getConnection() from DriverManager, we're trying to set
			//the connection's url, username, password to the variables we made earlier and
			//trying to get a connection at the same time. JDBC allows us to do this.
			openConnection = true;
		} catch (SQLException e) { //catching errors)
			e.printStackTrace(); //prints out SQLException errors to the console (if any)
		}

	}

	public void killConnection() {
		try { //using a try catch to catch connection errors (like wrong sql password...)
			if(connection!=null && connection.isClosed()){ //checking if connection isn't null to
				//avoid recieving a nullpointer
				connection.close(); //closing the connection field variable.
			}
		}catch(Exception e){
			e.printStackTrace();

		}
	}

	public int getStat(String tableName, String columnForRowID, String rowID, String columnName) {
		try {
			//player.sendMessage("getStat method start");
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT `" + columnName + "` FROM `" + tableName + "` WHERE " + columnForRowID + " = '" + rowID + "';");
			res.next();

			int value = res.getInt(columnName);

			//player.sendMessage("getStat method end");
			return value;

		} catch (SQLException e) {
			//m.getLogger.info("Could not get. Probably sql but not of certain");
			e.printStackTrace();
			return 0;
		}
	}

	public boolean setStat(String tableName, String columnForRowID, String rowID, String columnName, String newValue) {
		try {
			//player.sendMessage("getStat method start");
			Statement statement = connection.createStatement();
			statement.executeUpdate("UPDATE `" + tableName + "` SET `" + columnName + "` =  '" + newValue + "' WHERE " + columnForRowID + " = '" + rowID + "';");

			//player.sendMessage("getStat method end");
			return true;

		} catch (SQLException e) {
			//m.getLogger.info("Could not get. Probably sql but not of certain");
			e.printStackTrace();
			return false;
		}
	}

	//   getStat

	//   getCharityStat
	//     getStat('table1', blah)

	//   getPlayerStat
	//     getStat('table2', blah)

	public int getPlayerStat(String player, String columnName) {
		//player.sendMessage("getStat method start");

		int value = getStat(ECON, "PlayerName", player, columnName);

		//player.sendMessage("getStat method end");
		return value;
	}

	public boolean setPlayerStat(String player, String columnName, String newStat) {

		if (columnName != null && newStat != null) {
			boolean success = setStat(ECON, "PlayerName", player, columnName, newStat);

			return success;
		}
		return false; 
	}

	public boolean incrementPlayerStat(String player, String columnName, int increment) {

		if (columnName != null && increment != 0) {
			try {
				//player.sendMessage("start try in incrementPlayerStat");
				int base = getPlayerStat(player, columnName);
				boolean success = setPlayerStat(player, columnName, "" + (base + increment));
				//player.sendMessage("end try in incrementPlayerStat");
				return success;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false; 
	}

	public boolean setGlobally(String columnName, String newStat) {

		if (columnName != null && newStat != null) {
			try {
				Statement statement = connection.createStatement();
				boolean result = statement.execute("UPDATE `achievements-economy` SET `" + columnName + "` =" + newStat + ";");

				return true;      
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public String[] getCharityNames() {

		String[] charities = null;
		try {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT count(*) FROM `achievements-charities` WHERE `inUse` = 1");
			res.next();
			charities = new String[res.getInt("count(*)")];
			try {
				statement = connection.createStatement();
				res = statement.executeQuery("SELECT `charityName` FROM `achievements-charities` WHERE `inUse` = 1");
				for (int i=0; i<charities.length; i++) {
					res.next();
					charities[i] = res.getString("charityName");
				}
			} catch (SQLException e) {

			}
		} catch (SQLException e) {

		}
		return charities;
	}


	public int getCharityStat(String charityName, String columnName) {
		int value = getStat(CHT, "charityName", charityName, columnName);

		return value;
	}
	
	public boolean setCharityStat(String charityName, String columnName, String newValue) {
		boolean result = setStat(CHT, "charityName", charityName, columnName, newValue);

		return result;
	}
	
	public boolean incrementCharityStat(String charityName, String columnName, int increment) {
		int base = getCharityStat(charityName, columnName);
		boolean result = setCharityStat(charityName, columnName, "" + (base + increment));

		return result;
	}
	
	public boolean logCharityTransaction(String playerName, String amount, String charityName) {
		try {
			Statement statement = connection.createStatement();
			boolean result = statement.execute("INSERT INTO `6216`.`achievements-cp-transactions` " + 
			"(`playerName`, `amount`, `charityName`, `dateTime`) VALUES ('" + playerName + "', '" + amount + "', '" + charityName + "', NOW());");

			return result;      
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}