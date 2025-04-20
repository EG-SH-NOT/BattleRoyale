package org.infernworld.battleroyale.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.infernworld.battleroyale.BattleRoyale;
import org.infernworld.battleroyale.manager.ColbManager;
import org.jetbrains.annotations.NotNull;

public class CommandAdmin implements CommandExecutor {
    private final BattleRoyale plugin;
    private final ColbManager colbManager;

    public CommandAdmin(BattleRoyale plugin, ColbManager colbManager) {
        this.plugin = plugin;
        this.colbManager = colbManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("команда только игрокам");
            return true;
        }

        if (!sender.hasPermission("battleroyale.admin")) return false;

        if (args.length < 2 || !args[0].equalsIgnoreCase("set")) {
            sender.sendMessage("юз: /battleroyale set (назв)");
            return true;
        }

        Player player = (Player) sender;
        String name = args[1];
        colbManager.saveColbLocation(name, player.getLocation());
        player.sendMessage("Точка " + name + " сохранена");
        return true;
    }
}
