package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.manager.EnderPearlManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(
        method = "remove",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;unRide()V",
            shift = At.Shift.AFTER
        )
    )
    private void onRemove(ServerPlayer serverPlayer, CallbackInfo ci) {
        EnderPearlManager pearlManager = EnderPearlManager.getPearlManager(serverPlayer);
        for (ThrownEnderpearl pearl : pearlManager.getEnderPearls()) {
            pearl.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
            pearlManager.deregisterEnderPearl(pearl);
        }
    }
}
