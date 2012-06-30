package me.Josvth.Trade.Handlers;

import java.io.File;
import java.io.IOException;
import me.Josvth.Trade.Trade;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class YamlHandler
{
  Trade plugin;
  File configFile;
  public FileConfiguration configYaml;
  File languageFile;
  public FileConfiguration languageYaml;

  public YamlHandler(Trade instance)
  {
    plugin = instance;
    setupYamls();
    loadYamls();
  }

  public void setupYamls() {
    configFile = new File(plugin.getDataFolder(), "config.yml");
    if (!configFile.exists()) plugin.saveResource("config.yml", false);

    languageFile = new File(plugin.getDataFolder(), "language.yml");
    if (!languageFile.exists()) plugin.saveResource("language.yml", false); 
  }

  public void loadYamls()
  {
    loadConfig();
    loadLanguage();
  }

  public void saveYamls() {
    saveConfig();
    saveLanguage();
  }

  public void loadConfig() {
    configYaml = new YamlConfiguration();
    try {
      configYaml.load(configFile);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (configYaml.getString("config-version") != plugin.getDescription().getVersion())
    {
      if (configYaml.getString("config-version") == null)
      {
        if (!configYaml.contains("default-rows")) configYaml.set("default-rows", Integer.valueOf(4));

        if (configYaml.contains("right_click")) configYaml.set("right_click", null);
        if (configYaml.contains("shift_right_click")) configYaml.set("shift_right_click", null);

        if (!configYaml.contains("economy.enabled"))
        {
          configYaml.set("economy.enabled", Boolean.valueOf(configYaml.getBoolean("economy", false)));
          configYaml.set("economy.values.small", Integer.valueOf(1));
          configYaml.set("economy.values.medium", Integer.valueOf(10));
          configYaml.set("economy.values.big", Integer.valueOf(50));

          configYaml.set("economy.items.small.type", Integer.valueOf(371));
          configYaml.set("economy.items.small.amount", Integer.valueOf(1));
          configYaml.set("economy.items.medium.type", Integer.valueOf(266));
          configYaml.set("economy.items.medium.amount", Integer.valueOf(10));
          configYaml.set("economy.items.large.type", Integer.valueOf(41));
          configYaml.set("economy.items.large.amount", Integer.valueOf(50));
        }

      }

      configYaml.set("config-version", plugin.getDescription().getVersion());

      saveConfig();
    }
  }

  public void saveConfig() {
    try {
      configYaml.save(configFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadLanguage() {
    languageYaml = new YamlConfiguration();
    try {
      languageYaml.load(languageFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void saveLanguage() {
    try {
      languageYaml.save(languageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}