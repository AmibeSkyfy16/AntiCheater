package ch.skyfy.anticheater.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AntiCheaterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AntiCheaterImpl.initialize();
    }
}
