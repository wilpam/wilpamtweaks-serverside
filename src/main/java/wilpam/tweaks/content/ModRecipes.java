package wilpam.tweaks.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;

/**
 * Per-recipe runtime flags. Default for every recipe is {@code false}.
 */
public final class ModRecipes {
    public static final String ID = "wilpam_tweaks";

    /**
     * Cooking/smelting recipes that should return the ingredient's crafting
     * remainder (e.g. the empty bucket when smelting a milk bucket).
     */
    public static final Set<ResourceKey<Recipe<?>>> RETURNS_REMAINDER = Set.of(
            key("cook_milk"),
            key("smoke_milk")
    );

    private ModRecipes() {
    }

    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ID, path));
    }
}