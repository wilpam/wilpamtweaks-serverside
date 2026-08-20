package wilpam.tweaks.datagen;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import wilpam.tweaks.content.ModBlocks;
import wilpam.tweaks.content.ModItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ModModelProvider implements DataProvider {
    private final FabricPackOutput output;
    private PackOutput.PathProvider blockPaths;
    private PackOutput.PathProvider itemPaths;
    private CachedOutput cachedOutput;
    List<CompletableFuture<?>> futures = new ArrayList<>();

    public ModModelProvider(FabricPackOutput output) {
        this.output = output;

    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cachedOutput) {
        this.cachedOutput = cachedOutput;
        blockPaths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        itemPaths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");

        makeCubeBlockAndItemModelOf(ModBlocks.FLINT_BLOCK_ID);

        makeItemModelOf(ModItems.CHEESE_ID);
        makeItemModelOf(ModItems.DOUGH_ID);
        makeItemModelOf(ModItems.OIL_ID);
        makeItemModelOf(ModItems.STAMP_ID);
        makeItemModelOf(ModItems.SLIMEBALL_SUBSTRATE_ID);

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

    private void makeItemModelOf(Identifier id) {
        Path modelPath = itemPaths.json(id);
        futures.add(DataProvider.saveStable(cachedOutput, itemGenerated("wilpam_tweaks:item/" + id.getPath()), modelPath));
    }

    private void makeCubeBlockAndItemModelOf(@SuppressWarnings("SameParameterValue") Identifier id) {
        Path blockModel = blockPaths.json(id);
        futures.add(DataProvider.saveStable(cachedOutput, cubeAll("wilpam_tweaks:block/" + id.getPath()), blockModel));

        Path blockItemModel = itemPaths.json(id);
        futures.add(DataProvider.saveStable(cachedOutput, parentOnly("wilpam_tweaks:block/" + id.getPath()), blockItemModel));
    }

    @Override
    public @NonNull String getName() {
        return "Mod Models";
    }
}
