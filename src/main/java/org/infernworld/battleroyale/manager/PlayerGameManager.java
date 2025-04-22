package org.infernworld.battleroyale.manager;

import ch.tmrtrsv.teams.Teams;
import ch.tmrtrsv.teams.managers.TeamManager;
import ch.tmrtrsv.teams.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.infernworld.battleroyale.BattleRoyale;
import org.infernworld.battleroyale.fileSettings.Config;
import org.infernworld.battleroyale.fileSettings.Message;
import org.infernworld.battleroyale.util.SoundUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerGameManager {
    private final BattleRoyale plugin;
    private final ColbManager colbManager ;
    private final GameManager gameManager;
    private final Config cfg;
    private final Message message;
    private int colb = 0;
    private final Object sync = new Object();

    public PlayerGameManager(BattleRoyale plugin, ColbManager colbManager, Config cfg, Message message, GameManager gameManager) {
        this.plugin = plugin;
        this.colbManager = colbManager;
        this.cfg = cfg;
        this.message = message;
        this.gameManager = gameManager;
    }

    public void playerToColb(Player player) {
        if (player.hasPermission("battleroyale.admin")) return;

        long max = Bukkit.getOnlinePlayers().size();

        if (max > cfg.getMaxPlayer()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(message.getLimit());
                }
            }.runTaskLater(plugin, 20L);
            return;
        }

        if (colbManager.getColbs().isEmpty()) {
            plugin.getLogger().warning("Колб нет! Игроков нельзя распредялить");
            return;
        }

        World world = Bukkit.getWorld(cfg.getWorld());

        if (world == null ) {
            plugin.getLogger().info("Мир не найден! Чек кфг");
        }

        Location colbLoc = nearPlayerColbs(world);
        if (colbLoc == null) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(colbLoc);
            }
        }.runTaskLater(plugin, 20L);
        colb = (colb + 1) % colbManager.getColbs().size();
    }

    private Location nearPlayerColbs(World world) {
        List<Location> colbs = colbManager.getColbs();
        for (Location loc : colbs) {
            if (!loc.getWorld().equals(world)) {
                continue;
            }
            boolean playerNear = false;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(world) && p.getLocation().distance(loc) < 1.0) {
                    playerNear = true;
                    break;
                }
            }
            if (!playerNear) {
                return loc;
            }
        }
        return null;
    }

    public void playerCheckWin() {
        if (!gameManager.isStart) return;

        List<Player> alivePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)
                .collect(Collectors.toList());
        if (alivePlayers.isEmpty()) {
            return;
        }

        Teams teamsPlugin = (Teams) Bukkit.getPluginManager().getPlugin("Teams");
        TeamManager teamManager = (teamsPlugin != null) ? teamsPlugin.getTeamManager() : null;

        if (teamManager != null) {
            Set<Team> remainingTeams = new HashSet<>();
            for (Player player : alivePlayers) {
                Team team = teamManager.getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    remainingTeams.add(team);
                } else {
                    remainingTeams.add(null);
                }
            }

            if (remainingTeams.size() == 1) {
                Team winningTeam = remainingTeams.iterator().next();
                if (winningTeam != null) {
                    String message = this.message.getTeamWin()
                            .replace("{team}", String.valueOf(winningTeam.getId()));
                    Bukkit.broadcastMessage(message);
                    gameManager.stopGame();
                } else {
                    Player winner = alivePlayers.get(0);
                    Bukkit.broadcastMessage(this.message.getWin()
                            .replace("{player}", winner.getName()));
                    gameManager.stopGame();
                }
            }
        } else {
            if (alivePlayers.size() == 1) {
                Player winner = alivePlayers.get(0);
                Bukkit.broadcastMessage(this.message.getWin()
                        .replace("{player}", winner.getName()));
                gameManager.stopGame();
            }
        }
    }

    public void playerJoin(Player player) {
        if (gameManager.isStart) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(message.getJoinStartPlayer());
                }
            }.runTaskLater(plugin, 20L);
            return;
        }
        synchronized (sync) {
            if (Bukkit.getOnlinePlayers().size() >= cfg.getTimerMinStart()) {
                gameManager.preStartGame();
            }
        }
        AttributeInstance healthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(40.0);
            player.setHealth(40.0);
        }
        playerToColb(player);

        int online = Bukkit.getOnlinePlayers().size();
        Bukkit.broadcastMessage(message.getJoin()
                .replace("{player}", player.getName())
                .replace("{online}", String.valueOf(online)));
        for (Player p : Bukkit.getOnlinePlayers()) {
            SoundUtil.playSound(p, cfg.getSoundJoinPars());
        }
    }

    public void playerQuit(Player player){
        if (!gameManager.isStart) {
            Bukkit.broadcastMessage(message.getQuit()
                    .replace("{player}", player.getName())
                    .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size() - 1)));
            if (Bukkit.getOnlinePlayers().size() >= cfg.getTimerMinStart()) {
                gameManager.game.cancel();
                gameManager.game = null;
                Bukkit.broadcastMessage(message.getStopPre());
            }
        }
        playerCheckWin();
    }
}
