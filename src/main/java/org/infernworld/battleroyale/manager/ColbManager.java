package org.infernworld.battleroyale.manager;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.infernworld.battleroyale.BattleRoyale;
import org.infernworld.battleroyale.fileSettings.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ColbManager {
    private final BattleRoyale plugin;
    private final Config cfg;

    @Getter
    private final List<Location> colbs = new ArrayList<>();

    public ColbManager(BattleRoyale plugin, Config cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
    }

    public void loadColbs() {
        ConfigurationSection colbsSec = plugin.getDataColbs().getConfigurationSection("colbs");
        if (colbsSec == null) {
            plugin.getLogger().warning("Раздел 'colbs' не найден в конфиге!");
            return;
        }
        colbs.clear();
        colbsSec.getKeys(false).forEach(key -> {
            World world = Bukkit.getWorld(colbsSec.getString(key + ".world"));
            if (world == null) {
                plugin.getLogger().warning("Мир не найден для колбы: " + key);
                return;
            }
            Location loc = new Location(
                    world,
                    colbsSec.getDouble(key + ".x"),
                    colbsSec.getDouble(key + ".y"),
                    colbsSec.getDouble(key + ".z")
            );
            colbs.add(loc);
        });
    }

    public void saveColbLocation(String name, Location location) {
        plugin.getDataColbs().set("colbs." + name + ".world", location.getWorld().getName());
        plugin.getDataColbs().set("colbs." + name + ".x", location.getX());
        plugin.getDataColbs().set("colbs." + name + ".y", location.getY());
        plugin.getDataColbs().set("colbs." + name + ".z", location.getZ());
        plugin.saveConfig();
        try {
            plugin.getDataColbs().save(new File(plugin.getDataFolder(), "dataColbs.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Не удалось сохранить dataColbs.yml: " + e.getMessage());
        }
    }

    public void removeColbs() {
        for (Location colbLoc : colbs) {
            if (colbLoc.getWorld() != null) {
                for (int x = -2; x <= 2; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -2; z <= 2; z++) {
                            Block block = colbLoc.getWorld().getBlockAt(colbLoc.clone().add(x, y, z));
                            if (block.getType() == Material.valueOf(cfg.getBlockRemove())) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }
}
