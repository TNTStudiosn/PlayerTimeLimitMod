package com.TNTStudios.playertimelimit.events;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerJoinQuitEvents {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            // Cargar o inicializar tiempo
            PlayerTimeDataManager.onPlayerJoin(player);

            // Verificar si debe ser kickeado
            if (PlayerTimeDataManager.shouldKick(player.getUuid())) {
                player.networkHandler.disconnect(Text.of(PLTConfig.mensajes.tiempoAgotado));
            } else {
                // Enviar mensaje de bienvenida personalizado
                int tiempo = PlayerTimeDataManager.getTime(player.getUuid());
                String mensaje = PLTConfig.mensajes.bienvenida.replace("%tiempo%", String.valueOf(tiempo));
                player.sendMessage(Text.of(mensaje), false);
            }
        });

    }
}
