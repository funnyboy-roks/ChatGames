package com.funnyboyroks.chatgames.game;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.Util;
import com.funnyboyroks.chatgames.data.Messager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FillInTheBlanks extends Game {

    public int    amount  = -1;
    public double percent = -1;

    public String correct;
    public String blanked;

    public long    startTime;

    public FillInTheBlanks(ConfigurationSection section) {
        super(section);
        if (section.isString("amount")) {
            String amount = section.getString("amount", "50%");
            this.percent = Double.parseDouble(amount.substring(0, amount.length() - 1)) / 100.0;
            if (this.percent > 1) this.percent = 1;
            if (this.percent < 0) this.percent = 0;
        } else {
            this.amount = section.getInt("amount", 5);
            if (this.amount < 0) this.amount = 0;
        }
    }

    @Override
    public void start() {
        this.active = true;
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        Messager.send(
            aud, "games.fitb.prompt",
            "WORD", blanked,
            "TIME", ChatGames.config().timeout + ""
        );
    }

    @Override
    public void timeout() {
        if (!this.active) return;
        this.active = false;
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());

        Messager.send(
            aud, "games.fitb.timeout",
            "WORD", correct
        );
    }

    @Override
    public String name() {
        return Messager.get("games.fitb.name");
    }

    @Override
    public String word() {
        return this.active ? blanked : correct;
    }

    @Override
    public void reset() {

        this.correct = ChatGames.config().wordList.randomWord();
        this.blanked = blankString(this.correct);

    }

    @Override
    public boolean guess(String input, Player player) {
        if (this.active && input.equalsIgnoreCase(correct)) {
            broadcastWin(player);
            this.active = false;
            return true;
        }
        return false;
    }

    @Override
    public void broadcastWin(Player player) {
        Audience aud = Audience.audience(Bukkit.getOnlinePlayers());
        Messager.send(
            aud, "games.fitb.win",
            "WORD", correct,
            "NAME", player.getName()
        );
    }

    @Override
    public String toString() {
        return "FillInTheBlanks{" +
               "amount=" + amount +
               ", percent=" + percent +
               ", correctString='" + correct + '\'' +
               ", blankedString='" + blanked + '\'' +
               ", startTime=" + startTime +
               ", active=" + active +
               '}';
    }

    private String blankString(String str) {

        if (this.amount != -1 && str.length() < this.amount) {
            throw new IllegalArgumentException("String \"" + str + "\" is too short for the provided `amount` of " + this.amount);
        }

        for (int unused = 0; unused < 5; ++unused) {

            int amount = this.amount != -1 ? this.amount : (int) Math.round(str.length() * this.percent);

            List<Integer> possibleSpots = new ArrayList<>(IntStream.range(0, str.length()).filter(n -> str.charAt(n) != ' ').boxed().toList());
            List<Integer> chosen = new ArrayList<>();

            for (int i = 0; i < amount; ++i) {
                int removed = possibleSpots.remove(Util.RNG.nextInt(possibleSpots.size()));
                chosen.add(removed);
            }
            String[] chars = str.split("");

            chosen.forEach(i -> chars[i] = "_");

            String out = String.join("", chars);
            if (!str.equals(out)) {
                return out;
            }
        }
        return str;

    }
}
