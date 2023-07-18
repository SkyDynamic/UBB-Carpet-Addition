package dev.skydynamic.carpet;

import dev.skydynamic.carpet.utils.recipes.CraftingRule;
import static dev.skydynamic.carpet.settings.ScaRuleCategory.*;
//#if MC>=11900
//$$ import carpet.api.settings.Rule;
//#else
import carpet.settings.Rule;
//#endif

public class ScaSetting{

    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, FEATURE, SURVIVAL}
            //#else
            category = {SCA, FEATURE, SURVIVAL},
            desc = "Make cactus accepts scheduled tick as random tick"
            //#endif
        )
    public static boolean scheduledRandomTickCactus = false;

    @Rule(
            options = {"bone_block", "wither_skeleton_skull", "note_block", "OFF"},
            //#if MC>=11900
            //$$ categories = {SCA, SCA_CHUNKLOADER}
            //#else
            category = {SCA, SCA_CHUNKLOADER},
            desc = "Load nearby 3x3 chunks for 15 seconds when a note block is triggered"
            //#endif
        )
    public static String noteBlockChunkLoader = "OFF";

    @Rule(
            options = {"bone_block", "bedrock", "all", "OFF"},
            //#if MC>=11900
            //$$ categories = {SCA, FEATURE, SCA_CHUNKLOADER}
            //#else
            category = {SCA, FEATURE, SCA_CHUNKLOADER},
            desc = "Load nearby 3x3 chunks for 15 seconds when a piston is triggered (Centered on the piston head)"
            //#endif
        )
    public static String pistonBlockChunkLoader = "OFF";

    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, FEATURE, SCA_CHUNKLOADER}
            //#else
            category = {SCA, FEATURE, SCA_CHUNKLOADER},
            desc = "This mod allows Ender Pearl entity to selectively load chunks, so your Ender Pearl won't disappear in unloaded areas"
            //#endif
    )
    public static boolean pearlTickets = false;

    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, FEATURE, SURVIVAL}
            //#else
            category = {SCA, FEATURE, SURVIVAL},
            desc = "Change the hardness of deepslate to stone"
            //#endif
        )
    public static boolean softDeepslate = false;

    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, CRAFTING, SURVIVAL}
            //#else
            category = {SCA, CRAFTING, SURVIVAL},
            desc = "Can crafting Nongfu Spring"
            //#endif
    )
    @CraftingRule(recipes = "nongfu_spring.json")
    public  static  boolean craftingNongfuSpring = false;


    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, CRAFTING, SURVIVAL}
            //#else
            category = {SCA, CRAFTING, FEATURE},
            desc = "Enable tpa command"
            //#endif
    )
    public static boolean commandTpa = false;

    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, TPA, FEATURE, SURVIVAL}
            //#else
            category = {SCA, TPA, FEATURE, SURVIVAL},
            desc = "Set tpa request expiration time in seconds"
            //#endif
    )
    public static int commandTpaTimeout = 5 * 60;

    @Rule(
            //#if MC>=11900
            //$$ categories = {SCA, TPA, FEATURE, SURVIVAL}
            //#else
            category = {SCA, TPA, FEATURE, SURVIVAL},
            desc = "Set the time in seconds to wait before teleport occurs"
            //#endif
    )
    public static int commandTpaTeleportWaits = 5;
}