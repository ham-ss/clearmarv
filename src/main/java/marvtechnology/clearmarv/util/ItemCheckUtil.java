package marvtechnology.clearmarv.util;

import marvtechnology.clearmarv.config.ConfigManager;
import marvtechnology.clearmarv.config.ConfigManager.ItemExclusion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemCheckUtil {

    public static boolean isExcludedItem(ItemStack stack) {
        if (stack == null) return false;

        Material material = stack.getType();
        int cmd = stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData()
                ? stack.getItemMeta().getCustomModelData()
                : -1;

        for (ItemExclusion exclusion : ConfigManager.getExcludedItems()) {
            boolean matchMaterial = exclusion.getMaterial() == null || exclusion.getMaterial() == material;
            boolean matchCmd = exclusion.getCustomModelData() == null || exclusion.getCustomModelData() == cmd;

            if (matchMaterial && matchCmd) {
                return true;
            }
        }

        return false;
    }
}
