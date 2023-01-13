package com.funnyboyroks.chatgames.game;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.Util;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Scramble extends Game {

    private ScrambleMethod scrambleMethod;

    private String correct;
    private String scrambled;

    public Scramble(ConfigurationSection configurationSection) {
        super(configurationSection);
        this.scrambleMethod = ScrambleMethod.from(configurationSection.getString("scramble-method", "word"));
    }

    @Override
    public void start() {
        this.active = true;
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        aud.sendMessage(
            Component.text("You have %s seconds to unscramble this word!\n> ".formatted(ChatGames.config().timeout), NamedTextColor.GOLD)
                .append(Component.text(scrambled, NamedTextColor.AQUA))
        );
    }

    @Override
    public void timeout() {
        if (!this.active) return;
        this.active = false;
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        aud.sendMessage(
            Component.text("Nobody answered in time! The correct answer was ".formatted(ChatGames.config().timeout), NamedTextColor.GOLD)
                .append(Component.text(correct, NamedTextColor.AQUA))
                .append(Component.text("."))
        );
    }

    @Override
    public void reset() {

        this.correct = ChatGames.config().wordList.randomWord();
        this.scrambled = this.scrambleMethod.shuffle(this.correct);

    }

    @Override
    public boolean guess(String input, Player player) {
        if (this.active && input.equalsIgnoreCase(correct)) {
            this.active = false;
            return true;
        }
        return false;
    }

    @Override
    public void broadcastWin(Player player) {
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());
        aud.sendMessage(
            player.displayName()
                .append(Component.text(" unscrambled the word ", NamedTextColor.GOLD))
                .append(Component.text(correct, NamedTextColor.AQUA))
                .append(Component.text(" correctly!", NamedTextColor.GOLD))
        );
    }

    private enum ScrambleMethod {
        WORD,
        CHARACTER,
        SPLIT_SPACE,
        ;

        public String shuffle(String str) {
            String shuffled = str;

            for (int i = 0; i < 5; ++i) { // Ideally prevent it from returning the same string, effectively unshuffled (though don't try too hard as to cause lag)
                shuffled = switch (this) {

                    case WORD -> {
                        yield String.join(" ", Util.shuffleArray(str.split(" ")));
                    }

                    case CHARACTER -> {
                        yield String.join("", Util.shuffleArray(str.split("")));
                    }

                    case SPLIT_SPACE -> {
                        yield Arrays.stream(str.split(" "))
                            .map(s -> String.join("", Util.shuffleArray(s.split(""))))
                            .collect(Collectors.joining(" "));
                    }

                };
                if (!shuffled.equalsIgnoreCase(str)) {
                    break;
                }
            }
            return shuffled;

        }

        public static ScrambleMethod from(String str) {

            String check = str.replace("-", "_");
            return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(check))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid scramble method: " + str));

        }

    }
}
