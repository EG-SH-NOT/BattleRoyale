package org.infernworld.battleroyale.manager;

import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.infernworld.battleroyale.BattleRoyale;
import org.infernworld.battleroyale.fileSettings.Config;
import org.infernworld.battleroyale.fileSettings.Message;
import org.infernworld.battleroyale.util.SoundUtil;

public class GameManager {
    private final BattleRoyale plugin;
    private final Config cfg;
    private final ColbManager colbManager;
    private final Message message;

    public BukkitRunnable game;
    private BukkitRunnable pvpTimer;
    private BukkitRunnable adTimer;

    public boolean isStart = false;
    public boolean isPvp = false;
    public boolean isAd = false;

    @Getter
    private int preStartTime;
    @Getter
    private int pvpTimeLeft;
    @Getter
    private int adTimeLeft;

    public GameManager(BattleRoyale plugin, Config cfg, ColbManager colbManager, Message message) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.colbManager = colbManager;
        this.message = message;
    }

    public void preStartGame() {
        game = new BukkitRunnable() {
            int time = 60;
            @Override
            public void run() {
                preStartTime = time;
                if (time <= 0) {
                    startGame();
                    cancel();
                    return;
                }

                if (timer(time)) {
                    val preStartMsg = message.getPreStart()
                            .replace("{time}", String.valueOf(time));
                    Bukkit.broadcastMessage(preStartMsg);
                    if (time > 3) {
                        Bukkit.getOnlinePlayers().forEach(player -> SoundUtil.playSound(player, cfg.getSoundTimePars()));
                    }
                }
                if (time <= 3) {
                    int times = time;

                    val preStartTitle = message.getTitle().replace("{time}",String.valueOf(times));

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle(preStartTitle,"",5,20,5);
                        SoundUtil.playSound(player,cfg.getSoundTitlePars());
                    });
                }
                time--;
            }

            private boolean timer(int time) {
                return time == 60
                        || time == 30
                        || time == 10
                        || time == 5
                        || time <= 4;
            }

        };
        game.runTaskTimer(plugin,0L,20L);
    }

    private void startGame() {
        if (game == null) return;
        game.cancel();
        colbManager.removeColbs();

        Bukkit.broadcastMessage(message.getStart());
        Bukkit.getOnlinePlayers()
                .forEach(player ->
                        player.sendTitle(message.getTitleStart(),""));

        isStart = true;
        pvp();
        adEnable();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootrandom start");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "deathzone start");
    }

    public void stopGame() {
        if (pvpTimer != null) {
            pvpTimer.cancel();
            pvpTimer = null;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Server server = Bukkit.getServer();
                server.setWhitelist(true);
                server.shutdown();
            }
        } .runTaskLater(plugin, 10L*20L);
    }

    private void pvp() {
        pvpTimer = new BukkitRunnable() {
            int time = cfg.getEnablePvp() * 60;
            @Override
            public void run() {
                pvpTimeLeft = time;
                if (time <= 0) {
                    isPvp = true;
                    Bukkit.broadcastMessage(message.getPvpEnable());
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(message.getTitlePvp(),""));
                    cancel();
                    return;
                }
                if (time % 30 == 0 || time <= 10) {
                    Bukkit.broadcastMessage(message.getPvpTimer()
                            .replace("{time}",String.valueOf(time)));
                }
                time--;
            }
        };
        pvpTimer.runTaskTimer(plugin, 0L, 20L);
    }

    private void adEnable() {
        adTimer = new BukkitRunnable() {
            int time = cfg.getEnableAd() * 60;

            @Override
            public void run() {
                adTimeLeft = time;
                if (time <= 0) {
                    isAd = true;
                    Bukkit.broadcastMessage(message.getAdEnable());
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(message.getTitleAd(),""));
                    cancel();
                    return;
                }
                if (time % 30 == 0 || time <= 10) {
                    Bukkit.broadcastMessage(message.getAdTimer()
                            .replace("{time}",String.valueOf(time)));
                }
                time--;
            }
        };
        adTimer.runTaskTimer(plugin, 0L, 20L);
    }
}
