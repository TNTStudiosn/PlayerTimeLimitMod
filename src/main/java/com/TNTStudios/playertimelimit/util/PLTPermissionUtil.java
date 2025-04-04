package com.TNTStudios.playertimelimit.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PLTPermissionUtil {

    private static LuckPerms luckPerms = null;
    private static boolean initialized = false;

    /**
     * Verifica si el jugador tiene un permiso específico.
     */
    public static boolean has(ServerCommandSource source, String node) {
        if (source.hasPermissionLevel(4)) return true;

        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            return false;
        }

        if (!initialized) {
            tryInitLuckPerms();
        }

        if (luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUuid());
            if (user != null) {
                return user.getCachedData().getPermissionData(QueryOptions.defaultContextualOptions()).checkPermission(node).asBoolean();
            }
        }

        // Fallback si no hay LuckPerms: solo operadores
        return false;
    }

    /**
     * Intenta inicializar LuckPerms si está presente.
     */
    private static void tryInitLuckPerms() {
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            // LuckPerms no está disponible
            luckPerms = null;
        }
        initialized = true;
    }
}
