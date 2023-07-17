package dev.skydynamic.carpet.command;

import carpet.CarpetServer;
import carpet.settings.SettingsManager;
import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.skydynamic.carpet.ScaServer;
import dev.skydynamic.carpet.ScaSetting;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class TpaCommand {

    //{TargetPlayer : TeleportData}
    private static final ConcurrentHashMap<String, TeleportData> teleportDataHashMap = new ConcurrentHashMap<>();
    //5 min
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

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
                Messenger.m(commandSource, "l Accepted teleport request from %s.".formatted(srcPlayerName));
                Messenger.m(data.sourcePlayer, "l Player %s accepted your teleport request.".formatted(playerName));
                processTeleportAccept(data);
                teleportDataHashMap.remove(playerName);
            } else {
                Messenger.m(commandSource, "l Denied teleport request from %s.".formatted(srcPlayerName));
                Messenger.m(data.sourcePlayer, "r Player %s denied your teleport request.".formatted(playerName));
                teleportDataHashMap.remove(playerName);
            }
        } else {
            Messenger.m(commandSource, "y No teleport request present to accept or deny.");
        }
    }

    private static void processTeleportAccept(TeleportData teleportData) {
        var srcPlayer = teleportData.sourcePlayer;
        var destinationPlayerBlockPos = teleportData.destinationPlayer.getBlockPos();
        //#if MC >= 12000
        //$$ var destinationPlayerWorld = teleportData.destinationPlayer.getServerWorld();
        //#else
        var destinationPlayerWorld = teleportData.destinationPlayer.getWorld();
        //#endif
        float yaw = teleportData.destinationPlayer.getYaw();
        float pitch = teleportData.destinationPlayer.getPitch();
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
        teleportDataHashMap.put(targetPlayerName, new TeleportData(targetPlayer, srcPlayer, System.currentTimeMillis() + ScaSetting.commandTpaTimeout * 1000L));
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

    public static class TeleportData {
        ServerPlayerEntity destinationPlayer;
        ServerPlayerEntity sourcePlayer;
        long timeout;

        public TeleportData(ServerPlayerEntity destinationPlayer, ServerPlayerEntity sourcePlayer, long timeout) {
            this.destinationPlayer = destinationPlayer;
            this.sourcePlayer = sourcePlayer;
            this.timeout = timeout;
        }
    }
}
