package dev.fumaz.flashtags.command;

import dev.fumaz.commons.bukkit.command.VoidCommandExecutor;
import dev.fumaz.flashtags.tag.Tag;
import dev.fumaz.flashtags.tag.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemoveTagCommand implements VoidCommandExecutor {

    private final TagManager tagManager;

    public RemoveTagCommand(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public void onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings) {
        if (!commandSender.hasPermission("flashtags.remove")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return;
        }

        if (strings.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /removetag <player> <tag>");
            return;
        }

        String playerName = strings[0];
        String tagName = strings[1].toLowerCase();

        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        Tag tag = tagManager.getByName(tagName);

        if (tag == null) {
            commandSender.sendMessage(ChatColor.RED + "Tag not found");
            return;
        }

        tagManager.removeTag(player, tag.getName());
        commandSender.sendMessage(ChatColor.GREEN + "Removed from " + player.getName() + " the tag " + tag.getName());
    }

}
