package wilpam.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.Set;

public final class WilpamTweaksDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModAdvancementProvider::new);
        pack.addProvider(ModRandomLootTableProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider(ModLanguageProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModBlockTagsProvider::new);
    }

    @Override
    public void buildReloadableRegistry(RegistrySetBuilder registryBuilder) {
        // Make recipe references available to the standalone advancement provider.
        registryBuilder.add(new MultiRegistryBootstrap() {
            @Override
            public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
                return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
            }

            @Override
            public void run(BootstrapGetter registries) {
                ModRecipeProvider.bootstrap(registries.get(Registries.RECIPE), registries.get(Registries.ADVANCEMENT));
            }
        });
    }
}
