package ch.skyfy.anticheater.client;

import ch.skyfy.anticheater.utils.Deactivator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AntiCheaterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (Deactivator.getInstance().isDisabled()) return;
        AntiCheaterImpl.initialize();
    }
}
