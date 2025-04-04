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
    private static String ultimaFechaReinicio = ""; // yyyy-MM-dd

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
        // ✅ Descontar tiempo solo a jugadores conectados
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

        // ✅ Reinicio diario
        verificarReinicioDiario(server);

        // ✅ Advertencias
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
        String fechaHoy = java.time.LocalDate.now(zona).toString(); // yyyy-MM-dd

        if (ultimaFechaReinicio.equals(fechaHoy)) return; // Ya se hizo hoy

        if (ahora.getHour() == horaConfig.getHour() && ahora.getMinute() == horaConfig.getMinute()) {
            // Reiniciar a todos
            System.out.println("[PlayerTimeLimit] Reiniciando tiempos diarios a todos los jugadores...");

            for (ServerPlayerEntity jugador : server.getPlayerManager().getPlayerList()) {
                PlayerTimeDataManager.resetTime(jugador.getUuid());
                jugador.sendMessage(Text.of("♻ Tu tiempo ha sido reiniciado."), false);
                resetAdvertencias(jugador.getUuid());
            }

            // También reiniciamos jugadores offline (persistencia)
            PlayerTimeDataManager.resetAll();
            PlayerTimeDataManager.save(); // fuerza guardado tras el reinicio

            ultimaFechaReinicio = fechaHoy;
        }
    }

    public static void resetAdvertencias(UUID uuid) {
        advertenciasEnviadas.remove(uuid);
    }
}
