package me.Josvth.Trade.Handlers;

import me.Josvth.Trade.Trade;

import org.bukkit.configuration.file.FileConfiguration;

public class YamlHandler {

	public FileConfiguration languageYaml;
	public FileConfiguration config;
	
	Trade plugin;
	
	public YamlHandler(Trade instance){
		plugin = instance;
	}
	
	
}
