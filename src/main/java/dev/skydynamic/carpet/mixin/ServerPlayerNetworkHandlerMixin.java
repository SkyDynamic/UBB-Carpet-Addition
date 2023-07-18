package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.ScaSetting;
import dev.skydynamic.carpet.command.TpaManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Shadow private double lastTickX;

    @Shadow private double lastTickY;

    @Shadow private double lastTickZ;

    @Inject(method = "tick", at = @At("HEAD"))
    void inj(CallbackInfo ci) {
        if (ScaSetting.commandTpaTeleportWaits <= 0)return;
        if (lastTickX != player.getX()
                || lastTickY != player.getY()
                || lastTickZ != player.getZ()
        ) {
           TpaManager.invokePlayerMoveCallback(player);
        }
    }
}
