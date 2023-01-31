package com.funnyboyroks.chatgames.data;

import com.funnyboyroks.chatgames.ChatGames;
import com.funnyboyroks.chatgames.Util;
import com.funnyboyroks.chatgames.game.FillInTheBlanks;
import com.funnyboyroks.chatgames.game.Game;
import com.funnyboyroks.chatgames.game.Scramble;
import com.tchristofferson.configupdater.ConfigUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PluginConfig {

    private final File configFile;

    public final int          rotationTime;
    public final int timeout;
    public final boolean      shuffled;
    public final List<Game>   gameList;
    public final RewardConfig rewardConfig;
    public final WordList     wordList;

    public PluginConfig(ChatGames plugin) throws IOException {
        plugin.saveDefaultConfig();
        this.configFile = DataHandler.getPluginFile("config.yml");
        this.update();

        FileConfiguration config = plugin.getConfig();

        this.rotationTime = config.getInt("rotation-time", 5 * 60);
        this.timeout = Util.constrain(config.getInt("timeout", 1 * 60), 0, this.rotationTime);
        this.shuffled = config.getBoolean("shuffled", false);
        List<String> gameListNames = config.getStringList("game-list");
        this.rewardConfig = new RewardConfig(config.getConfigurationSection("reward"));
        this.wordList = new WordList(config.getStringList("word-lists"));
        ChatGames.instance().getLogger().info("Loaded " + this.wordList.size() + " word(s)!");

        this.gameList = new ArrayList<>(
            gameListNames
                .stream()
                .map(n -> switch (n) {
                    case "fill-in-the-blanks" ->
                        new FillInTheBlanks(config.getConfigurationSection("fill-in-the-blanks"));
                    case "scramble" -> new Scramble(config.getConfigurationSection("scramble"));
                    default -> null;
                })
                .filter(Objects::nonNull)
                .toList()
        );
    }

    private void update() throws IOException {
        ConfigUpdater.update(ChatGames.instance(), "config.yml", this.configFile);
    }

    public void giveReward(Player player) {
        player.playSound(
            player.getLocation(),
            Sound.ENTITY_PLAYER_LEVELUP,
            1,
            1
        );
        if (this.rewardConfig.hasCommand()) {
            String command = this.rewardConfig.command
                .replace("%player%", player.getName());
            Bukkit.getScheduler().runTask(ChatGames.instance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            });
        }

        if (this.rewardConfig.hasItem()) {
            player.sendMessage(
                Component.text("Receiving ", NamedTextColor.GOLD)
                    .append(Util.component(this.rewardConfig.item))
                    .append(Component.text("!"))
            );
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(this.rewardConfig.item);
            if (!overflow.isEmpty()) {
                overflow.forEach((k, v) -> {
                    player.getWorld()
                        .dropItem(player.getLocation(), v);
                });
                Messager.send(player, "reward.inventory-full");
            }
        }

        if (this.rewardConfig.hasMoney()) {
            if (this.rewardConfig.money > 0) {
                ChatGames.economy().depositPlayer(player, this.rewardConfig.money);
            } else {
                ChatGames.economy().withdrawPlayer(player, -this.rewardConfig.money);
            }
        }
    }

}
