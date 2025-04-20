package org.infernworld.battleroyale.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtil {

    private SoundUtil() {
        throw new UnsupportedOperationException("Неизвестная ошибка util класса!");
    }

    public static void playSound(Player player, Sound sound) {
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1f, 1f);
        }
    }
}
