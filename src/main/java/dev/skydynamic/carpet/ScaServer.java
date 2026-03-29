package dev.skydynamic.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import dev.skydynamic.carpet.api.tools.text.ComponentTranslate;
import dev.skydynamic.carpet.utils.translate.TranslateUtil;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ScaServer implements CarpetExtension, ModInitializer {

    public static MinecraftServer minecraftServer;
    public static String MOD_ID = "Carpet SD Addition";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public String get_version() {

        return "1.0.7";

    }

    public String version() {
        return "carpet-SD-addition";
    }

    public static void loadExtension() {
        CarpetServer.manageExtension(new ScaServer());
    }

    @Override
    public void onInitialize() {
        ScaServer.loadExtension();
    }

    @Override
    public void onGameStarted() {
        LOGGER.info("{} v{}载入成功", MOD_ID, get_version());
        LOGGER.info("开源链接：https://github.com/SkyDynamic/SkyDynamic-Carpet-Addition");
        CarpetServer.settingsManager.parseSettingsClass(ScaSetting.class);

    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        minecraftServer = server;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        TranslateUtil.handleResourceReload(lang);
        return ComponentTranslate.getTranslationFromResourcePath(lang);
    }
}