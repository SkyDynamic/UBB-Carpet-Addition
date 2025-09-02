package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.ScaSetting;

import dev.skydynamic.carpet.manager.EnderPearlManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin {
    @Unique
    private long ticketTimer = 0L;

    @Inject(
        method = "onHit",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ThrownEnderpearl;discard()V"
        )
    )
    private void onHitDiscard(CallbackInfo ci) {
        ThrownEnderpearl thrownEnderpearl = (ThrownEnderpearl) (Object) this;
        if (thrownEnderpearl.getOwner() instanceof ServerPlayer serverPlayer) {
            EnderPearlManager.getPearlManager(serverPlayer).deregisterEnderPearl(thrownEnderpearl);
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void onTicket(CallbackInfo ci) {
        ThrownEnderpearl pearl = (ThrownEnderpearl) (Object) this;

        Level world = pearl.level();

        int i = SectionPos.blockToSectionCoord(pearl.position().x());
		int j = SectionPos.blockToSectionCoord(pearl.position().z());

        Entity entity = pearl.getOwner();

        if (world instanceof ServerLevel && ScaSetting.pearlTickets && pearl.isAlive()) {
            BlockPos blockPos = BlockPos.containing(pearl.position());

            if ((--this.ticketTimer <= 0L || i != SectionPos.blockToSectionCoord(blockPos.getX()) || j != SectionPos.blockToSectionCoord(blockPos.getZ()))
				&& entity instanceof ServerPlayer serverPlayer) {
                EnderPearlManager enderPearlManager = EnderPearlManager.getPearlManager(serverPlayer);
                this.ticketTimer = enderPearlManager.registerAndUpdateEnderPearlTicket(pearl);
            }
        }
    }
}