package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.recipe.LootCookingRecipe;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Loot tables used as results of {@code wilpam.tweaks.recipe.LootCookingRecipe}s.
 */
public final class ModRandomLootTableProvider extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public ModRandomLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, LootContextParamSets.EMPTY);
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
        registryLookupFuture.join().lookupOrThrow(Registries.RECIPE).listElements().forEach(holder -> {
            if (!(holder.value() instanceof LootCookingRecipe recipe)) {
                return;
            }
            LootPool.Builder pool = LootPool.lootPool()
                    .setRolls(Holder.direct(new ConstantValue(1)));
            for (LootCookingRecipe.LootResult result : recipe.results()) {
                var item = BuiltInRegistries.ITEM.getOrThrow(result.item()).value();
                if (item == Items.AIR) {
                    pool.add(EmptyLootItem.emptyItem().setWeight(result.weight()));
                } else {
                    pool.add(LootItem.lootTableItem(item)
                            .setWeight(result.weight()));
                }
            }
            exporter.accept(recipe.lootTable(), LootTable.lootTable()
                    .setParamSet(LootContextParamSets.EMPTY)
                    .withPool(pool));
        });
    }

    @Override
    public @NonNull String getName() {
        return "Mod Random Loot Tables";
    }
}
