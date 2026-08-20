package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import wilpam.tweaks.content.ModBlocks;
import wilpam.tweaks.content.ModItems;
import wilpam.tweaks.content.ModRecipes;
import wilpam.tweaks.recipe.StampRecipe;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                var items = registries.lookupOrThrow(Registries.ITEM);

                ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.DOUGH)
                        .requires(Items.WHEAT, 2)
                        .unlockedBy("has_wheat", has(Items.WHEAT))
                        .save(output, "dough_from_wheat");

                ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLINT_BLOCK_ITEM)
                        .requires(Items.FLINT, 9)
                        .unlockedBy("has_flint", has(Items.FLINT))
                        .save(output, "flint_block");

                ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SLIMEBALL_SUBSTRATE)
                        .requires(ModItems.DOUGH)
                        .requires(ItemTags.LEAVES)
                        .requires(ItemTags.LEAVES)
                        .requires(Items.WATER_BUCKET)
                        .unlockedBy("has_dough", has(ModItems.DOUGH))
                        .save(output, "slimeball_substrate_leaves");

                ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SLIMEBALL_SUBSTRATE)
                        .requires(ModItems.DOUGH)
                        .requires(Items.DYE.lime())
                        .requires(Items.WATER_BUCKET)
                        .unlockedBy("has_dough", has(ModItems.DOUGH))
                        .save(output, "slimeball_substrate_dye");

                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(ModItems.DOUGH), RecipeCategory.FOOD, CookingBookCategory.FOOD,
                                Items.BREAD, 0.25f, 200)
                        .unlockedBy("has_dough", has(ModItems.DOUGH))
                        .save(output, "cook_dough");

                SimpleCookingRecipeBuilder.smoking(
                                Ingredient.of(ModItems.DOUGH), RecipeCategory.FOOD,
                                Items.BREAD, 0.25f, 100)
                        .unlockedBy("has_dough", has(ModItems.DOUGH))
                        .save(output, "smoke_dough");

                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(ModItems.SLIMEBALL_SUBSTRATE), RecipeCategory.MISC, CookingBookCategory.MISC,
                                Items.SLIME_BALL, 2f, 800)
                        .unlockedBy("has_slimeball_substrate", has(ModItems.SLIMEBALL_SUBSTRATE))
                        .save(output, "cook_slimeball");

                SimpleCookingRecipeBuilder.smoking(
                                Ingredient.of(Items.MILK_BUCKET), RecipeCategory.FOOD,
                                ModItems.CHEESE, 0.25f, 100)
                        .unlockedBy("has_milk", has(Items.MILK_BUCKET))
                        .save(output, "smoke_milk");

                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(Items.MILK_BUCKET), RecipeCategory.FOOD, CookingBookCategory.FOOD,
                                ModItems.CHEESE, 0.25f, 200)
                        .unlockedBy("has_milk", has(Items.MILK_BUCKET))
                        .save(output, "cook_milk");

                SimpleCookingRecipeBuilder.smelting(
                                Ingredient.of(Items.COAL), RecipeCategory.MISC, CookingBookCategory.MISC,
                                ModItems.OIL, 1f, 200)
                        .unlockedBy("has_coal", has(Items.COAL))
                        .save(output, "cook_coal");

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.LEATHER, 3)
                        .pattern("aaa")
                        .pattern("bbb")
                        .pattern("aaa")
                        .define('a', Items.PAPER)
                        .define('b', ModItems.OIL)
                        .unlockedBy("has_paper", has(Items.PAPER))
                        .save(output, "synthetic_leather");

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.STAMP)
                        .pattern("aa")
                        .pattern("bb")
                        .define('a', Items.DYE.red())
                        .define('b', ItemTags.PLANKS)
                        .unlockedBy("has_red_dye", has(Items.DYE.red()))
                        .save(output, "stamp");

                SpecialRecipeBuilder.special(() -> new StampRecipe(Ingredient.of(ModItems.STAMP)))
                        .save(output, "stamping");
            }
        };
    }

    @Override
    public String getName() {
        return "Mod Recipes";
    }
}
