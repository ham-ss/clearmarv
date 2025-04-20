package marvtechnology.clearmarv.commands;

import marvtechnology.clearmarv.Clearmarv;
import marvtechnology.clearmarv.config.ConfigManager;
import marvtechnology.clearmarv.lang.MessageManager;
import marvtechnology.clearmarv.task.ClearTaskScheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClearmarvCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUB_COMMANDS = Arrays.asList("start", "stop", "reload", "test");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("clearmarv.admin")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§6/clearmarv <start|stop|reload|test>");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("start")) {
            ClearTaskScheduler.start(Clearmarv.getInstance());
            sender.sendMessage("§aClearmarv task started.");
        } else if (sub.equals("stop")) {
            ClearTaskScheduler.stop();
            sender.sendMessage("§cClearmarv task stopped.");
        } else if (sub.equals("reload")) {
            Clearmarv plugin = Clearmarv.getInstance();
            plugin.reloadConfig();
            ConfigManager.load(plugin);
            MessageManager.load(plugin);
            sender.sendMessage("§bClearmarv config reloaded.");
        } else if (sub.equals("test")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(MessageManager.get(player, "countdown.warning",
                        Collections.singletonMap("time", "20")));
                player.sendMessage(MessageManager.get(player, "countdown.done",
                        Collections.singletonMap("count", "5")));
            } else {
                sender.sendMessage("§cThis command is for players only.");
            }
        } else {
            sender.sendMessage("§6/clearmarv <start|stop|reload|test>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> results = new ArrayList<String>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String sub : SUB_COMMANDS) {
                if (sub.startsWith(input)) {
                    results.add(sub);
                }
            }
        }

        return results;
    }
}
