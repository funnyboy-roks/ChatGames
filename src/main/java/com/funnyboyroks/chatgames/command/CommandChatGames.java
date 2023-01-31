package com.funnyboyroks.chatgames.command;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.data.Messager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
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
            return false; // TODO: make this print a help message
        }
        Subcommand sc = Subcommand.fromFormatted(args[0]);
        if(sc == null) {
            return false;
        }
        if(!sender.hasPermission("chatgames.command.chatgames." + sc.toFormatted())) {
            Messager.send(sender, "command.no-permission");
        }
        switch (sc) {
            case RELOAD -> {
                ChatGames.reload();
                Messager.send(sender, "command.reload.success");
            }
            case LISTS -> {
                Messager.send(sender, "command.lists.uploading");
                Bukkit.getScheduler().runTaskAsynchronously(ChatGames.instance(), () -> {
                    try {
                        String key = ChatGames.config().wordList.upload();
                        String url = "https://pastes.dev/" + key;

                        Messager.send(sender, "command.lists.success", "URL", url);
                    } catch (Exception e) {
                        sender.sendMessage(Component.text("There was an error executing this command.", NamedTextColor.RED));
                        throw new RuntimeException(e);
                    }
                });
            }
            case NEXT -> {
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
        LISTS,
        NEXT,
        ;

        public String toFormatted() {
            return name().toLowerCase().replace('_', '-');
        }

        public static Subcommand fromFormatted(String name) {
            return Subcommand.valueOf(name.toUpperCase().replace('-', '_'));
        }
    }
}
