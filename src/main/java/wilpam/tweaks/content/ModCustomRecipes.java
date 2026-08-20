package wilpam.tweaks.content;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import wilpam.tweaks.WilpamTweaks;
import wilpam.tweaks.recipe.LootCookingRecipe;
import wilpam.tweaks.recipe.StampRecipe;

public class ModCustomRecipes {
    public static final RecipeSerializer<StampRecipe> STAMP_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "crafting_special_stamping"),
            new RecipeSerializer<>(StampRecipe.MAP_CODEC, StampRecipe.STREAM_CODEC)
    );

    @SuppressWarnings("unused")
    public static final RecipeType<StampRecipe> STAMP_RECIPE_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "crafting_special_stamping"),
            new RecipeType<StampRecipe>() { }
    );

    // Smelting/smoking/blasting recipes whose result is a random loot table roll.
    public static final RecipeSerializer<LootCookingRecipe> SMELTING_LOOT_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "smelting_loot"),
            new RecipeSerializer<>(LootCookingRecipe.SMELTING_MAP_CODEC, LootCookingRecipe.SMELTING_STREAM_CODEC)
    );

    public static final RecipeSerializer<LootCookingRecipe> SMOKING_LOOT_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "smoking_loot"),
            new RecipeSerializer<>(LootCookingRecipe.SMOKING_MAP_CODEC, LootCookingRecipe.SMOKING_STREAM_CODEC)
    );

    public static final RecipeSerializer<LootCookingRecipe> BLASTING_LOOT_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "blasting_loot"),
            new RecipeSerializer<>(LootCookingRecipe.BLASTING_MAP_CODEC, LootCookingRecipe.BLASTING_STREAM_CODEC)
    );

    public static void initialize() {}
}
