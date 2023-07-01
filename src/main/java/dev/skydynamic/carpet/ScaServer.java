package dev.skydynamic.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.datafixers.util.Pair;
import dev.skydynamic.carpet.api.Function;
import dev.skydynamic.carpet.api.tools.text.ComponentTranslate;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScaServer implements CarpetExtension, ModInitializer {

    public static String MOD_ID = "Carpet SD Addition";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final List<Pair<Long, Function>> planFunction = new ArrayList<>();

    public String get_version() {
        return "1.0.0";
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

        LOGGER.info(MOD_ID + " " + "v" + get_version() + "载入成功");
        LOGGER.info("开源链接：https://github.com/SkyDynamic/SkyDynamic-Carpet-Addition");
        CarpetServer.settingsManager.parseSettingsClass(ScaSetting.class);

    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ComponentTranslate.getTranslationFromResourcePath(lang);
    }
}