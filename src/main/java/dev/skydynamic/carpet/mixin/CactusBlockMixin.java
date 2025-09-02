package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.ScaSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin{
    @Shadow
    protected abstract void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource);

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"
            ),
            cancellable = true
    )
    private void scheduleTickMixinInvoke(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (ScaSetting.scheduledRandomTickCactus)
            ci.cancel();
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void scheduleTickMixinTail(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (ScaSetting.scheduledRandomTickCactus)
            this.randomTick(blockState, serverLevel, blockPos, randomSource);
    }
}