package marvtechnology.clearmarv.task;

import marvtechnology.clearmarv.config.ConfigManager;
import marvtechnology.clearmarv.util.ItemCheckUtil;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityClearer {

    public static int clearEntitiesInWorld(World world) {
        int removed = 0;

        for (Entity entity : world.getEntities()) {
            if (shouldRemove(entity)) {
                entity.remove();
                removed++;
            }
        }

        return removed;
    }

    private static boolean shouldRemove(Entity entity) {
        if (entity instanceof Player) return false;

        // 除外エンティティタイプに含まれていれば削除しない
        if (ConfigManager.getExcludedEntityTypes().contains(entity.getType().getKey().toString())) {
            return false;
        }

        // 落ちているアイテム
        if (entity instanceof Item) {
            Item item = (Item) entity;

            // 設置アイテムやトリック的なものを無視（例: setPickupDelay(-1) は除外）
            if (item.getPickupDelay() < 0) return false;

            ItemStack stack = item.getItemStack();
            return !ItemCheckUtil.isExcludedItem(stack);
        }

        // その他（ゾンビなど）は削除対象
        return true;
    }
}
