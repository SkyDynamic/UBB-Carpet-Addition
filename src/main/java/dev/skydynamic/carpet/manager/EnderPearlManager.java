package dev.skydynamic.carpet.manager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static net.minecraft.server.level.TicketType.create;

public class EnderPearlManager {
    public static final TicketType<ChunkPos> ENDER_PEARL = create(
        "ender_pearl", Comparator.comparingLong(ChunkPos::toLong), 40
    );

    private static final HashMap<ServerPlayer, EnderPearlManager> pearlManagers = new HashMap<>();

    private final Set<ThrownEnderpearl> enderPearls = new HashSet<>();

    public static EnderPearlManager getPearlManager(ServerPlayer player) {
        if (!pearlManagers.containsKey(player)) {
            pearlManagers.put(player, new EnderPearlManager());
        }
        return pearlManagers.get(player);
    }

    public void registerEnderPearl(ThrownEnderpearl enderPearl) {
		this.enderPearls.add(enderPearl);
	}

    public void deregisterEnderPearl(ThrownEnderpearl enderPearl) {
		this.enderPearls.remove(enderPearl);
	}

    public Set<ThrownEnderpearl> getEnderPearls() {
        return this.enderPearls;
    }

    public long registerAndUpdateEnderPearlTicket(ThrownEnderpearl enderPearl) {
        if (enderPearl.level() instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = enderPearl.chunkPosition();
            this.registerEnderPearl(enderPearl);
            serverLevel.resetEmptyTime();
            return placeEnderPearlTicket(serverLevel, chunkPos);
        } else {
            return 0L;
        }
    }

    public static long placeEnderPearlTicket(ServerLevel level, ChunkPos pos) {
        level.getChunkSource().addRegionTicket(ENDER_PEARL, pos, 2, pos);
        return ENDER_PEARL.timeout();
    }
}
