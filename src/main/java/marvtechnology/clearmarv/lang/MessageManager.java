package marvtechnology.clearmarv.lang;

import marvtechnology.clearmarv.Clearmarv;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class MessageManager {

    private static final Map<String, YamlConfiguration> messages = new HashMap<>();
    private static final String DEFAULT_LANG = "en";
    private static final List<String> SUPPORTED_LANGS = Arrays.asList("en", "ja", "zh", "ko", "de");

    public static void load(Clearmarv plugin) {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists() && !langDir.mkdirs()) {
            plugin.getLogger().warning("言語ディレクトリの作成に失敗しました: " + langDir.getPath());
        }

        for (String lang : SUPPORTED_LANGS) {
            File file = new File(langDir, "messages_" + lang + ".yml");
            if (!file.exists()) {
                plugin.saveResource("lang/messages_" + lang + ".yml", false);
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            messages.put(lang, config);
        }
    }

    public static Component get(Player player, String path, Map<String, String> placeholders) {
        String lang = getLangKey(player);
        YamlConfiguration config = messages.getOrDefault(lang, messages.get(DEFAULT_LANG));

        String raw = config.getString(path);
        if (raw == null) raw = messages.get(DEFAULT_LANG).getString(path);
        if (raw == null) return Component.text("[Missing message: " + path + "]");

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                raw = raw.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return Component.text(raw);
    }

    private static String getLangKey(Player player) {
        String locale = player.locale().getLanguage();
        return messages.containsKey(locale) ? locale : DEFAULT_LANG;
    }
}
