package com.funnyboyroks.chatgames;

import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Util {

    public static final Random RNG;

    static {
        RNG = new Random();
    }


    public static String getNameByKey(String key) {
        if (ChatGames.dataHandler().translations != null) {
            String translation = ChatGames.dataHandler().translations.get(key);
            if (translation != null) {
                return translation;
            }
        }
        TranslatableComponent comp = Component.translatable(key);

        return PlainTextComponentSerializer.builder()
            .flattener(PaperComponents.flattener())
            .build()
            .serialize(comp);

    }

    public static String toText(Component originalMessage) {
        return PlainTextComponentSerializer.plainText().serialize(originalMessage);
    }

    public static Component component(ItemStack item) {
        return item.displayName().hoverEvent(item.asHoverEvent());
    }

    public static int constrain(int n, int min, int max) {
        return n > max ? max : Math.max(n, min);
    }

    public static <T> T[] shuffleArray(T[] s) {
        List<T> list = new ArrayList<>(Arrays.asList(s));
        List<T> newList = new ArrayList<>();

        while (!list.isEmpty()) {
            newList.add(list.remove(RNG.nextInt(list.size())));
        }
        return newList.toArray(s);


    }
}
