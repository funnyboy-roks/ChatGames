package com.funnyboyroks.chatgames;

import com.funnyboyroks.chatgames.command.CommandChatGames;
import com.funnyboyroks.chatgames.data.DataHandler;
import com.funnyboyroks.chatgames.data.Messager;
import com.funnyboyroks.chatgames.data.PapiExpansion;
import com.funnyboyroks.chatgames.data.PluginConfig;
import com.funnyboyroks.chatgames.game.GameHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class ChatGames extends JavaPlugin {

    private static ChatGames instance;

    private PluginConfig config;
    private Messager     messager;
    private DataHandler  dataHandler;
    private GameHandler  gameHandler;

    private Economy              economy = null;
    private PlaceholderExpansion papi    = null;

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

        if (setupVault()) {
            this.getLogger().info("Hooked into Vault.");
        } else {
            this.getLogger().info("Vault plugin not found.");
        }

        if (setupPapi()) {
            this.getLogger().info("Hooked into PAPI.");
        } else {
            this.getLogger().info("Vault plugin not found.");
        }

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
            throw new RuntimeException("Error Loading config.yml - Disabling Plugin", e);
        }
        try {
            this.messager = new Messager(this);
        } catch (IOException e) {
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException("Error Loading messages.yml - Disabling Plugin", e);
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

    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private boolean setupPapi() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return false;
        }
        (this.papi = new PapiExpansion()).register();
        return true;
    }

    public static ChatGames instance() {
        return instance;
    }

    public static PluginConfig config() {
        return instance().config;
    }

    public static Messager messager() {
        return instance().messager;
    }

    public static Economy economy() {
        return instance().economy;
    }

    public static boolean hasPapi() {
        return instance().papi != null;
    }

    public static DataHandler dataHandler() {
        return instance().dataHandler;
    }

    public static GameHandler gameHandler() {
        return instance().gameHandler;
    }
}
