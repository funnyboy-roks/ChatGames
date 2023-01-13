package com.funnyboyroks.chatgames.data;

import com.funnyboyroks.chatgames.ChatGames;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    public Map<String, String> translations;

    public void reloadTranslations() {
        File translationsFile = DataHandler.getPluginFile("translations.json");

        if (translationsFile.exists()) {
            try {
                String json = Files.readString(translationsFile.toPath());
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) new GsonBuilder()
                    .create()
                    .fromJson(json, TypeToken.getParameterized(HashMap.class, String.class, String.class));
                this.translations = map;
                ChatGames.instance()
                    .getLogger()
                    .info("Loaded translations from translation.json: " + map.get("language.name") + " (" + map.get("language.region") + ").");
            } catch (Exception e) {
                throw new RuntimeException("Error loading translations.json", e);
            }
        } else {
            this.translations = null;
        }

    }

    public static File getPluginFile(String name) {
        return new File(ChatGames.instance().getDataFolder(), name);
    }

    public String[] loadWordList(String fileName) throws IOException {
        return Files.readString(
                getPluginFile("lists/" + fileName).toPath(),
                StandardCharsets.UTF_8
            )
            .split("\n");
    }
}
