package dev.fumaz.flashtags.command;

import dev.fumaz.commons.bukkit.command.PlayerCommandExecutor;
import dev.fumaz.flashtags.tag.Tag;
import dev.fumaz.flashtags.tag.TagManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TagCommand implements PlayerCommandExecutor {

    private final TagManager tagManager;

    public TagCommand(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull Command command, @NotNull String[] strings) {
        if (strings.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /tag <name/off>");
            return;
        }

        if (strings[0].equalsIgnoreCase("off")) {
            tagManager.disableTag(player);
            player.sendMessage(ChatColor.GREEN + "Tag disabled.");
            return;
        }

        Tag tag = tagManager.getByName(strings[0].toLowerCase());

        if (tag == null) {
            player.sendMessage(ChatColor.RED + "Tag not found.");
            return;
        }

        if (!tagManager.getOwnedTags(player).contains(tag)) {
            player.sendMessage(ChatColor.RED + "You don't own this tag.");
            return;
        }

        tagManager.selectTag(player, tag.getName());
        player.sendMessage(ChatColor.GREEN + "Tag selected.");
    }

}
