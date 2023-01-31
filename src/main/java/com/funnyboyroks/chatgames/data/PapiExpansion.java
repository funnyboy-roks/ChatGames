package com.funnyboyroks.chatgames.data;

import com.funnyboyroks.chatgames.ChatGames;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return ChatGames.instance().getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", ChatGames.instance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return ChatGames.instance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] args = params.toLowerCase().split("_");
        return switch (args[0]) {
            case "current-game" -> ChatGames.gameHandler().currentGame().name();
            case "current-word" -> ChatGames.gameHandler().currentGame().word();
            default -> null;
        };
    }
}
