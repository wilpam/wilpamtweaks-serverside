package wilpam.tweaks.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.content.ModCustomRecipes;
import wilpam.tweaks.util.ServerHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A smelting/smoking/blasting recipe whose result is a random item rolled from a
 * loot table instead of a fixed item. "results" is a static list of the possible outcomes
 * with their relative weights, used for the recipe-book display [so it doesn't actually affect the results!
 */
public class LootCookingRecipe extends AbstractCookingRecipe {
    // The real result is rolled from a loot table in assemble(), but we need a placeholder for the super() call.
    private static final net.minecraft.world.item.ItemStackTemplate PLACEHOLDER_RESULT =
            new net.minecraft.world.item.ItemStackTemplate(Items.STICK);

    // Custom name shown on the recipe book's result preview, defined in the lang file.
    public static final String DEFAULT_RESULT_NAME_TRANSLATION_KEY = "recipe.wilpam_tweaks.random_cook_result";

private final RecipeType<? extends AbstractCookingRecipe> type;
    private final ResourceKey<LootTable> lootTable;
    private final List<LootResult> results;
    private final String resultTranslationKey;

    public LootCookingRecipe(Recipe.CommonInfo commonInfo, CookingBookInfo bookInfo, Ingredient ingredient,
                             ResourceKey<LootTable> lootTable, List<LootResult> results,
                             String resultTranslationKey,
                             float experience, int cookingTime, RecipeType<? extends AbstractCookingRecipe> type) {
        super(commonInfo, bookInfo, ingredient, PLACEHOLDER_RESULT, experience, cookingTime);
        this.type = type;
        this.lootTable = lootTable;
        this.results = results;
        this.resultTranslationKey = resultTranslationKey;
    }

    public ResourceKey<LootTable> lootTable() {
        return lootTable;
    }

    public List<LootResult> results() {
        return results;
    }

    public String resultTranslationKey() {
        return resultTranslationKey;
    }

    @Override
    public @NonNull RecipeSerializer<LootCookingRecipe> getSerializer() {
        if (type == RecipeType.SMELTING) {
            return ModCustomRecipes.SMELTING_LOOT_SERIALIZER;
        }
        if (type == RecipeType.SMOKING) {
            return ModCustomRecipes.SMOKING_LOOT_SERIALIZER;
        }
        return ModCustomRecipes.BLASTING_LOOT_SERIALIZER;
    }

    @Override
    public @NonNull RecipeType<? extends AbstractCookingRecipe> getType() {
        return type;
    }

    @Override
    protected @NonNull Item furnaceIcon() {
        if (type == RecipeType.SMOKING) {
            return Items.SMOKER;
        }
        if (type == RecipeType.BLASTING) {
            return Items.BLAST_FURNACE;
        }
        return Items.FURNACE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        if (type == RecipeType.SMOKING) {
            return RecipeBookCategories.SMOKER_FOOD;
        }
        if (type == RecipeType.BLASTING) {
            return switch (category()) {
                case BLOCKS -> RecipeBookCategories.BLAST_FURNACE_BLOCKS;
                case FOOD, MISC -> RecipeBookCategories.BLAST_FURNACE_MISC;
            };
        }
        return switch (category()) {
            case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
            case FOOD -> RecipeBookCategories.FURNACE_FOOD;
            case MISC -> RecipeBookCategories.FURNACE_MISC;
        };
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull SingleRecipeInput input) {
        // The furnace intercepts this for loot recipes and caches the roll on the
        // furnace itself (see AbstractFurnaceBlockEntityMixin), so a plain roll is
        // enough here.
        return rollResult();
    }

    /**
     * Rolls the loot table from a random seed.
     */
    public ItemStack rollResult() {
        return rollResult(RandomSource.create().nextLong());
    }

    /**
     * Rolls the loot table from a seed combined with this recipe's identity.
     */
    public ItemStack rollResult(long seed) {
        MinecraftServer server = ServerHolder.getServer();
        if (server == null) {
            return ItemStack.EMPTY;
        }
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        if (level == null) {
            return ItemStack.EMPTY;
        }
        LootTable table = server.reloadableRegistries().getLootTable(lootTable);
        if (table == LootTable.EMPTY) {
            return ItemStack.EMPTY;
        }

        // Mix the recipe into the seed so different recipes give different rolls
        // for the same furnace seed.
        long recipeHash = lootTable.identifier().toString().hashCode();
        long combined = seed * 0x9E3779B97F4A7C15L + recipeHash;
        LootParams params = new LootParams.Builder(level).create(table.getParamSet());
        var stacks = table.getRandomItems(params, RandomSource.create(combined));
        if (stacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = stacks.getFirst();
        result.setCount(Math.min(result.getCount(), result.getMaxStackSize()));
        return result;
    }

    @Override
    public @NonNull List<RecipeDisplay> display() {
        // Use the static "results" list so the
        // recipe book always has something to show.
        ItemStack representative = representativeStack(results, resultTranslationKey);

        SlotDisplay result = representative.isEmpty()
                ? SlotDisplay.Empty.INSTANCE
                : new SlotDisplay.ItemStackSlotDisplay(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(representative));
        return List.of(new FurnaceRecipeDisplay(input().display(), SlotDisplay.AnyFuel.INSTANCE, result,
                new SlotDisplay.ItemSlotDisplay(furnaceIcon()), cookingTime(), experience()));
    }

    private static ItemStack representativeStack(List<LootResult> results, String resultTranslationKey) {
        Item best = null;
        int bestWeight = Integer.MIN_VALUE;
        int totalWeight = 0;
        for (LootResult r : results) {
            totalWeight += r.weight();
            if (r.weight() > bestWeight) {
                bestWeight = r.weight();
                Item item = BuiltInRegistries.ITEM.getValue(r.item());
                if (item != Items.AIR) {
                    best = item;
                }
            }
        }
        if (best == null || totalWeight <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(best);
        stack.set(DataComponents.CUSTOM_NAME,
                Component.translatable(resultTranslationKey).setStyle(Style.EMPTY.withItalic(false)));

        List<Component> lines = new ArrayList<>();
        for (LootResult r : results) {
            Item item = BuiltInRegistries.ITEM.getValue(r.item());
            int count = r.weight();
            float percentage = count * 100f / totalWeight;
            Component name;
            if (item == null || item == Items.AIR) {
                name = Component.translatable("recipe.wilpam_tweaks.random_cook.nothing")
                        .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false));
            } else {
                name = Component.translatable(item.getDescriptionId())
                        .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false));
            }
            Component pct = Component.literal(String.format(" (%.1f%%)", percentage))
                    .withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withItalic(false));
            lines.add(name.copy().append(pct));
        }
        stack.set(DataComponents.LORE, new ItemLore(lines));
        return stack;
    }

    /**
     * A single potential result, so the item plus its weight. This is in
     * the recipe's JSON so we can use it for percentage results.
     */
    public record LootResult(ResourceKey<Item> item, int weight) {
        public static final Codec<LootResult> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceKey.codec(Registries.ITEM).fieldOf("item").forGetter(LootResult::item),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(LootResult::weight)
        ).apply(i, LootResult::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LootResult> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.ITEM), LootResult::item,
                ByteBufCodecs.INT, LootResult::weight,
                LootResult::new);

        public static LootResult of(ItemLike item, int weight) {
            return new LootResult(BuiltInRegistries.ITEM.getResourceKey(item.asItem()).orElseThrow(), weight);
        }
    }

    // === serializers (one per furnace type) ===

    public static final MapCodec<LootCookingRecipe> SMELTING_MAP_CODEC = mapCodec(RecipeType.SMELTING, 200);
    public static final StreamCodec<RegistryFriendlyByteBuf, LootCookingRecipe> SMELTING_STREAM_CODEC = streamCodec(RecipeType.SMELTING);

    public static final MapCodec<LootCookingRecipe> SMOKING_MAP_CODEC = mapCodec(RecipeType.SMOKING, 100);
    public static final StreamCodec<RegistryFriendlyByteBuf, LootCookingRecipe> SMOKING_STREAM_CODEC = streamCodec(RecipeType.SMOKING);

    public static final MapCodec<LootCookingRecipe> BLASTING_MAP_CODEC = mapCodec(RecipeType.BLASTING, 100);
    public static final StreamCodec<RegistryFriendlyByteBuf, LootCookingRecipe> BLASTING_STREAM_CODEC = streamCodec(RecipeType.BLASTING);

    private static MapCodec<LootCookingRecipe> mapCodec(RecipeType<? extends AbstractCookingRecipe> type, int defaultCookingTime) {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                        Recipe.CommonInfo.MAP_CODEC.forGetter(LootCookingRecipe::commonInfo),
                        CookingBookInfo.MAP_CODEC.forGetter(LootCookingRecipe::bookInfo),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(LootCookingRecipe::input),
                        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(LootCookingRecipe::lootTable),
                        Codec.list(LootResult.CODEC).fieldOf("results").forGetter(LootCookingRecipe::results),
                        Codec.STRING.optionalFieldOf("result_name",
                                DEFAULT_RESULT_NAME_TRANSLATION_KEY).forGetter(LootCookingRecipe::resultTranslationKey),
                        Codec.FLOAT.optionalFieldOf("experience", 0.0f).forGetter(LootCookingRecipe::experience),
                        Codec.INT.optionalFieldOf("cookingtime", defaultCookingTime).forGetter(LootCookingRecipe::cookingTime)
                )
                .apply(i, (commonInfo, bookInfo, ingredient, lootTable, results, resultName, experience, cookingTime) ->
                        new LootCookingRecipe(commonInfo, bookInfo, ingredient, lootTable, results, resultName, experience, cookingTime, type)));
    }

    private static StreamCodec<RegistryFriendlyByteBuf, LootCookingRecipe> streamCodec(RecipeType<? extends AbstractCookingRecipe> type) {
        return StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC, LootCookingRecipe::commonInfo,
                CookingBookInfo.STREAM_CODEC, LootCookingRecipe::bookInfo,
                Ingredient.CONTENTS_STREAM_CODEC, LootCookingRecipe::input,
                ResourceKey.streamCodec(Registries.LOOT_TABLE), LootCookingRecipe::lootTable,
                ByteBufCodecs.collection(java.util.ArrayList::new, LootResult.STREAM_CODEC), LootCookingRecipe::results,
                ByteBufCodecs.STRING_UTF8, LootCookingRecipe::resultTranslationKey,
                ByteBufCodecs.FLOAT, LootCookingRecipe::experience,
                ByteBufCodecs.INT, LootCookingRecipe::cookingTime,
                (commonInfo, bookInfo, ingredient, lootTable, results, resultName, experience, cookingTime) ->
                        new LootCookingRecipe(commonInfo, bookInfo, ingredient, lootTable, results, resultName, experience, cookingTime, type));
    }

    private Recipe.CommonInfo commonInfo() {
        return this.commonInfo;
    }

    private CookingBookInfo bookInfo() {
        return this.bookInfo;
    }
}