package dev.fumaz.flashtags.command;

import dev.fumaz.commons.bukkit.command.VoidCommandExecutor;
import dev.fumaz.flashtags.tag.TagManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DeleteTagCommand implements VoidCommandExecutor {

    private final TagManager tagManager;

    public DeleteTagCommand(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public void onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String[] strings) {
        if (!commandSender.hasPermission("flashtags.delete")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permissions to execute this command.");
            return;
        }

        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /deletetag <tag>");
            return;
        }

        String tagName = strings[0].toLowerCase();
        tagManager.deleteTag(tagName);
        commandSender.sendMessage(ChatColor.GREEN + "Tag " + tagName + " deleted.");
    }

}
