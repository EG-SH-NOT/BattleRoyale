package org.infernworld.battleroyale.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.infernworld.battleroyale.fileSettings.Message;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    private final Message message;

    public Command(Message message) {
        this.message = message;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!(player.getGameMode() == GameMode.SPECTATOR)) return true;

        if (args.length != 1) {
            player.sendMessage(message.getTpUse());
            return true;
        }

        Player p = Bukkit.getPlayerExact(args[0]);
        if (p == null) {
            player.sendMessage(message.getTpPlayerNot());
            return true;
        }

        if (player == p) {
            player.sendMessage(message.getTpTp());
            return true;
        }

        player.teleport(p);
        player.sendMessage(message.getTpPlayer().replace("{player}",p.getName()));
        return false;
    }
}
