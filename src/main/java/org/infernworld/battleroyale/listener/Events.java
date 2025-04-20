package org.infernworld.battleroyale.listener;

import lombok.val;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.infernworld.battleroyale.BattleRoyale;
import org.infernworld.battleroyale.fileSettings.Message;
import org.infernworld.battleroyale.manager.GameManager;
import org.infernworld.battleroyale.manager.PlayerGameManager;

public class Events implements Listener {
    private final PlayerGameManager playerGameManager;
    private final GameManager gameManager;
    private final Message message;
    private final BattleRoyale plugin;

    public Events(BattleRoyale plugin, PlayerGameManager playerGameManager,
                  GameManager gameManager,
                  Message message) {
        this.playerGameManager = playerGameManager;
        this.gameManager = gameManager;
        this.message = message;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        playerGameManager.playerQuit(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        playerGameManager.playerJoin(player);
    }

    @EventHandler
    public void onBreakEvent(BlockBreakEvent e) {
        if (!gameManager.isStart) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceEvent(BlockPlaceEvent e) {
        if (!gameManager.isStart) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            Bukkit.getLogger().info("[BattleRoyale] Сервер загружен");
            new BukkitRunnable() {
                @Override
                public void run() {
                    Server server = Bukkit.getServer();
                    server.setWhitelist(false);
                    Bukkit.getLogger().info("[BattleRoyale] вайтлист отключен");
                    World world = Bukkit.getWorld(plugin.getCfg().getWorld());
                    WorldBorder border = world.getWorldBorder();
                    border.setSize(4000);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "teams disband");
                }
            }.runTaskLater(plugin, 5 * 20);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageEvent(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;
        if (!gameManager.isPvp) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent e) {
        val player = e.getPlayer();

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && !gameManager.isAd) {
            player.sendMessage(message.getDisableAd());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent e) {
        final Player death = e.getEntity().getPlayer();

        Location deadLoc = death.getLocation();
        World world = deadLoc.getWorld();
        world.strikeLightningEffect(deadLoc);

        death.setGameMode(GameMode.SPECTATOR);
        long alivePlayers = Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode() != GameMode.SPECTATOR).count();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message.getDead()
                    .replace("{player}", death.getName())
                    .replace("{lifePlayers}", String.valueOf(alivePlayers)));
        }
        playerGameManager.playerCheckWin();

    }

    @EventHandler
    public void onEatEvent(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
        }
    }
}
