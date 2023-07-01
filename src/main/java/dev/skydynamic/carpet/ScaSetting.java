package dev.skydynamic.carpet;

import static dev.skydynamic.carpet.settings.ScaRuleCategory.*;

import carpet.api.settings.Rule;

public class ScaSetting{

    @Rule(categories = {SCA, FEATURE, SURVIVAL})
    public static boolean scheduledRandomTickCactus = false;

    @Rule(
        options = {"bone_block", "wither_skeleton_skull", "note_block", "OFF"},
        categories = {SCA, SCA_CHUNKLOADER}
        )
    public static String noteBlockChunkLoader = "OFF";

    @Rule(
        options = {"bone_block", "bedrock", "all", "OFF"},
        categories = {SCA, FEATURE, SCA_CHUNKLOADER}
        )
    public static String pistonBlockChunkLoader = "OFF";

    @Rule(
        categories = {SCA, FEATURE, SCA_CHUNKLOADER}
    )
    public static boolean pearlTickets = false;

    @Rule(categories = {SCA, FEATURE, SURVIVAL})
    public static boolean softDeepslate = false;

}