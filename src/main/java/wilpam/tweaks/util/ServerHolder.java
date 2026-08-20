package wilpam.tweaks.util;

import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;

/**
 * Holds the currently running server.
 */
public final class ServerHolder {
    @Nullable
    private static MinecraftServer server;

    private ServerHolder() {
    }

    public static void setServer(@Nullable MinecraftServer server) {
        ServerHolder.server = server;
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }
}