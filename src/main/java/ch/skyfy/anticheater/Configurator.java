package ch.skyfy.anticheater;

import ch.skyfy.anticheater.config.Config;
import ch.skyfy.anticheater.config.ConfigUtils;
import ch.skyfy.anticheater.utils.Deactivator;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static ch.skyfy.anticheater.ConstantsMessage.CONFIG_FOLDER_COULD_NOT_BE_CREATED;

public final class Configurator {

    private final static class ConfiguratorHolder {
        private static final Configurator INSTANCE = new Configurator();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Configurator getInstance() {
        return Configurator.ConfiguratorHolder.INSTANCE;
    }

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("AntiCheater");

    /**
     * Try to initialize the config
     * if an exception occurs, return true and mod will be disabled
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean initialize() {
        getInstance();
        return Deactivator.getInstance().isDisabled();
    }

    public Config config;

    private Configurator() {
        if (createConfigDir()) return;
        config = ConfigUtils.getOrCreateConfig(Config.class, "config.json");
    }

    /**
     * Try to create a folder called AntiCheater that will contain all configurations files
     * @return true if the file has been created, false otherwise
     */
    private boolean createConfigDir() {
        var configDir = MOD_CONFIG_DIR.toFile();
        if (!configDir.exists())
            if (!configDir.mkdir())
                return Deactivator.getInstance().disable(CONFIG_FOLDER_COULD_NOT_BE_CREATED.getMessage());
        return Deactivator.getInstance().isDisabled();
    }
}
