package wilpam.tweaks;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import wilpam.tweaks.content.ModBlocks;
import wilpam.tweaks.content.ModCustomRecipes;
import wilpam.tweaks.content.ModItems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WilpamTweaks implements ModInitializer {
    public static final String MOD_ID = "wilpam_tweaks";

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModCustomRecipes.initialize();

        ensureAutoHostEnabled();

        // Bridge custom item models into the pack's item-asset index (assets/<mod>/items/),
        // so the client can resolve item models that don't belong to a vanilla item.
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.fromNamespaceAndPath(MOD_ID, "item"));

        // Includes this mod's assets (models, textures, lang) in the server resource pack
        PolymerResourcePackUtils.addModAssets(MOD_ID);
        // Force clients to accept the resource pack so custom textures are always visible
        PolymerResourcePackUtils.markAsRequired();
    }

    /*
     * Polymer's autohost module is disabled by default (enabled: false). When the
     * server has no resource pack hosting configured, clients never receive the pack,
     * so custom textures/names show up as missing. This writes the autohost config.
     */
    private static void ensureAutoHostEnabled() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("polymer");
        Path configFile = configDir.resolve("auto-host.json");
        if (Files.exists(configFile)) {
            return;
        }

        try {
            Files.createDirectories(configDir);
            JsonObject enabled = new JsonObject();
            enabled.addProperty("enabled", true);
            Files.writeString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(enabled));
        } catch (IOException e) {
            throw new RuntimeException("Failed to enable Polymer auto-host config", e);
        }
    }
}