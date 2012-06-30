package me.Josvth.Trade.Handlers;

import java.io.File;
import me.Josvth.Trade.Trade;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageHandler
{
  Trade plugin;
  File languageFile;
  FileConfiguration languageYaml;

  public LanguageHandler(Trade instance)
  {
    plugin = instance;
    setupLanguageFile();
    loadLanguageYaml();
  }

  private void setupLanguageFile() {
    languageFile = new File(plugin.getDataFolder(), "language.yml");
    if (!languageFile.exists()) plugin.saveResource("language.yml", false); 
  }

  private void loadLanguageYaml()
  {
    languageYaml = new YamlConfiguration();
    try {
      languageYaml.load(languageFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(CommandSender reciever, String path) {
    String message = languageYaml.getString(path);
    if (message == null) return;
    message = ChatColor.translateAlternateColorCodes('&', message);
    reciever.sendMessage(message);
  }

  public void sendMessage(CommandSender reciever, Message message) {
    sendMessage(reciever, message.path, message.args);
  }

  public void sendMessage(CommandSender reciever, String path, MessageArgument argument) {
    String message = languageYaml.getString(path);
    if (message == null) return;
    message = ChatColor.translateAlternateColorCodes('&', message);
    message = message.replaceAll(argument.variable, argument.value);
    reciever.sendMessage(message);
  }

  public void sendMessage(CommandSender reciever, String path, MessageArgument[] args) {
    String message = languageYaml.getString(path);
    if (message == null) return;
    message = ChatColor.translateAlternateColorCodes('&', message);
    for (MessageArgument argument : args) {
      message = message.replaceAll(argument.variable, argument.value);
    }
    reciever.sendMessage(message);
  }

  public static class Message
  {
    public final String path;
    public final LanguageHandler.MessageArgument[] args;

    public Message(String path)
    {
      this.path = path;
      args = new LanguageHandler.MessageArgument[0];
    }

    public Message(String path, LanguageHandler.MessageArgument[] args) {
      this.path = path;
      this.args = args;
    }

    public Message(String path, LanguageHandler.MessageArgument arg) {
      this.path = path;
      args = new LanguageHandler.MessageArgument[] { arg };
    }
  }

  public static class MessageArgument
  {
    public final String variable;
    public final String value;

    public MessageArgument(String variable, String value)
    {
      this.variable = variable;
      this.value = value;
    }
  }
}