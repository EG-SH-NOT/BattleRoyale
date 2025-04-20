package org.infernworld.battleroyale.fileSettings;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.infernworld.battleroyale.util.ColorUtil;

@Getter
public class Message {
    private String limit, preStart, title,
            start, join, joinStartPlayer,
            pvpEnable, pvpTimer, dead,
            adEnable, disableAd, stopPre,
            quit, tpPlayerNot, tpUse,
            tpTp,tpPlayer, titleAd,
            titlePvp, adTimer, win,
            teamWin, titleStart;

    public Message(FileConfiguration msg) {
        ConfigurationSection message = msg.getConfigurationSection("message");
        ConfigurationSection title = msg.getConfigurationSection("title");
        if (message == null || title == null) return;

        this.limit = ColorUtil.getColor(message.getString("limit-player"));
        this.preStart = ColorUtil.getColor(message.getString("pre-start"));
        this.start = ColorUtil.getColor(message.getString("start"));
        this.join = ColorUtil.getColor(message.getString("player-join"));
        this.joinStartPlayer = ColorUtil.getColor(message.getString("join-player-gameStart"));
        this.pvpEnable = ColorUtil.getColor(message.getString("pvp-enable"));
        this.pvpTimer = ColorUtil.getColor(message.getString("pvp-timer"));
        this.adTimer = ColorUtil.getColor(message.getString("ad-timer"));
        this.dead = ColorUtil.getColor(message.getString("dead"));
        this.adEnable = ColorUtil.getColor(message.getString("ad-enable"));
        this.disableAd = ColorUtil.getColor(message.getString("ad-disable-cause"));
        this.quit = ColorUtil.getColor(message.getString("quit-player"));
        this.stopPre = ColorUtil.getColor(message.getString("stop-pre-start"));

        this.win = ColorUtil.getColor(message.getString("player-win"));
        this.teamWin = ColorUtil.getColor(message.getString("team-win"));

        this.tpPlayer = ColorUtil.getColor(message.getString("tp-player"));
        this.tpTp = ColorUtil.getColor(message.getString("tp-tp"));
        this.tpUse = ColorUtil.getColor(message.getString("tp-use"));
        this.tpPlayerNot = ColorUtil.getColor(message.getString("tp-player-not"));

        this.title = ColorUtil.getColor(title.getString("title-send"));
        this.titleAd = ColorUtil.getColor(title.getString("title-ad"));
        this.titlePvp = ColorUtil.getColor(title.getString("title-pvp"));
        this.titleStart = ColorUtil.getColor(title.getString("title-start"));
    }
}
