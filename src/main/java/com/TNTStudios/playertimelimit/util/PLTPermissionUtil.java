package com.TNTStudios.playertimelimit.util;

import net.minecraft.server.command.ServerCommandSource;

public class PLTPermissionUtil {

    public static boolean has(ServerCommandSource source, String node) {
        // LuckPerms u otra lógica: por ahora, caemos en operador
        return source.hasPermissionLevel(4) || source.hasPermission(node);
    }
}
