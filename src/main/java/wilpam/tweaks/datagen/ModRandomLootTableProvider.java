package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.WilpamTweaks;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Loot tables used as results of {@code wilpam.tweaks.recipe.LootCookingRecipe}s.
 */
public final class ModRandomLootTableProvider extends SimpleFabricLootTableSubProvider {
    public static final ResourceKey<LootTable> BAKED_APPLE = ResourceKey.create(Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath(WilpamTweaks.MOD_ID, "cooking/baked_apple"));

    public ModRandomLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, LootContextParamSets.EMPTY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
        exporter.accept(BAKED_APPLE, LootTable.lootTable()
                .setParamSet(LootContextParamSets.EMPTY)
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .add(LootItem.lootTableItem(Items.APPLE).setWeight(10))
                        .add(LootItem.lootTableItem(Items.SUGAR).setWeight(4))
                        .add(LootItem.lootTableItem(Items.COOKIE).setWeight(2))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(1))));
    }

    @Override
    public @NonNull String getName() {
        return "Mod Random Loot Tables";
    }
}