package com.TNTStudios.playertimelimit.scheduler;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class TimeCountdownTicker {

    private static int tickCounter = 0;
    private static final Map<UUID, Set<Integer>> advertenciasEnviadas = new HashMap<>();

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= 20) { // Cada segundo
                tickCounter = 0;
                tick(server);
            }
        });
    }

    private static void tick(MinecraftServer server) {
        PlayerTimeDataManager.tickAll();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            int tiempoRestante = PlayerTimeDataManager.getTime(uuid);

            if (PlayerTimeDataManager.shouldKick(uuid)) {
                player.networkHandler.disconnect(Text.of(PLTConfig.mensajes.tiempoAgotado));
                continue;
            }

            // Advertencias personalizadas
            for (PLTConfig.Advertencia adv : PLTConfig.advertencias) {
                if (tiempoRestante == adv.tiempo) {
                    Set<Integer> advertidas = advertenciasEnviadas.computeIfAbsent(uuid, k -> new HashSet<>());
                    if (!advertidas.contains(adv.tiempo)) {
                        player.sendMessage(Text.of(adv.mensaje), false);
                        advertidas.add(adv.tiempo);
                    }
                }
            }
        }
    }

    public static void resetAdvertencias(UUID uuid) {
        advertenciasEnviadas.remove(uuid);
    }
}
