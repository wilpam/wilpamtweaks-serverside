package wilpam.tweaks.content;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Per-item smelting speed multipliers, consumed by {@code AbstractFurnaceBlockEntityMixin}.
 */
public final class ModFuelSpeeds {
    public static final DataComponentType<Double> FUEL_SPEED;

    static {
        FUEL_SPEED = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath("wilpam_tweaks", "fuel_speed"),
                DataComponentType.<Double>builder().persistent(Codec.DOUBLE).build());
        PolymerComponent.registerDataComponent(FUEL_SPEED);
    }

    private ModFuelSpeeds() {
    }

    public static double speedOf(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 1.0;
        }
        Double speed = fuel.get(FUEL_SPEED);
        return (speed == null) ? 1.0 : speed;
    }
}
