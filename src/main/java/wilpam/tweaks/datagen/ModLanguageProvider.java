package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("block.wilpam_tweaks.flint_block", "Flint Block");
        translationBuilder.add("item.wilpam_tweaks.flint_block", "Flint Block");
        translationBuilder.add("block.wilpam_tweaks.plastic_block", "Plastic Block");
        translationBuilder.add("item.wilpam_tweaks.plastic_block", "Plastic Block");
        translationBuilder.add("item.wilpam_tweaks.dough", "Dough");
        translationBuilder.add("item.wilpam_tweaks.cheese", "Cheese");
        translationBuilder.add("item.wilpam_tweaks.slimeball_substrate", "Slimeball Substrate");
        translationBuilder.add("item.wilpam_tweaks.oil", "Crude Oil");
        translationBuilder.add("item.wilpam_tweaks.stamp", "Stamp");
        translationBuilder.add("item.wilpam_tweaks.stamp.signature", "Stamped by %s");
        translationBuilder.add("item.wilpam_tweaks.oil_wax", "Oil Wax");
        translationBuilder.add("item.wilpam_tweaks.prepolymerized_plate", "Prepolymerized Plastic Plate");
        translationBuilder.add("item.wilpam_tweaks.plastic_plate", "Plastic Plate");

        translationBuilder.add("recipe.wilpam_tweaks.random_cook_result", "Any of:");
        translationBuilder.add("recipe.wilpam_tweaks.random_cook.nothing", "No item");

        translationBuilder.add("recipe.wilpam_tweaks.crude_oil_distillation", "Crude Oil Distillation");

        translationBuilder.add("advancements.wilpam_tweaks.root.title", "wenyapaoucus wenyamim");
        translationBuilder.add("advancements.wilpam_tweaks.root.description", "Unfortunately, it's still new WilpamTweaks().");
        translationBuilder.add("advancements.wilpam_tweaks.flint_block.title", "We Didn't Start the Fire");
        translationBuilder.add("advancements.wilpam_tweaks.flint_block.description", "Put some flint together to craft a Flint Block");
        translationBuilder.add("advancements.wilpam_tweaks.slimeball.title", "Real Slimeballs Not Clickbait");
        translationBuilder.add("advancements.wilpam_tweaks.slimeball.description", "Smelt Slimeball Substrate into a Slimeball");
        translationBuilder.add("advancements.wilpam_tweaks.cheese.title", "Stop Being So Cheesy");
        translationBuilder.add("advancements.wilpam_tweaks.cheese.description", "Smelt a Milk Bucket into Cheese");
        translationBuilder.add("advancements.wilpam_tweaks.stamp.title", "Signature Move");
        translationBuilder.add("advancements.wilpam_tweaks.stamp.description", "Use a Stamp to stamp your name onto an item");
        translationBuilder.add("advancements.wilpam_tweaks.oil.title", "Liberation Time");
        translationBuilder.add("advancements.wilpam_tweaks.oil.description", "Smelt some coal to get Crude Oil");
        translationBuilder.add("advancements.wilpam_tweaks.oil_distillation.title", "Factorio Reference");
        translationBuilder.add("advancements.wilpam_tweaks.oil_distillation.description", "Smelt some Crude Oil to get a mix of things");
        translationBuilder.add("advancements.wilpam_tweaks.plastic.title", "10,000 Smelting Steps");
        translationBuilder.add("advancements.wilpam_tweaks.plastic.description", "Get a Plastic Plate from a Prepolymerized Plate");
    }

    @Override
    public @NonNull String getName() {
        return "Mod Language";
    }
}
