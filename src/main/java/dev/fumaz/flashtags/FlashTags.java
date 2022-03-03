package dev.fumaz.flashtags;

import dev.fumaz.commons.bukkit.misc.Logging;
import dev.fumaz.flashtags.command.CreateTagCommand;
import dev.fumaz.flashtags.command.DeleteTagCommand;
import dev.fumaz.flashtags.command.GiveTagCommand;
import dev.fumaz.flashtags.command.RemoveTagCommand;
import dev.fumaz.flashtags.command.TagCommand;
import dev.fumaz.flashtags.database.DatabaseManager;
import dev.fumaz.flashtags.tag.TagManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FlashTags extends JavaPlugin {

    private DatabaseManager databaseManager;
    private TagManager tagManager;

    @Override
    public void onEnable() {
        Logging.splash(this);

        databaseManager = new DatabaseManager(this);
        tagManager = new TagManager(this);

        getCommand("tag").setExecutor(new TagCommand(tagManager));
        getCommand("givetag").setExecutor(new GiveTagCommand(tagManager));
        getCommand("createtag").setExecutor(new CreateTagCommand(tagManager));
        getCommand("deletetag").setExecutor(new DeleteTagCommand(tagManager));
        getCommand("removetag").setExecutor(new RemoveTagCommand(tagManager));
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

}
