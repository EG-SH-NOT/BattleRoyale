package org.infernworld.battleroyale.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Player player = (Player) sender;

        if (!(player.getGameMode() == GameMode.SPECTATOR)) return List.of();

        if (args.length == 1) {
            return getPlayers(args[0]);
        }
        return List.of();
    }

    private List<String> getPlayers(String str) {
        List<String> players = new ArrayList<>();
        String string = str.toLowerCase();
        for (Player p : Bukkit.getOnlinePlayers()) {
            String player = p.getName();
            if (player.toLowerCase().startsWith(string)) {
                players.add(player);
            }
        }
        return players;
    }
}
