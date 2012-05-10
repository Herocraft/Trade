package me.Josvth.Trade.Handlers;

import java.io.File;
import java.io.IOException;

import me.Josvth.Trade.Trade;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlHandler {
	
	Trade plugin;
	
	File configFile;
	public FileConfiguration configYaml;
	
	File languageFile;
	public FileConfiguration languageYaml;
		
	public YamlHandler(Trade instance){
		plugin = instance;
	}
	
	public void setupYamls(){
		configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!(configFile.exists())) plugin.saveResource("config.yml", false);
		
		languageFile = new File(plugin.getDataFolder(), "config.yml");
		if (!(languageFile.exists())) plugin.saveResource("language.yml", false);
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
		languageYaml = new YamlConfiguration();
		try {
			languageYaml.load(languageFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveMessages() {
		try {
			languageYaml.save(languageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
