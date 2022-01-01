package ch.skyfy.anticheater.client;

import ch.skyfy.anticheater.AntiCheater;
import ch.skyfy.anticheater.config.Config;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipFile;

import static ch.skyfy.anticheater.AntiCheater.MOD_ID;

public class AntiCheaterImpl {

    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> Executors.newSingleThreadScheduledExecutor(r -> new Thread(r) {{
            setDaemon(true);
        }}).schedule(() -> {
            final var isPlayerSuspect = isPlayerSuspect() ? "suspect" : "validate";
            client.execute(() -> ClientPlayNetworking.send(new Identifier(MOD_ID, "anticheater"), PacketByteBufs.create().writeString(isPlayerSuspect)));
        }, 20, TimeUnit.SECONDS));
    }

    private static boolean isPlayerSuspect() {
        return areModsSuspect() || areTexturesPacksSuspect();
    }

    private static boolean isCheater(String researchWord) {
        for (var suspect : Config.DEFAULT_SUSPECT_WORDS)
            if (StringUtils.containsIgnoreCase(researchWord, suspect))
                if (Config.DEFAULT_EXCLUDED_WORDS.stream().noneMatch(exceptionString -> StringUtils.containsIgnoreCase(exceptionString, suspect))) {
                    AntiCheater.LOGGER.info("Player is a cheater !\t suspect word is " + suspect);
                    AntiCheater.LOGGER.info("Research word was -> " + researchWord);
                    return true;
                }
        return false;
    }

    private static boolean areModsSuspect() {
        for (var modContainer : FabricLoader.getInstance().getAllMods()) {
            var metadata = modContainer.getMetadata();
            if (isCheater(metadata.getName()) || isCheater(metadata.getId()) || isCheater(metadata.getDescription()))
                return true;
        }
        return false;
    }

    private static boolean areTexturesPacksSuspect() {
        var returnValue = new AtomicBoolean(false);
        var resourcepacksFolder = FabricLoader.getInstance().getConfigDir().getParent().toAbsolutePath().resolve("resourcepacks").toFile();
        var files = resourcepacksFolder.listFiles();
        if (files == null) return false;
        for (var file : files) {
            if (file.getName().endsWith(".zip")) {
                try {
                    var zipFile = new ZipFile(file);
                    zipFile.entries().asIterator().forEachRemaining(zipEntry -> {
                        if (!zipEntry.getName().equalsIgnoreCase("pack.mcmeta")) return;
                        if (isCheater(zipEntry.getName())) returnValue.set(true);
                        try (var bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)))) {
                            String line;
                            while ((line = bufferedReader.readLine()) != null)
                                if (isCheater(line)) returnValue.set(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    AntiCheater.LOGGER.error("An error occurred with the following file: " + file.getAbsolutePath());
                }
            }
        }
        return returnValue.get();
    }
}
