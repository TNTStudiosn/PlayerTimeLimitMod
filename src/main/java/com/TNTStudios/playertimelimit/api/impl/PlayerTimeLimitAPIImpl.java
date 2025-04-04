package com.TNTStudios.playertimelimit.api.impl;

import com.TNTStudios.playertimelimit.api.PlayerTimeLimitAPI;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;

import java.util.UUID;

public class PlayerTimeLimitAPIImpl implements PlayerTimeLimitAPI {
    @Override
    public int getTime(UUID uuid) {
        return PlayerTimeDataManager.getTime(uuid);
    }

    @Override
    public void addTime(UUID uuid, int seconds) {
        PlayerTimeDataManager.addTime(uuid, seconds);
    }

    @Override
    public void removeTime(UUID uuid, int seconds) {
        PlayerTimeDataManager.removeTime(uuid, seconds);
    }

    @Override
    public void resetTime(UUID uuid) {
        PlayerTimeDataManager.resetTime(uuid);
    }

    @Override
    public boolean isPaused(UUID uuid) {
        return PlayerTimeDataManager.isPaused(uuid);
    }

    @Override
    public void pause(UUID uuid) {
        PlayerTimeDataManager.pauseTime(uuid);
    }

    @Override
    public void resume(UUID uuid) {
        PlayerTimeDataManager.resumeTime(uuid);
    }
}
