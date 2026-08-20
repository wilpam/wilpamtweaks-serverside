package wilpam.tweaks.content;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import wilpam.tweaks.WilpamTweaks;
import wilpam.tweaks.recipe.StampRecipe;

public class ModCustomRecipes {
    public static final RecipeSerializer<StampRecipe> STAMP_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "crafting_special_stamping"),
            new RecipeSerializer<>(StampRecipe.MAP_CODEC, StampRecipe.STREAM_CODEC)
    );

    public static final RecipeType<StampRecipe> STAMP_RECIPE_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "crafting_special_stamping"),
            new RecipeType<StampRecipe>() { }
    );

    public static void initialize() {}
}
