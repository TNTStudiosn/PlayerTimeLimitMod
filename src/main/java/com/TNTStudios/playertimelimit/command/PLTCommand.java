package com.TNTStudios.playertimelimit.command;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import com.TNTStudios.playertimelimit.scheduler.TimeCountdownTicker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PLTCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("plt")
                .requires(source -> source.hasPermissionLevel(2)) // acceso por defecto para operadores

                // /plt check <jugador>
                .then(CommandManager.literal("check")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("jugador", EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "jugador");
                                    int tiempo = PlayerTimeDataManager.getTime(target.getUuid());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("⏳ Tiempo restante de " + target.getName().getString() + ": " + tiempo + " segundos"), false);
                                    return 1;
                                })))

                // /plt resettime <jugador>
                .then(CommandManager.literal("resettime")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("jugador", EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "jugador");
                                    PlayerTimeDataManager.resetTime(target.getUuid());
                                    TimeCountdownTicker.resetAdvertencias(target.getUuid());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("⏹ Tiempo reiniciado para " + target.getName().getString()), false);
                                    return 1;
                                })))

                // /plt addtime <jugador> <segundos>
                .then(CommandManager.literal("addtime")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("jugador", EntityArgumentType.player())
                                .then(CommandManager.argument("segundos", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "jugador");
                                            int secs = IntegerArgumentType.getInteger(ctx, "segundos");
                                            PlayerTimeDataManager.addTime(target.getUuid(), secs);
                                            ctx.getSource().sendFeedback(() ->
                                                    Text.of("✅ Se agregaron " + secs + " segundos a " + target.getName().getString()), false);
                                            return 1;
                                        }))))

                // /plt removetime <jugador> <segundos>
                .then(CommandManager.literal("removetime")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("jugador", EntityArgumentType.player())
                                .then(CommandManager.argument("segundos", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "jugador");
                                            int secs = IntegerArgumentType.getInteger(ctx, "segundos");
                                            PlayerTimeDataManager.removeTime(target.getUuid(), secs);
                                            ctx.getSource().sendFeedback(() ->
                                                    Text.of("⚠ Se eliminaron " + secs + " segundos a " + target.getName().getString()), false);
                                            return 1;
                                        }))))

                // /plt pausar <jugador>
                .then(CommandManager.literal("pausar")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("jugador", EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "jugador");
                                    PlayerTimeDataManager.pauseTime(target.getUuid());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("⏸ Tiempo pausado para " + target.getName().getString()), false);
                                    return 1;
                                })))

                // /plt reanudar <jugador>
                .then(CommandManager.literal("reanudar")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("jugador", EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "jugador");
                                    PlayerTimeDataManager.resumeTime(target.getUuid());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("▶ Tiempo reanudado para " + target.getName().getString()), false);
                                    return 1;
                                })))

                // /plt reload
                .then(CommandManager.literal("reload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ctx -> {
                            PLTConfig.loadConfig();
                            ctx.getSource().sendFeedback(() -> Text.of("♻ Configuración recargada"), false);
                            return 1;
                        }))
        );
    }
}
