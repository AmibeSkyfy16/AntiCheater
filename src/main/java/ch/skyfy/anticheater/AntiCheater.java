package ch.skyfy.anticheater;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class AntiCheater implements ModInitializer {

    public static AtomicBoolean DISABLED = new AtomicBoolean(false);

    public static final String MOD_ID = "anti_cheater";

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("AntiCheater");

    @Override
    public void onInitialize() {
        if (createConfigDir()) return;
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
            AntiCheaterServer.initialize();
    }

    private boolean createConfigDir() {
        var configDir = MOD_CONFIG_DIR.toFile();
        if (!configDir.exists()) {
            var result = configDir.mkdir();
            if (!result) {
                System.out.println("[AntiCheater] The configuration cannot be created");
                DISABLED.set(true);
            }
        }
        return DISABLED.get();
    }
}
