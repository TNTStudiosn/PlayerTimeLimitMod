package com.TNTStudios.playertimelimit.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OfflinePlayerUtil {

    private static final File DATA_FILE = new File("config/playertimes.json");
    private static final Gson gson = new Gson();
    private static final Map<UUID, String> nameCache = new ConcurrentHashMap<>();

    static {
        load();
    }

    public static void load() {
        if (!DATA_FILE.exists()) return;
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type type = new TypeToken<Map<UUID, Map<String, Object>>>(){}.getType();
            Map<UUID, Map<String, Object>> rawData = gson.fromJson(reader, type);
            rawData.forEach((uuid, data) -> {
                if (data.containsKey("name")) {
                    nameCache.put(uuid, (String) data.get("name"));
                }
            });
        } catch (Exception e) {
            System.err.println("[PlayerTimeLimit] Error loading offline player names: " + e.getMessage());
        }
    }

    public static List<String> getAllKnownNames() {
        return new ArrayList<>(nameCache.values());
    }

    public static Optional<UUID> getUUIDByName(String name) {
        return nameCache.entrySet().stream()
                .filter(e -> e.getValue().equalsIgnoreCase(name))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
