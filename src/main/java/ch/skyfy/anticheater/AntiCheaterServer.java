package ch.skyfy.anticheater;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.skyfy.anticheater.AntiCheater.MOD_ID;

public class AntiCheaterServer {
    public static void initialize() {
        final var clientAnswered = new AtomicBoolean(false);
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID, "anticheater"), (server, player, handler, buf, responseSender) -> {
            final var clientAnswer = buf.readString();
            clientAnswered.set(true);
            server.execute(() -> {
                switch (clientAnswer) {
                    case "suspect" -> {
                        server.getPlayerManager().broadcast(Text.of("Player " + player.getName().asString() + " is a cheater !"), MessageType.CHAT, Util.NIL_UUID);
                        player.networkHandler.disconnect(Text.of("You have installed cheat mods or texture packs !\n"));
                    }
                    case "validate" -> server.getPlayerManager().broadcast(Text.of("Player " + player.getName().asString() + " is not a cheater !"), MessageType.CHAT, Util.NIL_UUID);
                }
            });
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // If the player who logs in has not installed the modpack, or does not have the "skyfymodclient" mod, he will be kicked after 60 secondes
            Executors.newSingleThreadScheduledExecutor(r -> new Thread(r) {{
                setDaemon(true);
            }}).schedule(() -> {
                if (!clientAnswered.get())
                    client.execute(() -> handler.disconnect(Text.of("AntiCheater mod is not present on client. Client has to install it")));
            }, 80, TimeUnit.SECONDS);
        });
    }
}
