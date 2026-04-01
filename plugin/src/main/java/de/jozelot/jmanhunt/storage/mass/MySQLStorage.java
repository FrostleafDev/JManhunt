package de.jozelot.jmanhunt.storage.mass;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.jozelot.jmanhunt.JManhunt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLStorage implements ManhuntStorage {

    private final JManhunt plugin;
    private HikariDataSource dataSource;
    private String databasePrefix;

    public MySQLStorage(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        var cm = plugin.getBootstrap().getConfigManager();
        this.databasePrefix = cm.getDatabasePrefix();

        HikariConfig config = new HikariConfig();

        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8",
                cm.getMysqlHost(), cm.getMysqlPort(), cm.getMysqlDatabase());

        config.setJdbcUrl(url);
        config.setUsername(cm.getMysqlUser());
        config.setPassword(cm.getMysqlPassword());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000);

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
            plugin.getLogger().severe("Error while creating MySQL tables: " + e.getMessage());
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