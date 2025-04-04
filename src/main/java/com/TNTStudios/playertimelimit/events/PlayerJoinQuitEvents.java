package com.TNTStudios.playertimelimit.events;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import com.TNTStudios.playertimelimit.networking.BossBarHandler;
import com.TNTStudios.playertimelimit.util.PLTTextUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerJoinQuitEvents {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            BossBarHandler.ensureBossBarFor(player);

            PlayerTimeDataManager.onPlayerJoin(player);

            if (PlayerTimeDataManager.shouldKick(player.getUuid())) {
                player.networkHandler.disconnect(Text.of(PLTConfig.mensajes.tiempoAgotado));
            } else {
                int tiempo = PlayerTimeDataManager.getTime(player.getUuid());
                PLTTextUtils.sendMessage(player, PLTConfig.mensajes.bienvenida, tiempo);
            }
        });

    }
}
