package com.lojinho.bot.db.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.DbManager;
import com.lojinho.bot.db.model.OUser;

import emoji4j.EmojiUtils;

public class CUser {

  private static Map<Long, Integer> userCache = new ConcurrentHashMap<>();
  private static Map<Integer, Long> discordCache = new ConcurrentHashMap<>();

  public static int getCachedId(long discordId) {
    return getCachedId(discordId, String.valueOf(discordId));
  }

  public static int getCachedId(long discordId, String username) {
    if (!userCache.containsKey(discordId)) {
      OUser user = findBy(discordId);
      if (user.id == 0) {
        user.discord_id = String.valueOf(discordId);
        user.name = username;
        insert(user);
      }
      if (user.name == null || user.name.isEmpty() || user.name.equals(username)) {
        user.name = EmojiUtils.shortCodify(username);
        update(user);
      }
      userCache.put(discordId, user.id);
    }
    return userCache.get(discordId);
  }

  public static long getCachedDiscordId(int userId) {
    if (!discordCache.containsKey(userId)) {
      OUser user = findBy(userId);
      if (user.id == 0) {
        return 0L;
      }
      discordCache.put(userId, Long.parseLong(user.discord_id));
    }
    return discordCache.get(userId);
  }

  public static OUser findBy(String discordId) {
    return findBy(Long.valueOf(discordId));
  }

  public static OUser findBy(long discordId) {
    OUser s = new OUser();
    try (ResultSet rs = DbManager.INSTANCE.select("SELECT * " + "FROM users " + "WHERE discord_id = ?", discordId)) {
      if (rs.next()) {
        s = fillRecord(rs);
      }
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CUser OUser findBy long discordId:");
      e.printStackTrace();
    }
    return s;
  }

  public static OUser findBy(int internalId) {
    OUser s = new OUser();
    try (ResultSet rs = DbManager.INSTANCE.select("SELECT * " + "FROM users " + "WHERE id = ?", internalId)) {
      if (rs.next()) {
        s = fillRecord(rs);
      }
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CUser OUser findBy long discordId");
    }
    return s;
  }

  public static OUser fillRecord(ResultSet rs) throws SQLException {
    OUser s = new OUser();
    s.id = rs.getInt("id");
    s.discord_id = rs.getString("discord_id");
    s.name = rs.getString("name");
    s.commands_used = rs.getInt("commands_used");
    s.banned = rs.getBoolean("banned");
    return s;
  }

  public static List<OUser> getBannedUsers() {
    List<OUser> list = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * FROM users WHERE banned = true")) {
      while (rs.next()) {
        list.add(fillRecord(rs));
      }
      rs.getStatement().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public static void registerCommandUse(int userId) {
    try {
      DbHandler.INSTANCE.query("UPDATE users SET commands_used = commands_used + 1 " + "WHERE id = ?", userId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void update(OUser record) {
    if (record.id == 0) {
      insert(record);
      return;
    }
    try {
      DbHandler.INSTANCE.query("UPDATE users SET discord_id = ?, name = ?, commands_used = ? " + "WHERE id = ?",
          record.discord_id, record.name, record.commands_used, record.id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insert(OUser record) {
    try {
      record.id = DbHandler.INSTANCE.insert(
          "INSERT INTO users (discord_id, name, commands_used, banned) " + "VALUES (?, ?, ?, ?)", record.discord_id,
          record.name, record.commands_used, record.banned);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
