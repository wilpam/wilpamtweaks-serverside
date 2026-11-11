package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.content.ModBlocks;
import wilpam.tweaks.content.ModItems;
import wilpam.tweaks.recipe.LootCookingRecipe.LootResult;
import wilpam.tweaks.recipe.StampRecipe;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries,
                                                            @NonNull BootstrapContext<Recipe<?>> recipes,
                                                            @NonNull BootstrapContext<Advancement> advancements) {
        return new Recipes(recipes, advancements);
    }

    public static void bootstrap(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        new Recipes(recipes, advancements).buildRecipes();
    }

    private static final class Recipes extends RecipeProvider {
        private Recipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
            super(recipes, advancements);
        }

        @Override
        public void buildRecipes() {
            var items = output.lookup(Registries.ITEM);

            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.DOUGH.item())
                    .requires(Items.WHEAT, 2)
                    .unlockedBy("has_wheat", has(Items.WHEAT))
                    .save(output, ModItems.ID + ":dough_from_wheat");

            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.FLINT_BLOCK_ITEM)
                    .requires(Items.FLINT, 9)
                    .unlockedBy("has_flint", has(Items.FLINT))
                    .save(output);

            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SLIMEBALL_SUBSTRATE.item())
                    .requires(ModItems.DOUGH.item())
                    .requires(ItemTags.LEAVES)
                    .requires(ItemTags.LEAVES)
                    .requires(Items.WATER_BUCKET)
                    .unlockedBy("has_dough", has(ModItems.DOUGH.item()))
                    .save(output, ModItems.ID + ":slimeball_substrate_leaves");

            LootCookingRecipeBuilder.smelting(
                            Ingredient.of(ModItems.OIL.item()), RecipeCategory.MISC, CookingBookCategory.MISC,
                            java.util.List.of(
                                    LootResult.of(ModItems.OIL.item(), 10),
                                    LootResult.of(ModItems.OIL_WAX.item(), 5),
                                    LootResult.of(ModItems.PREPOLYMERIZED_PLATE.item(), 2),
                                    LootResult.of(Items.AIR, 1)
                            ),
                            0.2f, 200)
                    .resultName("recipe.wilpam_tweaks.crude_oil_distillation")
                    .unlockedBy("has_oil", has(ModItems.OIL.item()))
                    .save(output, ModItems.ID + ":crude_oil_distillation");

            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ModItems.SLIMEBALL_SUBSTRATE.item())
                    .requires(ModItems.DOUGH.item())
                    .requires(Items.DYE.lime())
                    .requires(Items.WATER_BUCKET)
                    .unlockedBy("has_dough", has(ModItems.DOUGH.item()))
                    .save(output, ModItems.ID + ":slimeball_substrate_dye");

            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(ModItems.DOUGH.item()), RecipeCategory.FOOD, CookingBookCategory.FOOD,
                            Items.BREAD, 0.25f, 200)
                    .unlockedBy("has_dough", has(ModItems.DOUGH.item()))
                    .save(output, ModItems.ID + ":cook_dough");

            SimpleCookingRecipeBuilder.smoking(
                            Ingredient.of(ModItems.DOUGH.item()), RecipeCategory.FOOD,
                            Items.BREAD, 0.25f, 100)
                    .unlockedBy("has_dough", has(ModItems.DOUGH.item()))
                    .save(output, ModItems.ID + ":smoke_dough");

            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(ModItems.SLIMEBALL_SUBSTRATE.item()), RecipeCategory.MISC, CookingBookCategory.MISC,
                            Items.SLIME_BALL, 2f, 800)
                    .unlockedBy("has_slimeball_substrate", has(ModItems.SLIMEBALL_SUBSTRATE.item()))
                    .save(output, ModItems.ID + ":cook_slimeball");

            SimpleCookingRecipeBuilder.smoking(
                            Ingredient.of(Items.MILK_BUCKET), RecipeCategory.FOOD,
                            ModItems.CHEESE.item(), 0.25f, 100)
                    .unlockedBy("has_milk", has(Items.MILK_BUCKET))
                    .save(output, ModItems.ID + ":smoke_milk");

            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(Items.MILK_BUCKET), RecipeCategory.FOOD, CookingBookCategory.FOOD,
                            ModItems.CHEESE.item(), 0.25f, 200)
                    .unlockedBy("has_milk", has(Items.MILK_BUCKET))
                    .save(output, ModItems.ID + ":cook_milk");

            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(Items.COAL), RecipeCategory.MISC, CookingBookCategory.MISC,
                            ModItems.OIL.item(), 1f, 200)
                    .unlockedBy("has_coal", has(Items.COAL))
                    .save(output, ModItems.ID + ":cook_coal");

            SimpleCookingRecipeBuilder.smelting(
                            Ingredient.of(ModItems.PREPOLYMERIZED_PLATE.item()), RecipeCategory.MISC, CookingBookCategory.MISC,
                            ModItems.PLASTIC_PLATE.item(), 1f, 200)
                    .unlockedBy("has_prepolymerized_plate", has(ModItems.PREPOLYMERIZED_PLATE.item()))
                    .save(output, ModItems.ID + ":cook_prepolymerized_plate");

            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PLASTIC_BLOCK_ITEM, 16)
                    .pattern(" a ")
                    .pattern("a a")
                    .pattern(" a ")
                    .define('a', ModItems.PLASTIC_PLATE.item())
                    .unlockedBy("has_plastic", has(ModItems.PLASTIC_PLATE.item()))
                    .save(output, ModItems.ID + ":plastic_plate");

            ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.LEATHER, 3)
                    .pattern("aaa")
                    .pattern("bbb")
                    .pattern("aaa")
                    .define('a', Items.PAPER)
                    .define('b', ModItems.OIL_WAX.item())
                    .unlockedBy("has_paper", has(Items.PAPER))
                    .save(output, ModItems.ID + ":synthetic_leather");

            ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, ModItems.STAMP.item())
                    .pattern("aa")
                    .pattern("bb")
                    .define('a', Items.DYE.red())
                    .define('b', ItemTags.PLANKS)
                    .unlockedBy("has_red_dye", has(Items.DYE.red()))
                    .save(output);

            SpecialRecipeBuilder.special(() -> new StampRecipe(Ingredient.of(ModItems.STAMP.item())))
                    .save(output, ModItems.ID + ":stamping");

        }
    }

    @Override
    public @NonNull String getName() {
        return "Mod Recipes";
    }
}
