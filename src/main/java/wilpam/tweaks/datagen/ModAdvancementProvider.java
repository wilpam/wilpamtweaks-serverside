package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.advancements.triggers.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.content.ModBlocks;
import wilpam.tweaks.content.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.@NonNull Provider registryLookup,
                                    @NonNull Consumer<AdvancementHolder> consumer) {
        var recipes = registryLookup.lookupOrThrow(Registries.RECIPE);
        AdvancementHolder root = Advancement.Builder.advancement()
                .rootDisplay(
                        ModItems.WILPAM_ICON.item(),
                        Component.translatable("advancements.wilpam_tweaks.root.title"),
                        Component.translatable("advancements.wilpam_tweaks.root.description"),
                        Identifier.fromNamespaceAndPath(ModBlocks.ID, "block/flint_block"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false)
                .addCriterion("on_join", PlayerTrigger.TriggerInstance.tick())
                .build(Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/root"));
        consumer.accept(root);

        consumer.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModBlocks.FLINT_BLOCK_ITEM,
                        Component.translatable("advancements.wilpam_tweaks.flint_block.title"),
                        Component.translatable("advancements.wilpam_tweaks.flint_block.description"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("obtain_flint_block",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.FLINT_BLOCK_ITEM))
                .build(Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/flint_block")));

        consumer.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.SLIME_BALL,
                        Component.translatable("advancements.wilpam_tweaks.slimeball.title"),
                        Component.translatable("advancements.wilpam_tweaks.slimeball.description"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("smelt_slimeball_substrate",
                        craftedRecipe(recipes, "cook_slimeball"))
                .build(Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/slimeball")));

        consumer.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.CHEESE.item(),
                        Component.translatable("advancements.wilpam_tweaks.cheese.title"),
                        Component.translatable("advancements.wilpam_tweaks.cheese.description"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("obtain_cheese",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CHEESE.item()))
                .build(Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/cheese")));

        consumer.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.STAMP.item(),
                        Component.translatable("advancements.wilpam_tweaks.stamp.title"),
                        Component.translatable("advancements.wilpam_tweaks.stamp.description"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("stamp_item",
                        craftedRecipe(recipes, "stamping"))
                .build(Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/stamp")));

        consumer.accept(Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.OIL.item(),
                        Component.translatable("advancements.wilpam_tweaks.oil.title"),
                        Component.translatable("advancements.wilpam_tweaks.oil.description"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("make_oil",
                        craftedRecipe(recipes, "cook_coal"))
                .build(Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/oil")));
    }

    @Override
    public @NonNull String getName() {
        return "Mod Advancements";
    }

    private static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedRecipe(
            HolderLookup.RegistryLookup<Recipe<?>> recipes, String path) {
        var key = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModBlocks.ID, path));
        var recipe = recipes.getOrThrow(key);
        return RecipeCraftedTrigger.TriggerInstance.craftedItem(HolderSet.direct(recipe));
    }
}
