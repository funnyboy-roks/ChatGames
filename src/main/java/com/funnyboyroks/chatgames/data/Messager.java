package com.funnyboyroks.chatgames.data;

import com.funnyboyroks.chatgames.ChatGames;
import com.tchristofferson.configupdater.ConfigUpdater;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messager {

    private final File              configFile;
    private final FileConfiguration config;

    public Messager(ChatGames plugin) throws IOException {
        this.configFile = DataHandler.getPluginFile("messages.yml");
        if(!configFile.exists()) {
            Files.createFile(this.configFile.toPath());
        }
        this.update();
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public static String get(String key) {
        return ChatGames.messager().config.getString(key);
    }

    private void update() throws IOException {
        ConfigUpdater.update(ChatGames.instance(), "messages.yml", this.configFile);
    }

    public static void send(@NotNull Audience sender, @NotNull String key, @Nullable Map<String, String> replacements) {
        String[] mmArr = { ChatGames.messager().config.getString(key) };
        if (mmArr[0] == null) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        mmArr[0] = mmArr[0].replace("\\n", "\n");
        if (replacements != null) {
            replacements.forEach((k, v) -> {
                mmArr[0] = mmArr[0].replace("$" + k + "$", v); // I hate Java sometimes (always :P)
            });
        }
        String mm = mmArr[0];
        Component c = MiniMessage.miniMessage().deserialize(mm);
        sender.sendMessage(c);
    }

    /**
     * @param replacements Array of strings, read as a map: key, value, key, value...
     */
    public static void send(@NotNull Audience sender, @NotNull String key, String replacements0, String... replacements) {
        if (replacements.length % 2 == 0) {
            throw new IllegalArgumentException("replacements must be an even length and follow the pattern of key, value...");
        }

        List<String> reps = new ArrayList<>(List.of(replacements));
        reps.add(0, replacements0);

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < reps.size(); i += 2) {
            map.put(reps.get(i), reps.get(i + 1));
        }

        send(sender, key, map);

    }

    public static void send(@NotNull Audience sender, @NotNull String key) {
        send(sender, key, null);
    }


}
