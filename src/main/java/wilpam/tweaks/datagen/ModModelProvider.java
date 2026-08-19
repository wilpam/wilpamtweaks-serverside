package wilpam.tweaks.datagen;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import wilpam.tweaks.content.ModBlocks;
import wilpam.tweaks.content.ModItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ModModelProvider implements DataProvider {
    private final FabricPackOutput output;

    public ModModelProvider(FabricPackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        PackOutput.PathProvider blockPaths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        PackOutput.PathProvider itemPaths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");

        List<CompletableFuture<?>> futures = new ArrayList<>();

        Path blockModel = blockPaths.json(ModBlocks.FLINT_BLOCK_ID);
        futures.add(DataProvider.saveStable(cachedOutput, cubeAll("wilpam_tweaks:block/flint_block"), blockModel));

        Path doughModel = itemPaths.json(ModItems.DOUGH_ID);
        futures.add(DataProvider.saveStable(cachedOutput, itemGenerated("wilpam_tweaks:item/dough"), doughModel));

        Path flintBlockItemModel = itemPaths.json(ModBlocks.FLINT_BLOCK_ID);
        futures.add(DataProvider.saveStable(cachedOutput, parentOnly("wilpam_tweaks:block/flint_block"), flintBlockItemModel));

        Path substrateModel = itemPaths.json(ModItems.SLIMEBALL_SUBSTRATE_ID);
        futures.add(DataProvider.saveStable(cachedOutput, itemGenerated("wilpam_tweaks:item/slimeball_substrate"), substrateModel));

        Path iconModel = itemPaths.json(Identifier.fromNamespaceAndPath(ModBlocks.ID, "wilpam_icon"));
        futures.add(DataProvider.saveStable(cachedOutput, itemGenerated("wilpam_tweaks:item/wilpam_icon"), iconModel));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private static JsonObject cubeAll(String texture) {
        JsonObject textures = new JsonObject();
        textures.addProperty("all", texture);
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");
        json.add("textures", textures);
        return json;
    }

    private static JsonObject itemGenerated(String texture) {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texture);
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");
        json.add("textures", textures);
        return json;
    }

    private static JsonObject parentOnly(String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        return json;
    }

    @Override
    public String getName() {
        return "Mod Models";
    }
}
