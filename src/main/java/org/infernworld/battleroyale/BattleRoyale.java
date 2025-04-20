package org.infernworld.battleroyale;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.infernworld.battleroyale.commands.Command;
import org.infernworld.battleroyale.commands.TabCompleter;
import org.infernworld.battleroyale.commands.admin.CommandAdmin;
import org.infernworld.battleroyale.commands.admin.TabCompleterAdmin;
import org.infernworld.battleroyale.fileSettings.Config;
import org.infernworld.battleroyale.fileSettings.Message;
import org.infernworld.battleroyale.listener.Events;
import org.infernworld.battleroyale.manager.ColbManager;
import org.bukkit.event.Listener;
import org.infernworld.battleroyale.manager.GameManager;
import org.infernworld.battleroyale.manager.PlayerGameManager;
import org.infernworld.battleroyale.manager.ResetManager;
import org.infernworld.battleroyale.placeholderAPI.Placeholders;

import java.io.File;

@Getter
public final class BattleRoyale extends JavaPlugin {
    private FileConfiguration msg;
    private FileConfiguration config;
    private FileConfiguration dataColbs;

    private Config cfg;
    private Message message;

    private ColbManager colbManager;
    private PlayerGameManager playerGameManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        loadFiles();
        setup();
        registerListener();
        registerCommand();

        createFolderWorld();
        new ResetManager(this).resetWorld(cfg.getWorld(), "base");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders(this).register();
        }
    }

    private void setup() {
        this.cfg = new Config(getConfig());
        this.message = new Message(getMsg());
        this.colbManager = new ColbManager(this, cfg);
        this.gameManager = new GameManager(this,cfg,colbManager,message);
        this.playerGameManager = new PlayerGameManager(this,colbManager,cfg,message,gameManager);
        colbManager.loadColbs();
    }

    private void registerCommand() {
        PluginCommand cmd = getCommand("tp");
        cmd.setExecutor(new Command(message));
        cmd.setTabCompleter(new TabCompleter());

        PluginCommand admCmd =getCommand("battleroyale");
        admCmd.setExecutor(new CommandAdmin(this, colbManager));
        admCmd.setTabCompleter(new TabCompleterAdmin());
    }

    private void createFolderWorld() {
        File folder = new File(getDataFolder(),"worlds");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void registerListener() {
        PluginManager pm = getServer().getPluginManager();
        Listener[] listeners = {
                new Events(this,playerGameManager,gameManager,message)
        };
        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    private FileConfiguration addonCfgFile(String path, boolean saveDefault) {
        final File file = new File(getDataFolder(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (saveDefault) {
                saveResource(path, true);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    private void loadFiles() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.msg = addonCfgFile("message.yml", true);
        this.config = addonCfgFile("config.yml", true);
        this.dataColbs = addonCfgFile("dataColbs.yml", true);
    }

    @Override
    public void onDisable() {
        if (gameManager.game != null) {
            gameManager.game.cancel();
        }
    }
}
