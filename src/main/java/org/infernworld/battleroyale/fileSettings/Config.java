package org.infernworld.battleroyale.fileSettings;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.infernworld.battleroyale.util.ColorUtil;

@Getter
public class Config {
    private int maxPlayer,
            timerMinStart,
            enablePvp,
            enableAd;
    private String soundDead,
            soundJoin,
            soundTitle,
            soundTime,
            phasePvp,
            phaseAd,
            phasePreStart,
            world,
            blockRemove;
    private Sound
            soundJoinPars,
            soundTitlePars,
            soundTimePars,
            soundDeadPars;

    public Config(FileConfiguration cfg) {
        ConfigurationSection set = cfg.getConfigurationSection("settings");
        if (set == null) return;

        this.maxPlayer = set.getInt("max-player-event");
        this.timerMinStart = set.getInt("timer-min-start");
        this.enablePvp = set.getInt("enable-pvp");
        this.enableAd = set.getInt("enable-ad");
        this.phasePvp = ColorUtil.getColor(set.getString("phase-pvp"));
        this.phaseAd = ColorUtil.getColor(set.getString("phase-ad"));
        this.phasePreStart = ColorUtil.getColor(set.getString("phase-pre-start"));
        this.world = set.getString("world");
        this.soundJoin = set.getString("sound-join-game","ENTITY_ILLUSIONER_PREPARE_MIRROR");
        this.blockRemove = set.getString("block-remove");
        if (this.soundJoin != null) {
            try {
                this.soundJoinPars = Sound.valueOf(this.soundJoin);
            } catch (IllegalArgumentException e) {
                this.soundJoinPars  = null;
                Bukkit.getLogger().warning("Звук " + this.soundJoin + " не найден. Проверьте конфиг!");
            }
        }

        this.soundTitle = set.getString("sound-title");
        if (this.soundTitle != null) {
            try {
                this.soundTitlePars = Sound.valueOf(this.soundTitle);
            } catch (IllegalArgumentException e) {
                this.soundTitlePars  = null;
                Bukkit.getLogger().warning("Звук " + this.soundTitle + " не найден. Проверьте конфиг!");
            }
        }

        this.soundTime = set.getString("sound-time");
        if (this.soundTime != null) {
            try {
                this.soundTimePars = Sound.valueOf(this.soundTime);
            } catch (IllegalArgumentException e) {
                this.soundTimePars  = null;
                Bukkit.getLogger().warning("Звук " + this.soundTime + " не найден. Проверьте конфиг!");
            }
        }

        this.soundDead = set.getString("sound-dead");
        if (this.soundTime != null) {
            try {
                this.soundDeadPars = Sound.valueOf(this.soundDead);
            } catch (IllegalArgumentException e) {
                this.soundDeadPars  = null;
                Bukkit.getLogger().warning("Звук " + this.soundDead + " не найден. Проверьте конфиг!");
            }
        }
    }
}

