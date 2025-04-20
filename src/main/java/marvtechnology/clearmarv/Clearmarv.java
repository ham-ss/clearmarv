package marvtechnology.clearmarv;

import marvtechnology.clearmarv.commands.ClearmarvCommand;
import marvtechnology.clearmarv.config.ConfigManager;
import marvtechnology.clearmarv.lang.MessageManager;
import marvtechnology.clearmarv.task.ClearTaskScheduler;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Clearmarv extends JavaPlugin {

    private static Clearmarv instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        ConfigManager.load(this);
        MessageManager.load(this);

        PluginCommand command = getCommand("clearmarv");
        if (command != null) {
            ClearmarvCommand executor = new ClearmarvCommand();
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().warning("plugin.yml に 'clearmarv' コマンドが定義されていません！");
        }

        ClearTaskScheduler.start(this);

        getLogger().info("Clearmarv 有効化完了！");
    }

    @Override
    public void onDisable() {
        ClearTaskScheduler.stop();
        getLogger().info("Clearmarv 無効化完了。");
    }

    public static Clearmarv getInstance() {
        return instance;
    }
}
