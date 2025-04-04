package com.TNTStudios.playertimelimit.command;

import com.TNTStudios.playertimelimit.config.PLTConfig;
import com.TNTStudios.playertimelimit.data.PlayerTimeDataManager;
import com.TNTStudios.playertimelimit.scheduler.TimeCountdownTicker;
import com.TNTStudios.playertimelimit.util.OfflinePlayerUtil;
import com.TNTStudios.playertimelimit.util.PLTPermissionUtil;
import com.TNTStudios.playertimelimit.util.PLTTextUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

public class PLTCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("plt")
                .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.admin"))

                // /plt check <jugador>
                .then(CommandManager.literal("check")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.check"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String nombre = StringArgumentType.getString(ctx, "nombre");
                                    Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                    if (uuidOpt.isEmpty()) {
                                        ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                        return 0;
                                    }

                                    int tiempo = PlayerTimeDataManager.getTime(uuidOpt.get());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("⏳ Tiempo restante de " + nombre + ": " + tiempo + " segundos"), false);
                                    return 1;
                                })))

                // /plt resettime <jugador>
                .then(CommandManager.literal("resettime")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.resettime"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String nombre = StringArgumentType.getString(ctx, "nombre");
                                    Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                    if (uuidOpt.isEmpty()) {
                                        ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                        return 0;
                                    }
                                    UUID uuid = uuidOpt.get();
                                    PlayerTimeDataManager.resetTime(uuid);
                                    TimeCountdownTicker.resetAdvertencias(uuid);
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("⏹ Tiempo reiniciado para " + nombre), false);

                                    ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                    if (target != null) {
                                        PLTTextUtils.sendMessage(target, PLTConfig.mensajes.tiempoRestablecido, PlayerTimeDataManager.getTime(uuid));
                                    }

                                    return 1;
                                })))

                // /plt addtime <jugador> <segundos>
                .then(CommandManager.literal("addtime")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.addtime"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(CommandManager.argument("segundos", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String nombre = StringArgumentType.getString(ctx, "nombre");
                                            int secs = IntegerArgumentType.getInteger(ctx, "segundos");
                                            Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                            if (uuidOpt.isEmpty()) {
                                                ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                                return 0;
                                            }
                                            UUID uuid = uuidOpt.get();
                                            PlayerTimeDataManager.addTime(uuid, secs);
                                            ctx.getSource().sendFeedback(() ->
                                                    Text.of("✅ Se agregaron " + secs + " segundos a " + nombre), false);

                                            ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                            if (target != null) {
                                                PLTTextUtils.sendMessage(target, PLTConfig.mensajes.tiempoAgregado, PlayerTimeDataManager.getTime(uuid));
                                            }
                                            return 1;
                                        }))))

                // /plt removetime <jugador> <segundos>
                .then(CommandManager.literal("removetime")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.removetime"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(CommandManager.argument("segundos", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String nombre = StringArgumentType.getString(ctx, "nombre");
                                            int secs = IntegerArgumentType.getInteger(ctx, "segundos");
                                            Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                            if (uuidOpt.isEmpty()) {
                                                ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                                return 0;
                                            }
                                            UUID uuid = uuidOpt.get();
                                            PlayerTimeDataManager.removeTime(uuid, secs);
                                            ctx.getSource().sendFeedback(() ->
                                                    Text.of("⚠ Se eliminaron " + secs + " segundos a " + nombre), false);

                                            ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                            if (target != null) {
                                                PLTTextUtils.sendMessage(target, PLTConfig.mensajes.tiempoRemovido, PlayerTimeDataManager.getTime(uuid));
                                            }
                                            return 1;
                                        }))))

                // /plt pausar <jugador>
                .then(CommandManager.literal("pausar")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.pausar"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String nombre = StringArgumentType.getString(ctx, "nombre");
                                    Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                    if (uuidOpt.isEmpty()) {
                                        ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                        return 0;
                                    }
                                    PlayerTimeDataManager.pauseTime(uuidOpt.get());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("⏸ Tiempo pausado para " + nombre), false);
                                    return 1;
                                })))

                // /plt reanudar <jugador>
                .then(CommandManager.literal("reanudar")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.reanudar"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String nombre = StringArgumentType.getString(ctx, "nombre");
                                    Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                    if (uuidOpt.isEmpty()) {
                                        ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                        return 0;
                                    }
                                    PlayerTimeDataManager.resumeTime(uuidOpt.get());
                                    ctx.getSource().sendFeedback(() ->
                                            Text.of("▶ Tiempo reanudado para " + nombre), false);
                                    return 1;
                                })))

                // /plt reload
                .then(CommandManager.literal("reload")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.reload"))
                        .executes(ctx -> {
                            PLTConfig.loadConfig();
                            TimeCountdownTicker.clearUltimaFechaReinicio();
                            ctx.getSource().sendFeedback(() -> Text.of("♻ Configuración recargada"), false);
                            return 1;
                        }))

                // /plt info <jugador>
                .then(CommandManager.literal("info")
                        .requires(source -> PLTPermissionUtil.has(source, "PLimitTime.command.check"))
                        .then(CommandManager.argument("nombre", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (String name : OfflinePlayerUtil.getAllKnownNames()) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String nombre = StringArgumentType.getString(ctx, "nombre");
                                    Optional<UUID> uuidOpt = OfflinePlayerUtil.getUUIDByName(nombre);
                                    if (uuidOpt.isEmpty()) {
                                        ctx.getSource().sendError(Text.of("❌ Jugador no encontrado: " + nombre));
                                        return 0;
                                    }
                                    UUID uuid = uuidOpt.get();
                                    int tiempo = PlayerTimeDataManager.getTime(uuid);
                                    boolean pausado = PlayerTimeDataManager.isPaused(uuid);
                                    boolean agotado = PlayerTimeDataManager.shouldKick(uuid);

                                    ctx.getSource().sendFeedback(() -> Text.of(String.format(
                                            "📄 Info de %s:\n⏳ Tiempo restante: %d segundos\n⏸ Pausado: %s\n❌ Tiempo agotado: %s",
                                            nombre, tiempo, pausado, agotado
                                    )), false);

                                    return 1;
                                })))
        );
    }
}
