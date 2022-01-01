package ch.skyfy.anticheater.config;

import java.util.ArrayList;
import java.util.List;

public class Config {

    /**
     * A list of suspect words, like advanced-xray-fabric (this is literally the mod id), so if this mod is present, player will be kick from server
     */
    public static final List<String> DEFAULT_SUSPECT_WORDS = new ArrayList<>() {{
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
    public static final List<String> DEFAULT_EXCLUDED_WORDS = new ArrayList<>() {{
        add("cheatbreaker");
    }};

    public final List<String> suspectsWords, excludedWords;

    public Config() {
        this.suspectsWords = DEFAULT_SUSPECT_WORDS;
        this.excludedWords = DEFAULT_EXCLUDED_WORDS;
    }
}
