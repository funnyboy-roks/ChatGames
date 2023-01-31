package com.funnyboyroks.chatgames.data;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.Util;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordList {

    private final List<String[]> words;

    private static String[] MOBS   = null; // effectively final
    private static String[] BLOCKS = null; // effectively final
    private static String[] ITEMS  = null; // effectively final

    public WordList(List<String> keys) {
        this.words = keys.stream().map(WordList::getWords).toList();
    }

    public String randomWord() {
        if (this.words.size() == 0) return null;
        String[] words = this.words.get(Util.RNG.nextInt(this.words.size()));
        return words[Util.RNG.nextInt(words.length)];
    }

    private static String[] getWords(String key) {
        key = key.toLowerCase();
        int colonPos = key.indexOf(":");
        String namespace;
        if (colonPos == -1) { // Count "key" as "custom:key"
            namespace = "custom";
        } else {
            namespace = key.substring(0, colonPos);
        }

        String id = key.substring(colonPos + 1);

        String[] lines;
        try {
            lines = switch (namespace) {
                case "chatgames" -> {
                    InputStream is = ChatGames.instance().getResource("lists/" + id + ".txt");
                    yield new String(is.readAllBytes(), StandardCharsets.UTF_8).split("\n");
                }
                case "minecraft" -> switch (id) {
                    case "blocks" -> getBlocksList();
                    case "items" -> getItemsList();
                    case "mobs" -> getMobsList();
                    default -> throw new IllegalArgumentException("Invalid id for minecraft namespace \"" + id + "\"");
                };
                case "custom" -> ChatGames.dataHandler().loadWordList(id);
                default -> {
                    throw new IllegalArgumentException("Invalid namespace \"" + namespace + "\"");
                }
            };
        } catch (Exception e) {
            ChatGames.instance().getLogger().severe("Unable to load word set \"" + key + "\"");
            e.printStackTrace();
            return new String[0];
        }


        return Arrays.stream(lines)
            .filter(l -> !l.startsWith("#"))
            .map(String::trim)
            .filter(l -> !l.isEmpty())
            .toArray(String[]::new);
    }

    private static String[] getBlocksList() {
        if (BLOCKS != null) return BLOCKS;
        return BLOCKS = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .map(Material::translationKey)
            .map(Util::getNameByKey)
            .toArray(String[]::new);
    }

    private static String[] getItemsList() {
        if (ITEMS != null) return ITEMS;
        return ITEMS = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .map(Material::translationKey)
            .map(Util::getNameByKey)
            .toArray(String[]::new);
    }

    private static String[] getMobsList() {
        if (MOBS != null) return MOBS;
        return MOBS = Arrays.stream(EntityType.values())
            .filter(EntityType::isAlive)
            .filter(EntityType::isSpawnable)
            .filter(e -> e != EntityType.PLAYER)
            .map(EntityType::translationKey)
            .map(Util::getNameByKey)
            .toArray(String[]::new);
    }

    public int size() {
        return this.words.stream().mapToInt(s -> s.length).sum();
    }

    public String upload() throws IOException {
        String toUpload = this.words.stream().map(s -> String.join("\n", s)).collect(Collectors.joining("\n\n"));

        URL url = new URL("https://api.pastes.dev/post");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestProperty("User-Agent", "ChatGames by funnyboy_roks");
        http.setDoInput(true);
        http.setDoOutput(true);
        try (OutputStream os = http.getOutputStream()) {
            os.write(toUpload.getBytes());
        }
        String s = new String(http.getInputStream().readAllBytes());
        return JsonParser.parseString(s).getAsJsonObject().get("key").getAsString();
    }
}
