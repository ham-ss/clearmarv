package marvtechnology.clearmarv.config;

import marvtechnology.clearmarv.Clearmarv;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigManager {

    private static int clearInterval;
    private static List<Integer> warningTimes;
    private static Set<String> excludedEntityTypes;
    private static List<ItemExclusion> excludedItems;

    public static void load(Clearmarv plugin) {
        FileConfiguration config = plugin.getConfig();

        clearInterval = config.getInt("clear-interval", 600);
        warningTimes = config.getIntegerList("warning-times");
        excludedEntityTypes = new HashSet<>(config.getStringList("excluded-entities"));

        excludedItems = new ArrayList<>();
        List<Map<?, ?>> rawItemList = config.getMapList("excluded-items");

        for (Map<?, ?> entry : rawItemList) {
            Material material = null;
            Integer customModelData = null;

            if (entry.containsKey("material")) {
                material = Material.matchMaterial(entry.get("material").toString());
            }

            if (entry.containsKey("custom_model_data")) {
                customModelData = (Integer) entry.get("custom_model_data");
            }

            if (material != null || customModelData != null) {
                excludedItems.add(new ItemExclusion(material, customModelData));
            }
        }
    }

    public static int getClearInterval() {
        return clearInterval;
    }

    public static List<Integer> getWarningTimes() {
        return warningTimes;
    }

    public static Set<String> getExcludedEntityTypes() {
        return excludedEntityTypes;
    }

    public static List<ItemExclusion> getExcludedItems() {
        return excludedItems;
    }


    public static class ItemExclusion {
        private final Material material;
        private final Integer customModelData;

        public ItemExclusion(Material material, Integer customModelData) {
            this.material = material;
            this.customModelData = customModelData;
        }

        public Material getMaterial() {
            return material;
        }

        public Integer getCustomModelData() {
            return customModelData;
        }
    }
}
