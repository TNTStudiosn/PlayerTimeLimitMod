package com.TNTStudios.playertimelimit.data;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTimeDataManager {

    private static final File DATA_FILE = new File("config/playertimes.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<UUID, PlayerData> playerTimes = new ConcurrentHashMap<>();


    public static void resetTime(UUID uuid) {
        PlayerData data = getOrCreate(uuid);
        data.timeRemaining = PLTConfig.tiempoPorDefecto;
        data.outOfTime = false;
        save();
    }

    public static void resetAll() {
        for (UUID uuid : playerTimes.keySet()) {
            resetTime(uuid);
        }
    }

    public static void addTime(UUID uuid, int seconds) {
        PlayerData data = getOrCreate(uuid);
        data.timeRemaining += seconds;
        save();
    }

    public static void removeTime(UUID uuid, int seconds) {
        PlayerData data = getOrCreate(uuid);
        data.timeRemaining = Math.max(0, data.timeRemaining - seconds);
        save();
    }

    public static void pauseTime(UUID uuid) {
        getOrCreate(uuid).paused = true;
        save();
    }

    public static void resumeTime(UUID uuid) {
        getOrCreate(uuid).paused = false;
        save();
    }

    public static boolean isPaused(UUID uuid) {
        return getOrCreate(uuid).paused;
    }

    public static int getTime(UUID uuid) {
        return getOrCreate(uuid).timeRemaining;
    }

    public static void setTime(UUID uuid, int seconds) {
        getOrCreate(uuid).timeRemaining = seconds;
        save();
    }

    public static void markOutOfTime(UUID uuid) {
        getOrCreate(uuid).outOfTime = true;
    }

    public static Set<UUID> getAllUUIDs() {
        return playerTimes.keySet();
    }

    public static boolean shouldKick(UUID uuid) {
        return getOrCreate(uuid).outOfTime;
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        String name = player.getName().getString();
        PlayerData data = playerTimes.computeIfAbsent(uuid, id -> new PlayerData(PLTConfig.tiempoPorDefecto, name));
        data.name = name; // actualiza el nombre si cambió
    }

    public static void load() {
        if (!DATA_FILE.exists()) return;
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type type = new TypeToken<Map<UUID, PlayerData>>() {}.getType();
            Map<UUID, PlayerData> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                playerTimes.clear();
                playerTimes.putAll(loaded);
            }
        } catch (Exception e) {
            System.err.println("[PlayerTimeLimit] Error loading time data: " + e.getMessage());
        }
    }

    public synchronized static void save() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(playerTimes, writer);
        } catch (Exception e) {
            System.err.println("[PlayerTimeLimit] Error saving time data: " + e.getMessage());
        }
    }

    private static PlayerData getOrCreate(UUID uuid) {
        return playerTimes.computeIfAbsent(uuid, id -> new PlayerData(PLTConfig.tiempoPorDefecto, "Desconocido"));
    }

    private static class PlayerData {
        int timeRemaining;
        boolean paused = false;
        boolean outOfTime = false;
        String name;

        PlayerData(int timeRemaining, String name) {
            this.timeRemaining = timeRemaining;
            this.name = name;
        }
    }
}
