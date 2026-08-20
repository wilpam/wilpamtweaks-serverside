package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.advancements.triggers.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
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
    public void generateAdvancement(HolderLookup.@NonNull Provider registryLookup, @NonNull Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        ModItems.WILPAM_ICON.item(),
                        Component.translatable("advancements.wilpam_tweaks.root.title"),
                        Component.translatable("advancements.wilpam_tweaks.root.description"),
                        Identifier.fromNamespaceAndPath(ModBlocks.ID, "block/flint_block"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false)
                .addCriterion("on_join", PlayerTrigger.TriggerInstance.tick())
                .save(consumer, Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/root"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModBlocks.FLINT_BLOCK_ITEM,
                        Component.translatable("advancements.wilpam_tweaks.flint_block.title"),
                        Component.translatable("advancements.wilpam_tweaks.flint_block.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("obtain_flint_block",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.FLINT_BLOCK_ITEM))
                .save(consumer, Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/flint_block"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.SLIME_BALL,
                        Component.translatable("advancements.wilpam_tweaks.slimeball.title"),
                        Component.translatable("advancements.wilpam_tweaks.slimeball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("smelt_slimeball_substrate",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceKey.create(Registries.RECIPE,
                                        Identifier.fromNamespaceAndPath(ModBlocks.ID, "cook_slimeball"))))
                .save(consumer, Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/slimeball"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.CHEESE.item(),
                        Component.translatable("advancements.wilpam_tweaks.cheese.title"),
                        Component.translatable("advancements.wilpam_tweaks.cheese.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("obtain_cheese",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CHEESE.item()))
                .save(consumer, Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/cheese"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.STAMP.item(),
                        Component.translatable("advancements.wilpam_tweaks.stamp.title"),
                        Component.translatable("advancements.wilpam_tweaks.stamp.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("stamp_item",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceKey.create(Registries.RECIPE,
                                        Identifier.fromNamespaceAndPath(ModBlocks.ID, "stamping"))))
                .save(consumer, Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/stamp"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.OIL.item(),
                        Component.translatable("advancements.wilpam_tweaks.oil.title"),
                        Component.translatable("advancements.wilpam_tweaks.oil.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("make_oil",
                        RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                ResourceKey.create(Registries.RECIPE,
                                        Identifier.fromNamespaceAndPath(ModBlocks.ID, "cook_coal"))))
                .save(consumer, Identifier.fromNamespaceAndPath(ModBlocks.ID, "main/oil"));
    }

    @Override
    public @NonNull String getName() {
        return "Mod Advancements";
    }
}
