package wilpam.tweaks.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-item smelting speed multipliers, consumed by {@code AbstractFurnaceBlockEntityMixin}.
 */
public final class ModFuelSpeeds {
    private static final Map<Item, Double> SPEEDS = new HashMap<>();

    private ModFuelSpeeds() {
    }

    /**
     * Registers a speed multiplier for a fuel. {@code speed > 1} smelts faster,
     * {@code 0 < speed < 1} smelts slower.
     */
    public static void register(Item fuel, double speed) {
        SPEEDS.put(fuel, speed);
    }

    public static double speedOf(ItemStack fuel) {
        return fuel.isEmpty() ? 1.0 : SPEEDS.getOrDefault(fuel.getItem(), 1.0);
    }
}
