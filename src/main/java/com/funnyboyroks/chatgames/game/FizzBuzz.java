package com.funnyboyroks.chatgames.game;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.Util;
import com.funnyboyroks.chatgames.data.Messager;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class FizzBuzz extends Game {

    private int minNum; 
    private int maxNum;

    private FizzBuzzOption correct;
    private int number;

    public FizzBuzz(ConfigurationSection configurationSection) {
        super(configurationSection);
        this.minNum = configurationSection.getInt("min", 0);
        if (this.minNum < 0) this.minNum = 0;

        this.maxNum = configurationSection.getInt("max", 100);
    }

    @Override
    public void start() {
        this.active = true;
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        Messager.send(
            aud, "games.fizzbuzz.prompt",
            "WORD", number + "",
            "TIME", ChatGames.config().timeout + ""
        );
    }

    @Override
    public void timeout() {
        if (!this.active) return;
        this.active = false;
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        Messager.send(
            aud, "games.fizzbuzz.timeout",
            "WORD", number + ""
        );
    }

    @Override
    public String name() {
        return Messager.get("games.fizzbuzz.name");
    }

    @Override
    public String word() {
        return this.active ? number + "" : correct + "";
    }

    @Override
    public void reset() {

        this.number = this.getNumber();
        this.correct = FizzBuzzOption.fromNumber(this.number);

    }

    @Override
    public boolean guess(String input, Player player) {
        player.sendMessage(input + " - " + FizzBuzzOption.fromString(input) + " - " + correct);
        if (this.active && FizzBuzzOption.fromString(input) == correct) {
            this.active = false;
            return true;
        }
        return false;
    }

    @Override
    public void broadcastWin(Player player) {
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        Messager.send(
            aud, "games.fizzbuzz.win",
            "WORD", correct + "",
            "NUM", number + "",
            "NAME", player.getName()
        );
    }

    private int getNumber() {
        return Util.RNG.nextInt(this.minNum, this.maxNum);
    }

    public enum FizzBuzzOption {
        FIZZ("Fizz"),
        BUZZ("Buzz"),
        FIZZBUZZ("FizzBuzz"),
        NONE("None"),
        ;

        public String humanName;
        FizzBuzzOption(String humanName) {
            this.humanName = humanName;
        }

        public static FizzBuzzOption fromNumber(int num) {
            if (num % 3 == 0 && num % 5 == 0) {
                return FIZZBUZZ;
            } else if (num % 3 == 0) {
                return FIZZ;
            } else if (num % 5 == 0) {
                return BUZZ;
            }
            return NONE;
        }

        public static FizzBuzzOption fromString(String str) {
            return Arrays.stream(FizzBuzzOption.values()) // Go through each value in the enum and find the one with the correct name
                .filter(o -> o.name().replaceAll("[-_ ]", "").equalsIgnoreCase(str.replaceAll("[-_ ]", "")))
                .findAny()
                .orElse(NONE);
        }

    }

}
