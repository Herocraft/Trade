package me.Josvth.Trade.Handlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Josvth.Trade.Trade;

public class YamlHandler {
	
	Trade plugin;
	
	File configFile;
	public FileConfiguration configYaml;
	
	File messagesFile;
	public FileConfiguration messagesYaml;
	
	public YamlHandler(Trade instance){
		plugin = instance;
	}
	
	public void setupYamls(){
		configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!(configFile.exists())) plugin.saveResource("config.yml", false);
		
		messagesFile = new File(plugin.getDataFolder(), "config.yml");
		if (!(messagesFile.exists())) plugin.saveResource("messages.yml", false);
	}
	
	public void loadYamls(){
		loadConfig();
		loadMessages();
	}
	
	public void saveYamls(){
		saveConfig();
		saveMessages();
	}
	
	public void loadConfig() {
		configYaml = new YamlConfiguration();
		try {
			configYaml.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			configYaml.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadMessages() {
		messagesYaml = new YamlConfiguration();
		try {
			messagesYaml.load(messagesFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveMessages() {
		try {
			messagesYaml.save(messagesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
