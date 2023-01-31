package com.funnyboyroks.chatgames.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class Game {

    public boolean enabled;
    public boolean active;

    public Game(ConfigurationSection section) {
        this.enabled = section.getBoolean("enabled", false);
    }

    public abstract void start();

    public abstract void reset();

    public abstract boolean guess(String input, Player player);

    public abstract void broadcastWin(Player player);

    public abstract void timeout();

    public abstract String name();

    public abstract String word();
}
