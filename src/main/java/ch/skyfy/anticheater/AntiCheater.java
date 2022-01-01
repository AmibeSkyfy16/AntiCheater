package ch.skyfy.anticheater;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AntiCheater implements ModInitializer {

    public static final String MOD_ID = "anti_cheater";

    public static final Logger LOGGER = LogManager.getLogger("AntiCheater");

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            if (Configurator.initialize()) return; // Create configuration files for server side, user will be able to set some specific word
            AntiCheaterServer.initialize();
        }
    }
}
