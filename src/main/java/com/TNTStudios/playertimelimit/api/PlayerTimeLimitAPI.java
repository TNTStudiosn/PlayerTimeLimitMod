package com.TNTStudios.playertimelimit.api;

import java.util.UUID;

public interface PlayerTimeLimitAPI {
    int getTime(UUID uuid);
    void addTime(UUID uuid, int seconds);
    void removeTime(UUID uuid, int seconds);
    void resetTime(UUID uuid);
    boolean isPaused(UUID uuid);
    void pause(UUID uuid);
    void resume(UUID uuid);
}
