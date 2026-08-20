package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class WilpamTweaksDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModRandomLootTableProvider::new);
        pack.addProvider(ModAdvancementProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider(ModLanguageProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModBlockTagsProvider::new);
    }
}
