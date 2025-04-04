package com.TNTStudios.playertimelimit;

import com.TNTStudios.playertimelimit.command.PLTCommand;
import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import com.TNTStudios.playertimelimit.events.PlayerJoinQuitEvents;
import com.TNTStudios.playertimelimit.networking.BossBarHandler;
import com.TNTStudios.playertimelimit.scheduler.TimeCountdownTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Playertimelimit implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("[PlayerTimeLimit] Inicializando...");

        // Cargar configuración y datos
        PLTConfig.loadConfig();
        PlayerTimeDataManager.load();

        // Registrar eventos
        PlayerJoinQuitEvents.register();
        BossBarHandler.register();
        TimeCountdownTicker.register();

        // Registrar comandos
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PLTCommand.register(dispatcher, registryAccess);
        });

        System.out.println("[PlayerTimeLimit] Mod PlayerTimeLimit activo.");
    }
}

