package com.lojinho.bot.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.lojinho.bot.data.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLDataSource implements DatabaseManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDataSource.class);
  private final HikariDataSource ds;

  public MySQLDataSource() {
    try {
      final File dbFile = new File("db/database.db");

      if (!dbFile.exists()) {
        if (dbFile.createNewFile()) {
          LOGGER.info("Arquivo de database criado.");
        } else {
          LOGGER.info("Não foi possível criar arquivo de database.");
        }
      }

    } catch (final IOException e) {
      e.printStackTrace();
    }

    final HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://localhost:3306/lojinhobot?serverTimezone=America/Sao_Paulo");
    config.setUsername("root");
    config.setPassword("batata");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    ds = new HikariDataSource(config);

    try (final Statement statement = getConnection().createStatement()) {
      final String defaultPrefix = Config.get("PREFIX");

      statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
          + "guild_id VARCHAR(20) NOT NULL," + "prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "'" + ");");
    } catch (final SQLException e) {
      e.printStackTrace();
    }
  }

  private Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  @Override
  public String getPrefix(long guildId) {
    try (final PreparedStatement preparedStatement = this.getConnection()
        .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

      preparedStatement.setString(1, String.valueOf(guildId));

      try (final ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getString("prefix");
        }
      }

      try (final PreparedStatement insertStatement = this.getConnection()
          .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {

        insertStatement.setString(1, String.valueOf(guildId));

        insertStatement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return Config.get("PREFIX");
  }

  @Override
  public void setPrefix(long guildId, String newPrefix) {
    try (final PreparedStatement preparedStatement = this.getConnection()
        .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {
      preparedStatement.setString(1, newPrefix);
      preparedStatement.setString(2, String.valueOf(guildId));

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
