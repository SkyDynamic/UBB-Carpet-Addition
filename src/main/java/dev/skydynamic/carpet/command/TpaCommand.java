package dev.skydynamic.carpet.command;

import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
    private static LiteralArgumentBuilder<ServerCommandSource> tpaCommand = literal("tpa").then(
            argument("player", EntityArgumentType.player())
                    .requires(TpaCommand::requirePlayerAndEnable)
                    .executes(it -> tpaCommand(it.getSource(), EntityArgumentType.getPlayer(it, "player")))
    );

    private static LiteralArgumentBuilder<ServerCommandSource> tpAcceptCommand = literal("tpaccept")
            .requires(TpaCommand::requirePlayerAndEnable).executes(it -> {
                tpAcceptCommand(it.getSource(), true);
                return 0;
            });
    private static LiteralArgumentBuilder<ServerCommandSource> tpDenyCommand = literal("tpdeny")
            .requires(TpaCommand::requirePlayerAndEnable).executes(it -> {
                tpAcceptCommand(it.getSource(), false);
                return 0;
            });

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(tpaCommand);
        dispatcher.register(tpAcceptCommand);
        dispatcher.register(tpDenyCommand);
    }

    private static boolean requirePlayerAndEnable(ServerCommandSource src) {
        try {
            return src.getPlayer() != null && ScaSetting.commandTpa;
        } catch (Exception e) {
            return false;
        }
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
        assert player != null;
        var playerName = player.getGameProfile().getName();
        if (playerName.isBlank()) return;
        if (teleportDataHashMap.containsKey(playerName)) {
            var data = teleportDataHashMap.get(playerName);
            boolean requestExpired = System.currentTimeMillis() > data.timeout;
            if (requestExpired) {
                teleportDataHashMap.remove(playerName);
                Messenger.m(commandSource, "yb No teleport request present to accept or deny.");
                return;
            }
            var srcPlayerName = data.sourcePlayer.getGameProfile().getName();
            if (accept) {
                Messenger.m(commandSource, "lb Accepted teleport request from %s.".formatted(srcPlayerName));
                Messenger.m(data.sourcePlayer, "rb Player %s accepted your teleport request.".formatted(playerName));
                processTeleportAccept(data);
                teleportDataHashMap.remove(playerName);
            } else {
                Messenger.m(commandSource, "lb Denied teleport request from %s.".formatted(srcPlayerName));
                Messenger.m(data.sourcePlayer, "rb Player %s denied your teleport request.".formatted(playerName));
                teleportDataHashMap.remove(playerName);
            }
        } else {
            Messenger.m(commandSource, "yb No teleport request present to accept or deny.");
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
        assert srcPlayer != null;
        var srcPlayerName = srcPlayer.getGameProfile().getName();
        var targetPlayerName = targetPlayer.getGameProfile().getName();
        Messenger.m(commandSource, "lb Sending teleport request to player %s, this request will be expired after %d seconds.".formatted(srcPlayerName, ScaSetting.commandTpaTimeout));
        teleportDataHashMap.put(targetPlayerName, new TeleportData(targetPlayer, srcPlayer, System.currentTimeMillis() + ScaSetting.commandTpaTimeout));
        Messenger.m(targetPlayer, "w %s wants to teleport to your location, using ", "lb /tpaccept", "^w click to accept", "!/tpaccept", " to accept and ", "rb /tpdeny", "^w click to deny", "!/tpdeny", " to reject");
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
