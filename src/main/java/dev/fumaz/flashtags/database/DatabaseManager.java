package dev.fumaz.flashtags.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.fumaz.flashtags.FlashTags;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {

    private final FlashTags plugin;
    private final HikariDataSource source;
    private final ExecutorService executor;

    public DatabaseManager(FlashTags plugin) {
        this.plugin = plugin;
        this.executor = Executors.newFixedThreadPool(4);

        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        source = new HikariDataSource();
        source.setJdbcUrl("jdbc:mysql://" + configuration.getString("database.host") + "/" + configuration.getString("database.database") + "?useUnicode=yes&characterEncoding=UTF-8");
        source.setUsername(configuration.getString("database.username"));
        source.setPassword(configuration.getString("database.password"));
        source.setLeakDetectionThreshold(45 * 1000);
        source.setPoolName("FlashTags");

        try (Connection connection = source.getConnection()) {
            plugin.getLogger().info("Database connected successfully.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to database.");
            e.printStackTrace();

            Bukkit.shutdown();
        }
    }

    public void useConnection(SQLConsumer<Connection> consumer) {
        try (Connection connection = getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void useConnectionAsynchronously(SQLConsumer<Connection> consumer) {
        executor.submit(() -> useConnection(consumer));
    }

    private Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
