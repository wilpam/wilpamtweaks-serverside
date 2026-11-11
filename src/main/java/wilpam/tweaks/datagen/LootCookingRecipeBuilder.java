package wilpam.tweaks.datagen;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.WilpamTweaks;
import wilpam.tweaks.recipe.LootCookingRecipe;
import wilpam.tweaks.recipe.LootCookingRecipe.LootResult;

import java.util.List;

/**
 * Builder for a {@link LootCookingRecipe}, mirroring {@code SimpleCookingRecipeBuilder}
 * with weighted results. {@link ModRandomLootTableProvider} generates the matching
 * loot table from those results, using {@code cooking/<recipe path>} as its ID.
 */
public class LootCookingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory craftingCategory;
    private final CookingBookCategory cookingBookCategory;
    private final Ingredient ingredient;
    private final List<LootResult> results;
    private final float experience;
    private final int cookingTime;
    private final RecipeType<? extends AbstractCookingRecipe> type;
    private final RecipeUnlockAdvancementBuilder advancement = new RecipeUnlockAdvancementBuilder();
    private String group = "";
    private String resultName = LootCookingRecipe.DEFAULT_RESULT_NAME_TRANSLATION_KEY;

    private LootCookingRecipeBuilder(RecipeCategory craftingCategory, CookingBookCategory cookingBookCategory,
                                     Ingredient ingredient, List<LootResult> results,
                                     float experience, int cookingTime, RecipeType<? extends AbstractCookingRecipe> type) {
        this.craftingCategory = craftingCategory;
        this.cookingBookCategory = cookingBookCategory;
        this.ingredient = ingredient;
        if (results.isEmpty() || results.stream().anyMatch(result -> result.weight() <= 0)) {
            throw new IllegalArgumentException("Loot cooking results must be nonempty and have positive weights");
        }
        this.results = List.copyOf(results);
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.type = type;
    }

    @SuppressWarnings("unused")
    public static LootCookingRecipeBuilder smelting(Ingredient ingredient, RecipeCategory category,
                                                    CookingBookCategory cookingCategory,
                                                    List<LootResult> results, float experience, int cookingTime) {
        return new LootCookingRecipeBuilder(category, cookingCategory, ingredient, results, experience, cookingTime, RecipeType.SMELTING);
    }

    @SuppressWarnings("unused")
    public static LootCookingRecipeBuilder smoking(Ingredient ingredient, RecipeCategory category,
                                                   List<LootResult> results,
                                                   float experience, int cookingTime) {
        return new LootCookingRecipeBuilder(category, CookingBookCategory.FOOD, ingredient, results, experience, cookingTime, RecipeType.SMOKING);
    }

    @SuppressWarnings("unused")
    public static LootCookingRecipeBuilder blasting(Ingredient ingredient, RecipeCategory category,
                                                    CookingBookCategory cookingCategory,
                                                    List<LootResult> results, float experience, int cookingTime) {
        return new LootCookingRecipeBuilder(category, cookingCategory, ingredient, results, experience, cookingTime, RecipeType.BLASTING);
    }

    @Override
    public @NonNull LootCookingRecipeBuilder unlockedBy(@NonNull String name, @NonNull Criterion<?> criterion) {
        advancement.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public @NonNull LootCookingRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public @NonNull LootCookingRecipeBuilder resultName(String resultName) {
        this.resultName = resultName;
        return this;
    }

    @Override
    public @NonNull ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "loot_cooking"));
    }

    @Override
    public void save(@NonNull RecipeOutput output, @NonNull ResourceKey<Recipe<?>> id) {
        Recipe.CommonInfo commonInfo = RecipeBuilder.createCraftingCommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(cookingBookCategory, group);
        ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE,
                id.identifier().withPrefix("cooking/"));
        LootCookingRecipe recipe = new LootCookingRecipe(commonInfo, bookInfo, ingredient, lootTable, results, resultName, experience, cookingTime, type);
        AdvancementHolder advancementHolder = advancement.build(output, id, craftingCategory);
        output.accept(id, recipe, advancementHolder);
    }
}
