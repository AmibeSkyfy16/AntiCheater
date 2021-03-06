package ch.skyfy.anticheater.config;

import ch.skyfy.anticheater.Configurator;
import ch.skyfy.anticheater.utils.Deactivator;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class ConfigUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     *
     * @param relativeFilePath the file path of the config, relative to FabricLoader.getInstance().getConfigDir().resolve("FolderModName")
     * @param <P> object representing the configuration that we have to save or load
     * @return read config or default config or null if exception
     */
    @SuppressWarnings("UnstableApiUsage")
    public static <P> @Nullable P getOrCreateConfig(Class<P> pClass, String relativeFilePath) {
        var type = TypeToken.of(pClass).getType();
        var configFile = Configurator.MOD_CONFIG_DIR.resolve(relativeFilePath).toFile();
        configFile = FabricLoader.getInstance().getConfigDir().resolve("AntiCheater").resolve("config.json").toFile();
        P config = null;
        try {
            if (configFile.exists())
                config = get(configFile, type);
//                config = gson.fromJson(reader, type);
            else
                save(configFile, type,pClass.getDeclaredConstructor().newInstance());
//                gson.toJson(pClass.getDeclaredConstructor().newInstance(), type, writer);
        } catch (IOException | JsonIOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            Deactivator.getInstance().disable("");
        }
        return config;
    }

    public static <P> P get(File file, Type type) throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        }
    }

    public static <P> void save(File file, Type type, P p) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(p, type, writer);
        }
    }

}
