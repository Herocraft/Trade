package me.Josvth.Trade.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import me.Josvth.Trade.Trade;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class CommandHandler
  implements CommandExecutor
{
  Trade plugin;
  LanguageHandler languageHandler;

  public CommandHandler(Trade instance)
  {
    plugin = instance;
    languageHandler = plugin.getLanguageHandler();
    plugin.getCommand("trade").setExecutor(this);
  }

  public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args)
  {
    if (args.length == 0) return false;

    if (args.length == 1)
    {
      if (args[0].equalsIgnoreCase("ignore"))
      {
        if (!sender.hasPermission("trade.ignore")) {
          languageHandler.sendMessage(sender, "command.no-permission");
          return true;
        }

        if (!(sender instanceof Player)) {
          languageHandler.sendMessage(sender, "command.only-player");
          return true;
        }

        Player player = (Player)sender;

        if (plugin.ignoring.contains(player)) {
          plugin.ignoring.remove(player);
          languageHandler.sendMessage(player, "request.ignoring.disabled");
        } else {
          plugin.ignoring.add(player);
          languageHandler.sendMessage(player, "request.ignoring.enabled");
        }
        return true;
      }

      if (args[0].equalsIgnoreCase("reload")) {
        if (!sender.hasPermission("trade.reload")) {
          languageHandler.sendMessage(sender, "command.no-permission");
        } else {
          plugin.yamlHandler.loadYamls();
          languageHandler.sendMessage(sender, "global.reload");
        }
        return true;
      }

      if (!(sender instanceof Player)) {
        languageHandler.sendMessage(sender, "command.only-player");
        return true;
      }

      if (!sender.hasPermission("trade.request.command")) {
        languageHandler.sendMessage(sender, "command.no-permission");
        return true;
      }

      Player player = (Player)sender;
      Player requested = plugin.getServer().getPlayer(args[0]);
      if (requested == null)
        languageHandler.sendMessage(player, "request.player-not-found", new LanguageHandler.MessageArgument("%playername%", args[0]));
      else {
        plugin.requestPlayer(player, requested);
      }

      return true;
    }

    if (args.length == 2)
    {
      if (args[0].equalsIgnoreCase("accept"))
      {
        if (!(sender instanceof Player)) {
          languageHandler.sendMessage(sender, "command.only-player");
          return true;
        }

        if (!sender.hasPermission("trade.accept-request.command")) {
          languageHandler.sendMessage(sender, "command.no-permission");
          return true;
        }

        Player player = (Player)sender;
        Player requester = plugin.getServer().getPlayer(args[1]);

        if (requester == null)
          languageHandler.sendMessage(player, "request.player-not-found", new LanguageHandler.MessageArgument("%playername%", args[0]));
        else if (plugin.pendingRequests.containsKey(requester))
          plugin.acceptRequest(player, requester);
        else {
          languageHandler.sendMessage(player, "request.player-not-requested");
        }

        return true;
      }

      if (args[0].equalsIgnoreCase("refuse"))
      {
        if (!(sender instanceof Player)) {
          languageHandler.sendMessage(sender, "command.only-player");
          return true;
        }

        if (!sender.hasPermission("trade.refuse-request.command")) {
          languageHandler.sendMessage(sender, "command.no-permission");
          return true;
        }

        Player player = (Player)sender;
        Player requester = plugin.getServer().getPlayer(args[1]);
        if (requester == null)
          languageHandler.sendMessage(player, "request.player-not-found", new LanguageHandler.MessageArgument("%player-not-found%", args[1]));
        else if (plugin.pendingRequests.containsKey(requester))
          plugin.refuseRequest(requester, player);
        else {
          languageHandler.sendMessage(player, "request.player-not-requested");
        }
        return true;
      }
    }

    return false;
  }
}