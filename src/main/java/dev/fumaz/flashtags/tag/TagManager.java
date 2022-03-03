package dev.fumaz.flashtags.tag;

import dev.fumaz.commons.bukkit.misc.Scheduler;
import dev.fumaz.flashtags.FlashTags;
import dev.fumaz.flashtags.database.DatabaseManager;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TagManager {

    // language=MySQL
    private final static String CREATE_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS `tags`(`name` VARCHAR(64) NOT NULL PRIMARY KEY, `display` VARCHAR(255) NOT NULL)";

    // language=MySQL
    private final static String CREATE_PLAYERS_TABLE = "CREATE TABLE IF NOT EXISTS `owned_tags`(`uuid` VARCHAR(36) NOT NULL, `tag` VARCHAR(64) NOT NULL, PRIMARY KEY(`uuid`, `tag`))";

    // language=MySQL
    private final static String CREATE_SELECTED_TABLE = "CREATE TABLE IF NOT EXISTS `selected_tags`(`uuid` VARCHAR(36) NOT NULL PRIMARY KEY , `tag` VARCHAR(64) NOT NULL)";

    // language=MySQL
    private final static String SELECT_TAGS = "SELECT * FROM `tags`";

    private final FlashTags plugin;
    private final DatabaseManager databaseManager;
    private final TagListener listener;

    private final Set<Tag> tags;
    private final Map<UUID, Set<String>> ownedTags;
    private final Map<UUID, String> selectedTags;

    public TagManager(FlashTags plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.listener = new TagListener(plugin, this);

        this.tags = new HashSet<>();
        this.ownedTags = new HashMap<>();
        this.selectedTags = new HashMap<>();

        // Run this synchronously since we need the tables before everything else
        databaseManager.useConnection(connection -> {
            connection.prepareStatement(CREATE_TAGS_TABLE).execute();
            connection.prepareStatement(CREATE_PLAYERS_TABLE).execute();
            connection.prepareStatement(CREATE_SELECTED_TABLE).execute();
        });

        Scheduler.of(plugin).runTaskTimerAsynchronously(this::refreshTags, 0, 20 * 60);

        plugin.getLogger().info("Loaded " + tags.size() + " tags!");
    }

    public Tag getByName(String name) {
        return tags.stream()
                .filter(tag -> tag.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void createTag(String name, String display) {
        tags.add(new Tag(name, display));

        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `tags` (`name`, `display`) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setString(2, display);
            statement.execute();
        });
    }

    public void deleteTag(String name) {
        tags.remove(getByName(name));

        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `tags` WHERE `name` = ?");
            statement.setString(1, name);
            statement.execute();
        });
    }

    public void giveTag(Player player, String tagName) {
        Tag tag = getByName(tagName);

        if (tag == null) {
            return;
        }

        Set<String> playerTags = ownedTags.computeIfAbsent(player.getUniqueId(), uuid -> new HashSet<>());
        playerTags.add(tagName);

        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `owned_tags` (`uuid`, `tag`) VALUES (?, ?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, tagName);
            statement.execute();
        });
    }

    public void removeTag(Player player, String tagName) {
        Set<String> playerTags = ownedTags.get(player.getUniqueId());

        if (playerTags == null) {
            return;
        }

        playerTags.remove(tagName);

        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `owned_tags` WHERE `uuid` = ? AND `tag` = ?");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, tagName);
            statement.execute();
        });
    }

    public void disableTag(Player player) {
        selectedTags.remove(player.getUniqueId());

        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `selected_tags` WHERE `uuid` = ?");
            statement.setString(1, player.getUniqueId().toString());
            statement.execute();
        });
    }

    public void selectTag(Player player, String tagName) {
        selectedTags.put(player.getUniqueId(), tagName);

        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `selected_tags` (`uuid`, `tag`) VALUES (?, ?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, tagName);
            statement.execute();
        });
    }

    public Set<Tag> getOwnedTags(Player player) {
        Set<Tag> owned = ownedTags.getOrDefault(player.getUniqueId(), new HashSet<>())
                .stream()
                .map(this::getByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Tag tag : tags) {
            if (player.hasPermission(tag.getPermission())) {
                owned.add(tag);
            }
        }

        return owned;
    }

    public Tag getSelectedTag(Player player) {
        return getByName(selectedTags.getOrDefault(player.getUniqueId(), ""));
    }

    public void loadOwnedTags(Player player) {
        ownedTags.put(player.getUniqueId(), new HashSet<>());

        databaseManager.useConnectionAsynchronously(connection -> {
            {
                PreparedStatement statement = connection.prepareStatement("SELECT `tag` FROM `owned_tags` WHERE `uuid` = ?");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    ownedTags.get(player.getUniqueId()).add(resultSet.getString("tag"));
                }
            }

            {
                PreparedStatement statement = connection.prepareStatement("SELECT `tag` FROM `selected_tags` WHERE `uuid` = ?");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    selectedTags.put(player.getUniqueId(), resultSet.getString("tag"));
                }
            }
        });
    }

    private void refreshTags() {
        databaseManager.useConnectionAsynchronously(connection -> {
            PreparedStatement statement = connection.prepareStatement(SELECT_TAGS);
            ResultSet resultSet = statement.executeQuery();

            tags.clear();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String display = resultSet.getString("display");
                Tag tag = new Tag(name, display);

                tags.add(tag);
            }
        });
    }


}
