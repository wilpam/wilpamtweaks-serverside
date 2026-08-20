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

    // The furnace calls assemble() on every tick with the same input ItemStack
    // (identity = the furnace's inventory slot), and burn() shrinks it in place
    // when a cook completes. Rolling randomly every tick results in smelts not finishing,
    // so the roll is cached per input stack and only re-rolled when a new cook has begun.
    // Weak identity so we don't leak anything.
    private final java.util.concurrent.ConcurrentMap<ItemStack, ResultCache> rollCaches =
            new com.google.common.collect.MapMaker().weakKeys().makeMap();

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
    public @NonNull ItemStack assemble(SingleRecipeInput input) {
        ItemStack in = input.item();
        ResultCache cache = rollCaches.computeIfAbsent(in, ignored -> new ResultCache());
        if (in.getCount() != cache.lastCount) {
            cache.lastCount = in.getCount();
            cache.roll = rollLoot();
        }
        return cache.roll;
    }

    private static final class ResultCache {
        int lastCount = -1;
        ItemStack roll = ItemStack.EMPTY;
    }

    private ItemStack rollLoot() {
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
        LootParams params = new LootParams.Builder(level).create(table.getParamSet());
        var stacks = table.getRandomItems(params, RandomSource.create());
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
            assert item != null;
            int count = r.weight();
            float percentage = count * 100f / totalWeight;
            Component name = Component.translatable(item.getDescriptionId())
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false));
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