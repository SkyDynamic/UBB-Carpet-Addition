package dev.skydynamic.carpet.function;


import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;

public class BlockChunkLoader {
    public static final TicketType<ChunkPos>
            BLOCK_LOADER = TicketType.create
            (
                    "block_loader", Comparator.comparingLong(ChunkPos::toLong),
                    300
            );
}