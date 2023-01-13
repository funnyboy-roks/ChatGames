package com.funnyboyroks.chatgames.game;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.Util;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class GameHandler implements Listener {

    private List<Game> upcomingGames;
    private BukkitTask timeoutClock;
    private BukkitTask nextGameClock;

    public GameHandler() {
        this.upcomingGames = new ArrayList<>();
        List<Game> gameList = ChatGames.config().gameList;
        if (ChatGames.config().shuffled) {
            for (int i = 0; i < 3; ++i) {
                upcomingGames.add(gameList.get(Util.RNG.nextInt(gameList.size())));
            }
        } else {
            upcomingGames.addAll(gameList);
        }
        this.currentGame().reset();
        this.currentGame().start();
    }

    @EventHandler()
    public void onChat(AsyncChatEvent event) {

        Player player = event.getPlayer();
        String message = Util.toText(event.originalMessage());

        boolean guess = this.currentGame().guess(message, player);
        if (guess) {
            ChatGames.config().runReward(player);
            event.setCancelled(true);
        }
    }

    public void nextGame() {
        Game removed = this.upcomingGames.remove(0);
        if (ChatGames.config().shuffled) {
            List<Game> gameList = ChatGames.config().gameList;
            Game game = gameList.get(Util.RNG.nextInt(gameList.size()));
            this.upcomingGames.add(game);
        } else {
            this.upcomingGames.add(removed);
        }

        if (this.timeoutClock != null) {
            this.timeoutClock.cancel();
        }

        if (this.nextGameClock != null) {
            this.nextGameClock.cancel();
        }

        this.currentGame().reset();
        this.currentGame().start();

        this.timeoutClock = Bukkit.getScheduler().runTaskLater(ChatGames.instance(), () -> {
            if(this.currentGame().active)
                this.currentGame().timeout();
        }, ChatGames.config().timeout * 20L);

        this.nextGameClock = Bukkit.getScheduler().runTaskLater(ChatGames.instance(), this::nextGame, ChatGames.config().rotationTime * 20L);
    }

    public Game currentGame() {
        return this.upcomingGames.get(0);
    }

    public void reload() {

    }

}
