package com.TNTStudios.playertimelimit;

import com.TNTStudios.playertimelimit.api.PlayerTimeLimitAPI;
import com.TNTStudios.playertimelimit.api.impl.PlayerTimeLimitAPIImpl;
import com.TNTStudios.playertimelimit.command.PLTCommand;
import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import com.TNTStudios.playertimelimit.events.PlayerJoinQuitEvents;
import com.TNTStudios.playertimelimit.networking.BossBarHandler;
import com.TNTStudios.playertimelimit.scheduler.TimeCountdownTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Playertimelimit implements ModInitializer {

    public static final String MOD_ID = "playertimelimit";

    private static PlayerTimeLimitAPI api;

    public static PlayerTimeLimitAPI getAPI() {
        return api;
    }

    @Override
    public void onInitialize() {
        System.out.println("[PlayerTimeLimit] Inicializando...");

        PLTConfig.loadConfig();
        PlayerTimeDataManager.load();

        PlayerJoinQuitEvents.register();
        BossBarHandler.register();
        TimeCountdownTicker.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PLTCommand.register(dispatcher, registryAccess);
        });

        api = new PlayerTimeLimitAPIImpl();

        System.out.println("[PlayerTimeLimit] Mod PlayerTimeLimit activo.");
    }
}

