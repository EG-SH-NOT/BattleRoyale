package org.infernworld.battleroyale.manager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.infernworld.battleroyale.BattleRoyale;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class ResetManager {

    private final BattleRoyale plugin;
    private final File pluginWorldsFolder;

    public ResetManager(BattleRoyale plugin) {
        this.plugin = plugin;
        this.pluginWorldsFolder = new File(plugin.getDataFolder(), "worlds");
    }

    public void resetWorld(String worldName, String templateName) {
        unloadWorld(worldName);
        deleteWorldFolder(worldName);
        copyWorldTemplate(templateName, worldName);
        loadWorld(worldName);
    }

    private void unloadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            plugin.getLogger().info("Мир " + worldName + " выгружен.");
        }
    }

    private void deleteWorldFolder(String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        try {
            if (worldFolder.exists()) {
                deleteRecursive(worldFolder.toPath());
                plugin.getLogger().info("Мир " + worldName + " удалён.");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка при удалении мира: " + e.getMessage());
        }
    }

    private void copyWorldTemplate(String templateName, String targetWorldName) {
        File templateFolder = new File(pluginWorldsFolder, templateName);
        File targetFolder = new File(Bukkit.getWorldContainer(), targetWorldName);

        if (!templateFolder.exists()) {
            plugin.getLogger().severe("Шаблонный мир '" + templateName + "' не найден!");
            return;
        }

        try {
            copyDirectory(templateFolder.toPath(), targetFolder.toPath());
            plugin.getLogger().info("Шаблонный мир '" + templateName + "' скопирован как '" + targetWorldName + "'.");
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка копирования мира: " + e.getMessage());
        }
    }

    private void loadWorld(String worldName) {
        WorldCreator creator = new WorldCreator(worldName);
        Bukkit.createWorld(creator);
        plugin.getLogger().info("Мир '" + worldName + "' загружен.");
    }

    private void deleteRecursive(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(src -> {
            Path dest = target.resolve(source.relativize(src));
            try {
                if (Files.isDirectory(src)) {
                    if (!Files.exists(dest)) {
                        Files.createDirectory(dest);
                    }
                } else {
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
