package com.lojinho.bot.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import com.lojinho.bot.data.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbHandler implements DbManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(DbHandler.class);
  private final HikariDataSource ds;

  public DbHandler() {
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
    config.addDataSourceProperty("minimumIdle", "2");
    config.addDataSourceProperty("maximumPoolSize", "10");
    config.addDataSourceProperty("idleTimeout", "120000");
    config.addDataSourceProperty("connectionTimeout", "300000");
    config.addDataSourceProperty("leakDetectionThreshold", "300000");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    config.addDataSourceProperty("useLocalSessionState", "true");
    config.addDataSourceProperty("rewriteBatchedStatements", "true");
    config.addDataSourceProperty("cacheResultSetMetadata", "true");
    config.addDataSourceProperty("cacheServerConfiguration", "true");
    config.addDataSourceProperty("elideSetAutoCommits", "true");
    config.addDataSourceProperty("maintainTimeStats", "false");

    ds = new HikariDataSource(config);
  }

  private void resolveParameters(PreparedStatement query, Object... params) throws SQLException {
    int index = 1;
    for (Object p : params) {
      if (p instanceof String) {
        query.setString(index, (String) p);
      } else if (p instanceof Integer) {
        query.setInt(index, (int) p);
      } else if (p instanceof Long) {
        query.setLong(index, (Long) p);
      } else if (p instanceof Double) {
        query.setDouble(index, (double) p);
      } else if (p instanceof Boolean) {
        query.setBoolean(index, (Boolean) p);
      } else if (p instanceof java.sql.Date) {
        java.sql.Date d = (java.sql.Date) p;
        Timestamp ts = new Timestamp(d.getTime());
        query.setTimestamp(index, ts);
      } else if (p instanceof java.util.Date) {
        java.util.Date d = (java.util.Date) p;
        Timestamp ts = new Timestamp(d.getTime());
        query.setTimestamp(index, ts);
      } else if (p instanceof Calendar) {
        Calendar cal = (Calendar) p;
        Timestamp ts = new Timestamp(cal.getTimeInMillis());
        query.setTimestamp(index, ts);
      } else if (p == null) {
        query.setNull(index, Types.NULL);
      } else {
        LOGGER.error("UnimplementedParameterException");
      }
      index++;
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  @Override
  public ResultSet select(String sql, Object... params) throws SQLException {
    try (Connection conn = getConnection();
        Statement statement = conn.createStatement();
        PreparedStatement query = conn.prepareStatement(sql)) {
      try {
        statement.executeUpdate("SET NAMES utf8mb4");
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.out.println("COULD NOT SET utf8mb4");
      }
      resolveParameters(query, params);
      return query.executeQuery();
    }
  }

  @Override
  public int query(String sql) throws SQLException {
    try (Connection conn = getConnection();
        Statement statement = conn.createStatement()) {
      try {
        statement.executeUpdate("SET NAMES utf8mb4");
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.out.println("COULD NOT SET utf8mb4");
      }
      return statement.executeUpdate(sql);
    }
  }

  @Override
  public int query(String sql, Object... params) throws SQLException {
    try (Connection conn = getConnection();
        Statement statement = conn.createStatement();
        PreparedStatement query = conn.prepareStatement(sql)) {
      try {
        statement.executeUpdate("SET NAMES utf8mb4");
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.out.println("COULD NOT SET utf8mb4");
      }
      resolveParameters(query, params);
      return query.executeUpdate();
    }
  }

  @Override
  public int insert(String sql, Object... params) throws SQLException {
    try (Connection conn = getConnection();
        Statement statement = conn.createStatement();
        PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      try {
        statement.executeUpdate("SET NAMES utf8mb4");
      } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.out.println("COULD NOT SET utf8mb4");
      }
      resolveParameters(query, params);
      query.executeUpdate();
      ResultSet rs = query.getGeneratedKeys();

      if (rs.next()) {
        return rs.getInt(1);
      }
    }
    return -1;
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
