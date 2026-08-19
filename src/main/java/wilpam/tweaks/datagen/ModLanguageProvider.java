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
        translationBuilder.add("item.wilpam_tweaks.slimeball_substrate", "Slimeball Substrate");

        translationBuilder.add("advancements.wilpam_tweaks.root.title", "Wilpam Tweaks");
        translationBuilder.add("advancements.wilpam_tweaks.root.description", "The start of a sturdier world");
        translationBuilder.add("advancements.wilpam_tweaks.flint_block.title", "Eye of Flint");
        translationBuilder.add("advancements.wilpam_tweaks.flint_block.description", "Smash flint together to craft a Flint Block");
        translationBuilder.add("advancements.wilpam_tweaks.slimeball.title", "Sticky Situation");
        translationBuilder.add("advancements.wilpam_tweaks.slimeball.description", "Smelt Slimeball Substrate into a Slimeball");
    }

    @Override
    public String getName() {
        return "Mod Language";
    }
}
