package marvtechnology.clearmarv.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import marvtechnology.clearmarv.Clearmarv;
import marvtechnology.clearmarv.config.ConfigManager;
import marvtechnology.clearmarv.lang.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClearTaskScheduler {

    private static final Map<Integer, ScheduledTask> warningTasks = new HashMap<>();
    private static ScheduledTask clearTask = null;

    public static void start(final Clearmarv plugin) {
        stop();

        final int interval = ConfigManager.getClearInterval();
        List<Integer> warnings = ConfigManager.getWarningTimes();

        for (final int warnBefore : warnings) {
            int delay = interval - warnBefore;
            if (delay < 0) continue;

            ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(
                    plugin,
                    t -> broadcastWarning(warnBefore),
                    delay,
                    interval,
                    TimeUnit.SECONDS
            );

            warningTasks.put(warnBefore, task);
        }

        clearTask = Bukkit.getAsyncScheduler().runAtFixedRate(
                plugin,
                t -> runClearTask(),
                interval,
                interval,
                TimeUnit.SECONDS
        );
    }

    public static void stop() {
        for (ScheduledTask task : warningTasks.values()) {
            if (task != null) task.cancel();
        }
        warningTasks.clear();

        if (clearTask != null) {
            clearTask.cancel();
            clearTask = null;
        }
    }

    private static void broadcastWarning(int secondsLeft) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(secondsLeft));
            player.sendMessage(MessageManager.get(player, "countdown.warning", placeholders));
        }
    }

    private static void runClearTask() {
        Bukkit.getGlobalRegionScheduler().execute(Clearmarv.getInstance(), () -> {
            int removedTotal = 0;

            for (World world : Bukkit.getWorlds()) {
                removedTotal += EntityClearer.clearEntitiesInWorld(world);
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("count", String.valueOf(removedTotal));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(MessageManager.get(player, "countdown.done", placeholders));
            }
        });
    }
}
