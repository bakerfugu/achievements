package com.hosthorde.baker.achievements;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ConfigManager extends YamlConfiguration{
	private static String path;
	public static File configx;
	public static FileConfiguration config;
	private final achievements plugin;
	private static WatchService watcher;

	//# Constructor
//	public static void Main(Main pl) {
//		plugin = pl;
//		pl.getConfig();
//		pl.getConfig().options().copyDefaults(true);
//	}
	
	public ConfigManager(achievements m, String configName) {
		plugin = m;
		m.getConfig();
		m.getConfig().options().copyDefaults(true);
	}

	//# Get Config
	public String getStringConfig(String string){
		string = plugin.getConfig().getString(string);
		return string;
	}
	public boolean getBooleanConfig(String string){
		boolean bool = plugin.getConfig().getBoolean(string);
		return bool;
	}
	public int getIntegerConfig(String string){
		int Integer = plugin.getConfig().getInt(string);
		return Integer;
	}

	//# Set Config
	public void setConfig(String path, String key){
		if (key != null && path != null){
			plugin.getConfig().set(path, key);
			plugin.getConfig().options().copyDefaults(true);
			plugin.saveConfig();
			plugin.getLogger().info("Path: " + path);
			plugin.getLogger().info("Key: " + key);
			return;
		}
		plugin.getLogger().info("Something Went wrong");
	}

	//# Save Config
	public void saveConfig() {
		try{
			config.save(configx);
			plugin.getLogger().info("Config " + path.replace("plugins\\", "") + " saved.");
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	//#Load Custom Config
	public void loadConfig(String configPath, String configName) {
		activateConfig(configPath, configName);
		try{
			config.load(configx);
			//config.set("Path", "Test"); // If I add this it only adds it to the Latest not all of them :/
			plugin.getLogger().info(path.replace("plugins\\", "") + " loaded.");
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}catch (InvalidConfigurationException e){
			e.printStackTrace();
		}
	}

	//#Load Custom Config ??
	public void activateConfig(String configPath, String configName) {
		//# Ternary operations.
		//# if configName contains .yml add it.
		//# If configPath is empty path should only have \file.yml else folder\file.yml, ternary operation.
		configName += (!configName.contains(".yml")) ? ".yml" : "";
		path = (configPath == null) ? plugin.getDataFolder() + "\\" + configName : plugin.getDataFolder() + "\\" + configPath + "\\" + configName;
		configx = new File(path);
		config = YamlConfiguration.loadConfiguration(configx);
		config.set("Path", "Test");
	}
}

