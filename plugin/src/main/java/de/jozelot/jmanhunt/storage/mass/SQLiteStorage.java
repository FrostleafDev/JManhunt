package de.jozelot.jmanhunt.storage.mass;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.jozelot.jmanhunt.JManhunt;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLiteStorage implements ManhuntStorage {

    private final JManhunt plugin;
    private HikariDataSource dataSource;
    private String databasePrefix;

    public SQLiteStorage(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.databasePrefix = plugin.getBootstrap().getConfigManager().getDatabasePrefix();

        File storageFolder = new File(plugin.getDataFolder(), "storage");
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }

        File databaseFile = new File(storageFolder, "database.db");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());

        config.setPoolName("JManhuntSQLitePool");
        config.setMaximumPoolSize(1);
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);

        createTables();
    }

    private void createTables() {
        String[] statements = {
                "CREATE TABLE IF NOT EXISTS `" + databasePrefix + "player` (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "player_name VARCHAR(36), " +
                        "kills INT DEFAULT 0, " +
                        "deaths INT DEFAULT 0, " +
                        "lives INT DEFAULT 0, " +
                        "alive TINYINT(1) DEFAULT 1, " +
                        "team VARCHAR(20) DEFAULT 'NONE'" +
                        ");",

                "CREATE TABLE IF NOT EXISTS `" + databasePrefix + "game` (" +
                        "id INT PRIMARY KEY, " +
                        "state VARCHAR(20) DEFAULT 'SETUP', " +
                        "end_reason VARCHAR(20) DEFAULT 'NONE'" +
                        ");"
        };

        try (Connection con = dataSource.getConnection()) {
            for (String sql : statements) {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while creating SQLite tables: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Datasource is null.");
        }
        return dataSource.getConnection();
    }

    @Override
    public String getPrefix() {
        return databasePrefix;
    }
}