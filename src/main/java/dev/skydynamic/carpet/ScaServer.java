package dev.skydynamic.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;

//#if MC>=11900
//$$ import carpet.api.settings.CarpetRule;
//$$ import carpet.api.settings.RuleHelper;
//$$ import carpet.script.Module;
//#else
import carpet.settings.ParsedRule;
import carpet.script.bundled.BundledModule;
//#endif

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//#if MC>=11900
import com.mojang.brigadier.CommandDispatcher;
import dev.skydynamic.carpet.api.tools.text.ComponentTranslate;
//#else
//$$ import dev.skydynamic.carpet.api.tools.text.OldComponentTranslate;
//#endif
import dev.skydynamic.carpet.command.TpaCommand;
import dev.skydynamic.carpet.utils.recipes.CraftingRule;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

public class ScaServer implements CarpetExtension, ModInitializer {

    public static MinecraftServer minecraftServer;
    public static String MOD_ID = "Carpet SD Addition";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public String get_version() {

        return "1.0.6";

    }

    public String version() {
        return "carpet-SD-addition";
    }

    public static void loadExtension() {

        CarpetServer.manageExtension(new ScaServer());

    }
    //#if MC >= 11900
    //$$@Override
    //$$    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, net.minecraft.command.CommandRegistryAccess commandBuildContext) {
    //$$        TpaCommand.register(dispatcher);
    //$$    }
    //#else
    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        TpaCommand.register(dispatcher);
    }
    //#endif
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
    public void onServerLoaded(MinecraftServer server) {
        minecraftServer = server;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        //#if MC>=11900
        return ComponentTranslate.getTranslationFromResourcePath(lang);
        //#else
        //$$ return OldComponentTranslate.getTranslationFromResourcePath(lang);
        //#endif
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        String datapackPath = server.getSavePath(WorldSavePath.DATAPACKS).toString();
        if (Files.isDirectory(new File(datapackPath + "/Sca_flexibleData/").toPath())) {
            try {
                FileUtils.deleteDirectory(new File(datapackPath + "/Sca_flexibleData/"));
            } catch (IOException e) {
                return;
            }
        }
        datapackPath += "/ScaData/";
        boolean isFirstLoad = !Files.isDirectory(new File(datapackPath).toPath());

        try {
            Files.createDirectories(new File(datapackPath + "data/sca/recipes").toPath());
            Files.createDirectories(new File(datapackPath + "data/sca/advancements").toPath());
            Files.createDirectories(new File(datapackPath + "data/sca/functions").toPath());
            Files.createDirectories(new File(datapackPath + "data/minecraft/recipes").toPath());
            copyFile("assets/sca/ScaRecipeTweakPack/pack.mcmeta", datapackPath + "pack.mcmeta");
            copyFile("assets/sca/ScaRecipeTweakPack/sca/functions/give_nongfu_spring.mcfunction", datapackPath + "data/sca/functions/give_nongfu_spring.mcfunction");
            copyFile("assets/sca/ScaRecipeTweakPack/sca/advancements/adv_nongfu.json", datapackPath + "data/sca/advancements/adv_nongfu.json");
        } catch (IOException e) {
            return;
        }

        copyFile(
                "assets/sca/ScaRecipeTweakPack/sca/advancements/root.json",
                datapackPath + "data/sca/advancements/root.json"
        );

        for (Field f : ScaSetting.class.getDeclaredFields()) {
            CraftingRule craftingRule = f.getAnnotation(CraftingRule.class);
            if (craftingRule == null) continue;
            registerCraftingRule(
                    craftingRule.name().isEmpty() ? f.getName() : craftingRule.name(),
                    craftingRule.recipes(),
                    craftingRule.recipeNamespace(),
                    datapackPath + "data/"
            );
        }
        reload();
        if (isFirstLoad) {
            //#if MC>=11900
            //$$server.getCommandManager().executeWithPrefix(server.getCommandSource(), "/datapack enable \"file/ScaData\"");
            //#else
            server.getCommandManager().execute(server.getCommandSource(), "/datapack enable \"file/ScaData\"");
            //#endif
        }
    }

    private void registerCraftingRule(String ruleName, String[] recipes, String recipeNamespace, String dataPath) {
        updateCraftingRule(CarpetServer.settingsManager.getRule(ruleName),recipes,recipeNamespace,dataPath,ruleName);
        CarpetServer.settingsManager.addRuleObserver
                ((source, rule, s) -> {
                    //#if MC>=11900
                    //$$if (rule.name().equals(ruleName)) {
                    //$$    updateCraftingRule(rule, recipes, recipeNamespace, dataPath, ruleName);
                    //$$    reload();
                    //$$}
                    //#else
                    if (rule.name.equals(ruleName)) {
                        updateCraftingRule(rule, recipes, recipeNamespace, dataPath, ruleName);
                        reload();
                    }
                    //#endif
                });
    }

    private void updateCraftingRule(
            ParsedRule<?> rule,
            String[] recipes,
            String recipeNamespace,
            String datapackPath,
            String ruleName
    ) {
        ruleName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, ruleName);
        //#if MC>=11900
        //$$if (rule.type() == String.class) {
        //$$String value = RuleHelper.toRuleString(rule.value());
        //#else
        if (rule.type == String.class) {
            String value = rule.getAsString();
            //#endif
            List<String> installedRecipes = Lists.newArrayList();
            try {
                Stream<Path> fileStream = Files.list(new File(datapackPath + recipeNamespace, "recipes").toPath());
                fileStream.forEach(( path -> {
                    for (String recipeName : recipes) {
                        String fileName = path.getFileName().toString();
                        if (fileName.startsWith(recipeName)) {
                            installedRecipes.add(fileName);
                        }
                    }
                } ));
                fileStream.close();
            } catch (IOException e) {
                return;
            }

            deleteRecipes(installedRecipes.toArray(new String[0]), recipeNamespace, datapackPath, ruleName, false);

            if (recipeNamespace.equals("sca")) {
                List<String> installedAdvancements = Lists.newArrayList();
                try {
                    Stream<Path> fileStream = Files.list(new File(datapackPath, "sca/advancements").toPath());
                    String finalRuleName = ruleName;
                    fileStream.forEach(( path -> {
                        String fileName = path.getFileName().toString().replace(".json", "");
                        if (fileName.startsWith(finalRuleName)) {
                            installedAdvancements.add(fileName);
                        }
                    } ));
                    fileStream.close();
                } catch (IOException e) {
                    return;
                }
                for (String advancement : installedAdvancements.toArray(new String[0])) {
                    removeAdvancement(datapackPath, advancement);
                }
            }

            if (!value.equals("off")) {
                List<String> tempRecipes = Lists.newArrayList();
                for (String recipeName : recipes) {
                    tempRecipes.add(recipeName + "_" + value + ".json");
                }

                copyRecipes(tempRecipes.toArray(new String[0]), recipeNamespace, datapackPath, ruleName + "_" + value);
            }
        }
        //#if MC>=11900
        //$$else if (rule.type() == Integer.class && (Integer) rule.value() > 0) {
        //#else
        else if (rule.type == int.class && (Integer) rule.get() > 0) {
            //#endif
            copyRecipes(recipes, recipeNamespace, datapackPath, ruleName);
            int value = (Integer) rule.get();
            for (String recipeName : recipes) {
                String filePath = datapackPath + recipeNamespace + "/recipes/" + recipeName;
                JsonObject jsonObject = readJson(filePath);
                assert jsonObject != null;
                jsonObject.getAsJsonObject("result").addProperty("count", value);
                writeJson(jsonObject, filePath);
            }
        }
        //#if MC>=11900
        //$$else if (rule.type() == Boolean.class && RuleHelper.getBooleanValue(rule)) {
        //#else
        else if (rule.type == boolean.class && rule.getBoolValue()) {
            //#endif
            copyRecipes(recipes, recipeNamespace, datapackPath, ruleName);
        } else {
            deleteRecipes(recipes, recipeNamespace, datapackPath, ruleName, true);
        }
    }

    private void copyRecipes(String[] recipes, String recipeNamespace, String datapackPath, String ruleName) {
        for (String recipeName : recipes) {
            copyFile(
                    "assets/sca/ScaRecipeTweakPack/" + recipeNamespace + "/recipes/" + recipeName,
                    datapackPath + recipeNamespace + "/recipes/" + recipeName
            );
        }
        if (recipeNamespace.equals("sca")) {
            writeAdvancement(datapackPath, ruleName, recipes);
        }
    }

    private void deleteRecipes(
            String[] recipes,
            String recipeNamespace,
            String datapackPath,
            String ruleName,
            boolean removeAdvancement
    ) {
        for (String recipeName : recipes) {
            try {
                Files.deleteIfExists(new File(datapackPath + recipeNamespace + "/recipes", recipeName).toPath());
            } catch (IOException e) {
                return;
            }
        }
        if (removeAdvancement && recipeNamespace.equals("ams")) {
            removeAdvancement(datapackPath, ruleName);
        }
    }

    private void writeAdvancement(String datapackPath, String ruleName, String[] recipes) {
        copyFile(
                "assets/sca/ScaRecipeTweakPack/sca/advancements/recipe_rule.json",
                datapackPath + "sca/advancements/" + ruleName + ".json"
        );

        JsonObject advancementJson = readJson(datapackPath + "sca/advancements/" + ruleName + ".json");
        assert advancementJson != null;
        JsonArray recipeRewards = advancementJson.getAsJsonObject("rewards").getAsJsonArray("recipes");

        for (String recipeName : recipes) {
            recipeRewards.add("sca:" + recipeName.replace(".json", ""));
        }
        writeJson(advancementJson, datapackPath + "sca/advancements/" + ruleName + ".json");
    }

    private void removeAdvancement(String datapackPath, String ruleName) {
        try {
            Files.deleteIfExists(new File(datapackPath + "sca/advancements/" + ruleName + ".json").toPath());
        } catch (IOException ignored) {

        }
    }

    private void reload() {
        ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
        resourcePackManager.scanPacks();
        Collection<String> collection = Lists.newArrayList(resourcePackManager.getEnabledNames());
        collection.add("ScaData");

        ReloadCommand.tryReloadDataPacks(collection, minecraftServer.getCommandSource());
    }

    private void copyFile(String resourcePath, String targetPath) {
        InputStream source = BundledModule.class.getClassLoader().getResourceAsStream(resourcePath);
        Path target = new File(targetPath).toPath();

        try {
            assert source != null;
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        } catch (NullPointerException e) {
            LOGGER.error("Resource '" + resourcePath + "' is null:");
        }
    }

    //#if MC>=11900
    private static JsonObject readJson(String filePath) {
        try {
            FileReader reader = new FileReader(filePath);
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    //#else
    //$$ private static JsonObject readJson(String filePath) {
    //$$    JsonParser jsonParser = new JsonParser();
    //$$    try {
    //$$        FileReader reader = new FileReader(filePath);
    //$$        return jsonParser.parse(reader).getAsJsonObject();
    //$$    } catch (FileNotFoundException e) {
    //$$        e.printStackTrace();
    //$$    }
    //$$    return null;
    //$$}
    //#endif

    private static void writeJson(JsonObject jsonObject, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
            writer.close();
        } catch (IOException ignored) {
        }
    }
}