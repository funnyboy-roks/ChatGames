package com.funnyboyroks.chatgames.data;

import com.funnyboyroks.chatgames.ChatGames;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RewardConfig {

    public final ItemStack item;
    public final String    command;
    public final int       money;

    public RewardConfig(ConfigurationSection section) {
        if (section == null) {
            this.item = new ItemStack(Material.DIAMOND, 1);
            this.command = "";
            this.money = 0;
            return;
        }
        this.money = section.getInt("money", 0);
        String command = section.getString("command", "");
        if (command.startsWith("/")) command = command.substring(1);
        this.command = command;
        Material material = Material.matchMaterial(section.getString("item", "minecraft:diamond"));
        if (material == null) material = Material.DIAMOND;
        this.item = new ItemStack(
            material,
            section.getInt("item_amount", 1)
        );
    }

    public boolean hasCommand() {
        return this.command != null && !this.command.isEmpty();
    }

    public boolean hasItem() {
        return this.item != null
               && this.item.getType() != Material.AIR
               && this.item.getAmount() > 0;
    }

    public boolean hasMoney() {
        return this.money != 0 && ChatGames.economy() != null;
    }
}
