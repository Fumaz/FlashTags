package dev.fumaz.flashtags.tag;

import dev.fumaz.commons.bukkit.interfaces.FListener;
import dev.fumaz.flashtags.FlashTags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TagListener implements FListener {

    private final TagManager tagManager;

    public TagListener(FlashTags plugin, TagManager tagManager) {
        this.tagManager = tagManager;

        register(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        tagManager.loadOwnedTags(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Tag tag = tagManager.getSelectedTag(event.getPlayer());

        if (tag == null) {
            tag = new Tag("", "");
        }

        String format = event.getFormat().replace("{TAG}", tag.getDisplay() + (tag.getName().isEmpty() ? "" : " "));
        event.setFormat(format);
    }

}
