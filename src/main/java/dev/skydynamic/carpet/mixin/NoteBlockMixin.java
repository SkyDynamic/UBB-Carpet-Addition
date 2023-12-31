package dev.skydynamic.carpet.mixin;

import dev.skydynamic.carpet.ScaSetting;
import dev.skydynamic.carpet.function.BlockChunkLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
//#if MC>=11900
//$$ import net.minecraft.block.BlockState;
//$$ import net.minecraft.entity.Entity;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Inject(at = @At("HEAD"), method = "playNote")
    private void playNoteMixin(
            //#if MC>=11900
            //$$ Entity entity,
            //#endif
            //#if MC>=11903
            //$$ BlockState blockState,
            //#endif
            World world, BlockPos pos, CallbackInfo info) {
        if (Objects.equals(ScaSetting.noteBlockChunkLoader, "note_block") && !world.isClient) {
            ChunkPos chunkPos = new ChunkPos(pos);
            ((ServerWorld) world).getChunkManager().addTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
        }
        if (Objects.equals(ScaSetting.noteBlockChunkLoader, "bone_block") && !world.isClient) {
            BlockState noteBlock = world.getBlockState(pos.up(1));
            if (Objects.equals(ScaSetting.noteBlockChunkLoader, "bone_block") && noteBlock.isOf(Blocks.BONE_BLOCK)) {
                ChunkPos chunkPos = new ChunkPos(pos.up(1));
                ((ServerWorld) world).getChunkManager().addTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
            }
        }
        if (Objects.equals(ScaSetting.noteBlockChunkLoader, "wither_skeleton_skull") && !world.isClient) {
            BlockState noteBlock = world.getBlockState(pos.up(1));
            if (Objects.equals(ScaSetting.noteBlockChunkLoader, "wither_skeleton_skull") && (noteBlock.isOf(Blocks.WITHER_SKELETON_SKULL)) || (noteBlock.isOf(Blocks.WITHER_SKELETON_WALL_SKULL))) {
                ChunkPos chunkPos = new ChunkPos(pos.up(1));
                ((ServerWorld) world).getChunkManager().addTicket(BlockChunkLoader.BLOCK_LOADER, chunkPos, 3, chunkPos);
            }
        }
    }
}