package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.ScaSetting;
import dev.skydynamic.carpet.function.BlockChunkLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Inject(at = @At("HEAD"), method = "playNote")
    private void playNoteMixin(
        Entity entity, BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (Objects.equals(ScaSetting.noteBlockChunkLoader, "note_block") && !level.isClientSide()) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            ((ServerLevel) level).getChunkSource().addRegionTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
        }
        if (Objects.equals(ScaSetting.noteBlockChunkLoader, "bone_block") && !level.isClientSide()) {
            BlockState noteBlock = level.getBlockState(blockPos.above());
            if (Objects.equals(ScaSetting.noteBlockChunkLoader, "bone_block") && noteBlock.is(Blocks.BONE_BLOCK)) {
                ChunkPos chunkPos = new ChunkPos(blockPos.above());
                ((ServerLevel) level).getChunkSource().addRegionTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
            }
        }
        if (Objects.equals(ScaSetting.noteBlockChunkLoader, "wither_skeleton_skull") && !level.isClientSide()) {
            BlockState noteBlock = level.getBlockState(blockPos.below());
            if (Objects.equals(ScaSetting.noteBlockChunkLoader, "wither_skeleton_skull") && (noteBlock.is(Blocks.WITHER_SKELETON_SKULL))
                || (noteBlock.is(Blocks.WITHER_SKELETON_WALL_SKULL))) {
                ChunkPos chunkPos = new ChunkPos(blockPos.below());
                ((ServerLevel) level).getChunkSource().addRegionTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
            }
        }
    }
}