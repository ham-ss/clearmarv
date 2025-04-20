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
        final List<World> worlds = Bukkit.getWorlds();
        final int totalWorlds = worlds.size();

        final int[] removedCount = {0};
        final int[] completed = {0};

        for (final World world : worlds) {
            Bukkit.getRegionScheduler().run(
                    Clearmarv.getInstance(),
                    world,
                    0, 0,
                    task -> {
                        int removed = EntityClearer.clearEntitiesInWorld(world);

                        // 合計集計と通知はメインスレッドで安全に
                        Bukkit.getGlobalRegionScheduler().run(Clearmarv.getInstance(), mainTask -> {
                            removedCount[0] += removed;
                            completed[0]++;

                            if (completed[0] == totalWorlds) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    Map<String, String> placeholders = new HashMap<>();
                                    placeholders.put("count", String.valueOf(removedCount[0]));
                                    player.sendMessage(MessageManager.get(player, "countdown.done", placeholders));
                                }
                            }
                        });
                    }
            );
        }
    }
}
