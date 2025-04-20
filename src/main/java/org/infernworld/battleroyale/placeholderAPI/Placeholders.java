package org.infernworld.battleroyale.placeholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.infernworld.battleroyale.BattleRoyale;
import org.infernworld.battleroyale.manager.GameManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {
    private final BattleRoyale plugin;
    private final GameManager gameManager;

    public Placeholders(BattleRoyale plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "battleroyale";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("phase")) {
            return getPhase();
        } else if (params.equalsIgnoreCase("border")) {
            return getBorder();
        } else if (params.equalsIgnoreCase("player")) {
            return getLifeOnline();
        }
        return null;
    }

    private String getLifeOnline() {
        return String.valueOf(Bukkit.getOnlinePlayers().stream().filter(
                p -> p.getGameMode() !=
                        GameMode.SPECTATOR).count());
    }

    private String getPhase() {
        long online = Bukkit.getOnlinePlayers().size();

        if (!gameManager.isStart) {
            return plugin.getCfg().getPhasePreStart()
                    .replace("{player}", String.valueOf(online));
        } else if (!gameManager.isPvp) {
            return plugin.getCfg().getPhasePvp()
                    .replace("{time}", time(gameManager.getPvpTimeLeft()));
        } else if (!gameManager.isAd) {
            return plugin.getCfg().getPhaseAd()
                    .replace("{time}", time(gameManager.getAdTimeLeft()));
        }
        return "Игра в процессе";
    }

    private String getBorder() {
        try {
            String worldName = plugin.getCfg().getWorld();
            if (worldName == null || worldName.isEmpty()) {
                plugin.getLogger().warning("Имя мира в кфг нет!");
                return "Нет мира";
            }
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("Мир " + worldName + " не найден :( проверьте название в кофниге");
                return "Мир не загружен или не найден";
            }
            WorldBorder border = world.getWorldBorder();
            if (border == null) {
                return "Нет границы";
            }
            double size = border.getSize();
            Location center = border.getCenter();
            double sizeCen = size / 2;
            double minX = Math.floor(center.getX() - sizeCen);
            double maxX = Math.floor(center.getX() + sizeCen);

            return String.format("%.0f, %.0f", minX, maxX);

        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка границы " + e.getMessage());
            return "Ошибка смотрите в консоль";
        }
    }

    private String time(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d",min,sec);
    }
}