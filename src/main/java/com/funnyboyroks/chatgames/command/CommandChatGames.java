package com.funnyboyroks.chatgames.command;

import com.funnyboyroks.chatgames.ChatGames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandChatGames implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false; // TODO: make this print the help message
        }
        switch (Subcommand.fromFormatted(args[0])) {
            case RELOAD -> {
                ChatGames.reload();
                sender.sendMessage(Component.text("ChatGames reloaded!", NamedTextColor.GREEN));
            }
            case RANDOM -> {
                sender.sendMessage(ChatGames.config().wordList.randomWord());
            }
            case RESTART -> {
                ChatGames.gameHandler().nextGame();
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Subcommand.values()).map(Subcommand::toFormatted).filter(s -> s.startsWith(args[0])).toList();
        }
        return Collections.emptyList();
    }

    private enum Subcommand {
        RELOAD,
        RANDOM,
        RESTART,
        ;

        public String toFormatted() {
            return name().toLowerCase().replace('_', '-');
        }

        public static Subcommand fromFormatted(String name) {
            return Subcommand.valueOf(name.toUpperCase().replace('-', '_'));
        }
    }
}
