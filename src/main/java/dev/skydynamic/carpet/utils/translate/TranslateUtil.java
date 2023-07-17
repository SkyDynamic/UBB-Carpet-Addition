package dev.skydynamic.carpet.utils.translate;

import dev.skydynamic.carpet.api.tools.text.ComponentTranslate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class TranslateUtil {

    private static Map<String, String> translateMap = new HashMap<>();

    public static void handleResourceReload(String lang){
        //#if MC >= 11900
        translateMap = ComponentTranslate.getTranslationFromResourcePath(lang);
        //#else
        //$$ translateMap = OldComponentTranslate.getTranslationFromResourcePath(lang);
        //#endif
    }

    public static String translate(String key, Object... args){
        String fmt = translateMap.getOrDefault(key,key);
        if (!translateMap.containsKey(key))return key;
        return MessageFormat.format(fmt, args);
    }

    public static String tr(String k, Object... o){
        return translate(k,o);
    }
}
