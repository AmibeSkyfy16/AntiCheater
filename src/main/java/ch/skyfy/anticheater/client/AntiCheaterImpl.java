package ch.skyfy.anticheater.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipFile;

import static ch.skyfy.anticheater.AntiCheater.MOD_ID;

public class AntiCheaterImpl {

    /**
     * A list of suspect words, like advanced-xray-fabric (this is literally the mod id), so if this mod is present, player will be kick from server
     */
    public static final List<String> SUSPECT_WORDS = new ArrayList<>() {{
        add("x-ray");
        add("xray");
        add("x ray");
        add("advanced-xray-fabric");
        add("better-xray");
        add("betterxray");
        add("better xray");
        add("Better Xray [ Vanilla ] 1.18");
        add("Better Xray [ Vanilla ] Lite 1.18");
        add("Better Xray [ Vanilla ] 1.1 By AlleCraft");
        add("Better Xray [ Vanilla ] Lite 1.0 By AlleCraft");
        add("Super Xray v1.0.0");
        add("Super Xray");
        add("Super X-ray pack - For Minecraft 1.13+");
        add("Xray Bridge 1.18");
        add("Bridge to Better X-Ray and Better 3D Ores By AlleCraft");

        add("fly-mod");
        add("flymod");
        add("fly mod");

        add("easy-cheats");
        add("easycheats");
        add("easy cheats");
        add("easy cheat");

        add("cheats");
        add("cheat");
        add("cheats mod");
        add("cheat mod");
    }};

    /**
     * Some strings from certain mods that are not cheats are still considered cheats
     */
    public static final List<String> EXCLUDED_WORDS = new ArrayList<>() {{
        add("cheatbreaker");
    }};

    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            Executors.newSingleThreadScheduledExecutor(r -> new Thread(r) {{
                setDaemon(true);
            }}).schedule(() -> client.execute(() -> ClientPlayNetworking.send(new Identifier(MOD_ID, "anticheater"), PacketByteBufs.create().writeString(isPlayerSuspect() ? "suspect" : "validate"))), 30, TimeUnit.SECONDS);
        });
    }

    private static boolean isPlayerSuspect() {
        return areModsSuspect() || areTexturesPacksSuspect();
    }

    private static boolean isCheater(String researchWord) {
        for (var suspect : SUSPECT_WORDS)
            if (StringUtils.containsIgnoreCase(researchWord, suspect))
                if (EXCLUDED_WORDS.stream().noneMatch(exceptionString -> StringUtils.containsIgnoreCase(exceptionString, suspect))) {
                    System.out.println("[AntiCheater] player " + " is a cheater !\t suspect word is " + suspect);
                    System.out.println("[AntiCheater] \tResearch word was -> " + researchWord);
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
                    System.out.println("[AntiCheater] -> [ERROR] -> FileName: " + file.getName());
                }
            }
        }
        return returnValue.get();
    }
}
