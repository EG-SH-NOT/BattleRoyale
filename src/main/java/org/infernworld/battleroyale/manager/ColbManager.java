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
        String tpl = plugin.getDataColbs().getString("world-base");
        ConfigurationSection section = plugin.getDataColbs()
                .getConfigurationSection("colbs." + tpl);

        if (section == null) {
            plugin.getLogger().warning("Колбы для шаблона '" + tpl + "' не найдены!");
            return;
        }

        colbs.clear();
        for (String key : section.getKeys(false)) {
            ConfigurationSection c = section.getConfigurationSection(key);
            World world = Bukkit.getWorld(c.getString("world"));
            Location loc = new Location(
                    world,
                    c.getDouble("x"),
                    c.getDouble("y"),
                    c.getDouble("z")
            );
            colbs.add(loc);
        }
    }

    public void saveColbLocation(String name, Location location) {
        String tpl = plugin.getDataColbs().getString("world-base");
        String path = "colbs." + tpl + "." + name;

        plugin.getDataColbs().set(path + ".world", location.getWorld().getName());
        plugin.getDataColbs().set(path + ".x", location.getX());
        plugin.getDataColbs().set(path + ".y", location.getY());
        plugin.getDataColbs().set(path + ".z", location.getZ());

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
