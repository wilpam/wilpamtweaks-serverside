package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("block.wilpam_tweaks.flint_block", "Flint Block");
        translationBuilder.add("item.wilpam_tweaks.flint_block", "Flint Block");
        translationBuilder.add("item.wilpam_tweaks.dough", "Dough");
        translationBuilder.add("item.wilpam_tweaks.cheese", "Cheese");
        translationBuilder.add("item.wilpam_tweaks.slimeball_substrate", "Slimeball Substrate");

        translationBuilder.add("advancements.wilpam_tweaks.root.title", "Wilpam Tweaks");
        translationBuilder.add("advancements.wilpam_tweaks.root.description", "new WilpamTweaks()");
        translationBuilder.add("advancements.wilpam_tweaks.flint_block.title", "We Didn't Start the Fire");
        translationBuilder.add("advancements.wilpam_tweaks.flint_block.description", "Put some flint together to craft a Flint Block");
        translationBuilder.add("advancements.wilpam_tweaks.slimeball.title", "Real Slimeballs Not Clickbait");
        translationBuilder.add("advancements.wilpam_tweaks.slimeball.description", "Smelt Slimeball Substrate into a Slimeball");
        translationBuilder.add("advancements.wilpam_tweaks.cheese.title", "Stop Being So Cheesy");
        translationBuilder.add("advancements.wilpam_tweaks.cheese.description", "Smoke a Milk Bucket into Cheese");
    }

    @Override
    public String getName() {
        return "Mod Language";
    }
}
