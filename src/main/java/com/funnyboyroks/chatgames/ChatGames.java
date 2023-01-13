package com.funnyboyroks.chatgames;

import com.funnyboyroks.chatgames.command.CommandChatGames;
import com.funnyboyroks.chatgames.data.DataHandler;
import com.funnyboyroks.chatgames.data.PluginConfig;
import com.funnyboyroks.chatgames.game.GameHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class ChatGames extends JavaPlugin {

    private static ChatGames instance;

    private PluginConfig config;
    private DataHandler  dataHandler;
    private GameHandler  gameHandler;

    public ChatGames() {
        instance = this;
    }

    public static void reload() {
        instance._reload();
    }

    @Override
    public void onEnable() {
        this.getCommand("chatgames").setExecutor(new CommandChatGames());

        this.dataHandler = new DataHandler();
        DataHandler.getPluginFile("lists").mkdirs();
        this.saveResource("lists/example.txt", false);
        this.load();
        Bukkit.getPluginManager().registerEvents(this.gameHandler = new GameHandler(), this);

    }

    @Override
    public void onDisable() {

    }

    private void load() {
        this.dataHandler.reloadTranslations();
        try {
            this.config = new PluginConfig(this);
        } catch (IOException e) {
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException("Error Loading Config - Disabling Plugin", e);
        }
    }

    private void _reload() {
        this.getLogger().info("Reloading plugin...");
        long start = System.currentTimeMillis();

        this.load();
        this.gameHandler.reload();

        long end = System.currentTimeMillis();
        this.getLogger().info("Done reloading plugin! (" + (end - start) + "ms)");
    }

    public static ChatGames instance() {
        return instance;
    }

    public static PluginConfig config() {
        return instance().config;
    }

    public static DataHandler dataHandler() {
        return instance().dataHandler;
    }

    public static GameHandler gameHandler() {
        return instance().gameHandler;
    }
}
