package dev.fumaz.flashtags.command;

import dev.fumaz.commons.bukkit.command.VoidCommandExecutor;
import dev.fumaz.flashtags.tag.TagManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CreateTagCommand implements VoidCommandExecutor {

    private final TagManager tagManager;

    public CreateTagCommand(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public void onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings) {
        if (!commandSender.hasPermission("flashtags.create")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return;
        }

        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /createtag <name> <display>");
            return;
        }

        String name = strings[0].toLowerCase();
        String display = String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));

        if (tagManager.getByName(name) != null) {
            commandSender.sendMessage(ChatColor.RED + "A tag with that name already exists.");
            return;
        }

        tagManager.createTag(name, display);
        commandSender.sendMessage(ChatColor.GREEN + "Tag created successfully.");
    }

}
