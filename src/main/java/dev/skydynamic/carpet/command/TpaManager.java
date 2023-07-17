package dev.skydynamic.carpet.command;

import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.skydynamic.carpet.ScaSetting;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class TpaManager {

    //{TargetPlayer : TeleportData}
    private static final ConcurrentHashMap<String, TeleportRequest> teleportDataHashMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TeleportRequest> onPlayerMoveCallbackMap = new ConcurrentHashMap<>();


    private static final LiteralArgumentBuilder<ServerCommandSource> tpaCommand = literal("tpa")
            .requires(src -> src.hasPermissionLevel(2) || ScaSetting.commandTpa)
            .then(argument("player", EntityArgumentType.player())
                    .executes(it -> tpaCommand(it.getSource(), EntityArgumentType.getPlayer(it, "player")))
            );

    private static final LiteralArgumentBuilder<ServerCommandSource> tpAcceptCommand = literal("tpaccept")
            .requires(src -> src.hasPermissionLevel(2) || ScaSetting.commandTpa)
            .executes(it -> {
                tpAcceptCommand(it.getSource(), true);
                return 0;
            });
    private static final LiteralArgumentBuilder<ServerCommandSource> tpDenyCommand = literal("tpdeny")
            .requires(src -> src.hasPermissionLevel(2) || ScaSetting.commandTpa)
            .executes(it -> {
                tpAcceptCommand(it.getSource(), false);
                return 0;
            });

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(tpaCommand);
        dispatcher.register(tpAcceptCommand);
        dispatcher.register(tpDenyCommand);
    }

    private static void tpAcceptCommand(ServerCommandSource commandSource, boolean accept) {
        ServerPlayerEntity player = null;
        //#if MC>=11900
        //$$ player = commandSource.getPlayer();
        //#else
        try {
            player = commandSource.getPlayer();
        } catch (CommandSyntaxException ignored) {
        } //this exception will never thrown
        //#endif
        if (player == null) return;
        var playerName = player.getGameProfile().getName();
        if (playerName.isBlank()) return;
        if (teleportDataHashMap.containsKey(playerName)) {
            var data = teleportDataHashMap.get(playerName);
            boolean requestExpired = System.currentTimeMillis() > data.timeout;
            if (requestExpired) {
                teleportDataHashMap.remove(playerName);
                Messenger.m(commandSource, "y No teleport request present to accept or deny.");
                return;
            }
            var srcPlayerName = data.sourcePlayer.getGameProfile().getName();
            if (accept) {
                if (ScaSetting.commandTpaTeleportWaits <= 0) {
                    Messenger.m(commandSource, "l Accepted teleport request from %s.".formatted(srcPlayerName));
                    Messenger.m(data.sourcePlayer, "l Player %s accepted your teleport request.".formatted(playerName));
                    processTeleportAccept(data);
                    teleportDataHashMap.remove(playerName);
                }else {
                    Messenger.m(commandSource, "l Accepted teleport request from %s, teleport will start in %d seconds.".formatted(srcPlayerName, ScaSetting.commandTpaTimeout));
                    Messenger.m(data.sourcePlayer, "l Player %s accepted your teleport request, teleport will start in %d seconds.".formatted(playerName, ScaSetting.commandTpaTimeout));
                    data.timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            onPlayerMoveCallbackMap.remove(playerName);
                            processTeleportAccept(data);
                        }
                    }, ScaSetting.commandTpaTimeout * 1000L);
                    onPlayerMoveCallbackMap.put(data.sourcePlayer.getGameProfile().getName(), data);
                    teleportDataHashMap.remove(playerName);
                }
            } else {
                Messenger.m(commandSource, "l Denied teleport request from %s.".formatted(srcPlayerName));
                Messenger.m(data.sourcePlayer, "r Player %s denied your teleport request.".formatted(playerName));
                teleportDataHashMap.remove(playerName);
            }
        } else {
            Messenger.m(commandSource, "y No teleport request present to accept or deny.");
        }
    }

    private static void processTeleportAccept(TeleportRequest teleportRequest) {
        var srcPlayer = teleportRequest.sourcePlayer;
        var destinationPlayerBlockPos = teleportRequest.destinationPlayer.getBlockPos();
        //#if MC >= 12000
        //$$ var destinationPlayerWorld = teleportData.destinationPlayer.getServerWorld();
        //#else
        var destinationPlayerWorld = teleportRequest.destinationPlayer.getWorld();
        //#endif
        float yaw = teleportRequest.destinationPlayer.getYaw();
        float pitch = teleportRequest.destinationPlayer.getPitch();
        srcPlayer.teleport(destinationPlayerWorld,
                destinationPlayerBlockPos.getX(),
                destinationPlayerBlockPos.getY(),
                destinationPlayerBlockPos.getZ(),
                yaw,
                pitch);
    }

    private static int tpaCommand(ServerCommandSource commandSource, ServerPlayerEntity targetPlayer) {
        ServerPlayerEntity srcPlayer = null;
        //#if MC>=11900
        //$$ srcPlayer = commandSource.getPlayer();
        //#else
        try {
            srcPlayer = commandSource.getPlayer();
        } catch (CommandSyntaxException ignored) {
        } //this exception will never thrown
        //#endif
        if (srcPlayer == null) return 1;
        var srcPlayerName = srcPlayer.getGameProfile().getName();
        var targetPlayerName = targetPlayer.getGameProfile().getName();
        Messenger.m(commandSource, "l Sending teleport request to player %s, this request will be expired after %d seconds.".formatted(targetPlayerName, ScaSetting.commandTpaTimeout));
        teleportDataHashMap.put(targetPlayerName, new TeleportRequest(targetPlayer, srcPlayer, System.currentTimeMillis() + ScaSetting.commandTpaTimeout * 1000L));
        Messenger.m(targetPlayer, "w %s wants to teleport to your location, using ".formatted(srcPlayerName),
                "lb /tpaccept",
                "^w click to accept",
                "!/tpaccept",
                "w  to accept and ",
                "rb /tpdeny",
                "^w click to deny",
                "!/tpdeny",
                "w  to reject,this request will be expired after %d seconds.".formatted(ScaSetting.commandTpaTimeout));
        return 0;
    }

    public static class TeleportRequest {
        ServerPlayerEntity destinationPlayer;
        ServerPlayerEntity sourcePlayer;
        long timeout;
        Timer timer = new Timer("TeleportTimer");

        public TeleportRequest(ServerPlayerEntity destinationPlayer, ServerPlayerEntity sourcePlayer, long timeout) {
            this.destinationPlayer = destinationPlayer;
            this.sourcePlayer = sourcePlayer;
            this.timeout = timeout;
        }
    }

    public static void invokePlayerMoveCallback(ServerPlayerEntity player){
        synchronized (onPlayerMoveCallbackMap) {
            var playerName = player.getGameProfile().getName();
            var request = onPlayerMoveCallbackMap.get(playerName);
            if (request != null) {
                onPlayerMoveCallbackMap.remove(playerName);
                Messenger.m(request.destinationPlayer,"y Teleport cancelled.");
                Messenger.m(request.sourcePlayer,"y Teleport cancelled.");
                request.timer.cancel();
            }
        }
    }
}
