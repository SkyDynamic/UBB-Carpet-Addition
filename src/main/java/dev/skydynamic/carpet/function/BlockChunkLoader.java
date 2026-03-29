package dev.skydynamic.carpet.function;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.TicketType;

public class BlockChunkLoader {
    public static final TicketType BLOCK_LOADER = Registry.register(
        BuiltInRegistries.TICKET_TYPE, "block_loader",new TicketType(300L, 14));
}