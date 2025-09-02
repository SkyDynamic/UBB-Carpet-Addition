package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.ScaSetting;
import dev.skydynamic.carpet.function.BlockChunkLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
    @Inject(method = "triggerEvent", at = @At("HEAD"))
    private void onSyncedBlockEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        if((Objects.equals(ScaSetting.pistonBlockChunkLoader, "bone_block") || Objects.equals(ScaSetting.pistonBlockChunkLoader, "all")) && !level.isClientSide()) {
            Direction direction = blockState.getValue(DirectionalBlock.FACING);
            BlockState pistonBlock = level.getBlockState(blockPos.above());
            if ((Objects.equals(ScaSetting.pistonBlockChunkLoader, "bone_block") || Objects.equals(ScaSetting.pistonBlockChunkLoader, "all"))
                && pistonBlock.is(Blocks.BONE_BLOCK)) {
                ChunkPos chunkPos = new ChunkPos(blockPos.relative(direction));
                ((ServerLevel) level).getChunkSource().addRegionTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
            }
        }

        if((Objects.equals(ScaSetting.pistonBlockChunkLoader, "bedrock") || Objects.equals(ScaSetting.pistonBlockChunkLoader, "all")) && !level.isClientSide()) {
            Direction direction = blockState.getValue(DirectionalBlock.FACING);
            BlockState pistonBlock = level.getBlockState(blockPos.below());
            if ((Objects.equals(ScaSetting.pistonBlockChunkLoader, "bedrock") || Objects.equals(ScaSetting.pistonBlockChunkLoader, "all"))
                && pistonBlock.is(Blocks.BEDROCK)) {
                ChunkPos chunkPos = new ChunkPos(blockPos.relative(direction));
                ((ServerLevel) level).getChunkSource().addRegionTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
            }
        }
    }
}
