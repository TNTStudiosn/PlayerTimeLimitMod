package com.TNTStudios.playertimelimit.networking;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarHandler {

    private static final Map<UUID, ServerBossBar> barras = new HashMap<>();
    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                updateBossBars(server);
            }
        });
    }

    private static void updateBossBars(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            int time = PlayerTimeDataManager.getTime(uuid);
            boolean paused = PlayerTimeDataManager.isPaused(uuid);

            ServerBossBar bar = barras.get(uuid);
            if (bar == null) {
                bar = new ServerBossBar(
                        Text.of("Tiempo restante"),
                        getColorFromConfig(),
                        BossBar.Style.PROGRESS
                );
                bar.setVisible(true);
                barras.put(uuid, bar);
                bar.addPlayer(player);
            }

            if (paused) {
                bar.setName(Text.of("⏸ Tiempo pausado"));
                bar.setColor(BossBar.Color.WHITE); // Color neutro para pausa
                bar.setPercent(1.0f);
                continue;
            }

            int horas = time / 3600;
            int minutos = (time % 3600) / 60;
            int segundos = time % 60;

            String msg = PLTConfig.bossbar.message
                    .replace("%horas%", String.valueOf(horas))
                    .replace("%minutos%", String.valueOf(minutos))
                    .replace("%segundos%", String.valueOf(segundos));

            float porcentaje = Math.min(1f, Math.max(0f, (float) time / PLTConfig.tiempoPorDefecto));
            bar.setName(Text.of(msg));
            bar.setColor(getColorFromConfig());
            bar.setPercent(porcentaje);

            if (!bar.getPlayers().contains(player)) {
                bar.addPlayer(player);
            }
        }
    }

    private static BossBar.Color getColorFromConfig() {
        try {
            return BossBar.Color.byName(PLTConfig.bossbar.color.toLowerCase());
        } catch (Exception e) {
            return BossBar.Color.WHITE;
        }
    }
}
