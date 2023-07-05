package dev.skydynamic.carpet;

import static dev.skydynamic.carpet.settings.ScaRuleCategory.*;

import carpet.settings.Rule;

public class ScaSetting{

    @Rule(
            desc = "Make cactus accepts scheduled tick as random tick",
            category = {SCA, FEATURE, SURVIVAL}
    )
    public static boolean scheduledRandomTickCactus = false;

    @Rule(
            options = {"bone_block", "wither_skeleton_skull", "note_block", "OFF"},
            category = {SCA, SCA_CHUNKLOADER},
            desc = "Load nearby 3x3 chunks for 15 seconds when a note block is triggered"
    )
    public static String noteBlockChunkLoader = "OFF";

    @Rule(
            options = {"bone_block", "bedrock", "all", "OFF"},
            category = {SCA, FEATURE, SCA_CHUNKLOADER},
            desc = "Load nearby 3x3 chunks for 15 seconds when a piston is triggered (Centered on the piston head)"
    )
    public static String pistonBlockChunkLoader = "OFF";

    @Rule(
            desc = "This mod allows Ender Pearl entity to selectively load chunks, so your Ender Pearl won't disappear in unloaded areas",
            category = {SCA, FEATURE, SCA_CHUNKLOADER}
    )
    public static boolean pearlTickets = false;

    @Rule(
            desc = "Change the hardness of deepslate to stone",
            category = {SCA, FEATURE, SURVIVAL}
    )
    public static boolean softDeepslate = false;

}