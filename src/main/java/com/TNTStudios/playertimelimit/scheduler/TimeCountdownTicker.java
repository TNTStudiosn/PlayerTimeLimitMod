package com.TNTStudios.playertimelimit.scheduler;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.time.LocalTime;
import java.time.ZoneId;
import com.TNTStudios.playertimelimit.util.PLTTextUtils;

import java.util.*;

public class TimeCountdownTicker {

    private static int tickCounter = 0;
    private static final Map<UUID, Set<Integer>> advertenciasEnviadas = new HashMap<>();
    private static String ultimaFechaReinicio = "";

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                tick(server);
            }
        });
    }

    private static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();

            if (PlayerTimeDataManager.isPaused(uuid)) continue;

            int tiempo = PlayerTimeDataManager.getTime(uuid);
            if (tiempo > 0) {
                PlayerTimeDataManager.setTime(uuid, tiempo - 1);
                if (tiempo - 1 == 0) {
                    PlayerTimeDataManager.markOutOfTime(uuid);
                }
            }
        }

        verificarReinicioDiario(server);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            int tiempoRestante = PlayerTimeDataManager.getTime(uuid);

            if (PlayerTimeDataManager.shouldKick(uuid)) {
                player.networkHandler.disconnect(Text.of(PLTConfig.mensajes.tiempoAgotado));
                continue;
            }

            for (PLTConfig.Advertencia adv : PLTConfig.advertencias) {
                if (tiempoRestante == adv.tiempo) {
                    Set<Integer> advertidas = advertenciasEnviadas.computeIfAbsent(uuid, k -> new HashSet<>());
                    if (!advertidas.contains(adv.tiempo)) {
                        PLTTextUtils.sendMessage(player, adv.mensaje, tiempoRestante);
                        advertidas.add(adv.tiempo);
                    }
                }
            }
        }
    }


    private static void verificarReinicioDiario(MinecraftServer server) {
        ZoneId zona = PLTConfig.getZonaHoraria();
        LocalTime horaConfig = PLTConfig.getHoraReinicio();
        LocalTime ahora = LocalTime.now(zona);
        String fechaHoy = java.time.LocalDate.now(zona).toString();

        if (ultimaFechaReinicio.equals(fechaHoy)) return;

        if (ahora.getHour() == horaConfig.getHour() && ahora.getMinute() == horaConfig.getMinute()) {
            System.out.println("[PlayerTimeLimit] Reiniciando tiempos diarios a todos los jugadores...");

            PlayerTimeDataManager.resetAll();
            PlayerTimeDataManager.save();

            for (ServerPlayerEntity jugador : server.getPlayerManager().getPlayerList()) {
                UUID uuid = jugador.getUuid();
                jugador.sendMessage(Text.of("♻ Tu tiempo ha sido reiniciado."), false);
                resetAdvertencias(uuid);
            }

            ultimaFechaReinicio = fechaHoy;
        }
    }

    public static void clearUltimaFechaReinicio() {
        ultimaFechaReinicio = "";
    }

    public static void resetAdvertencias(UUID uuid) {
        advertenciasEnviadas.remove(uuid);
    }
}
