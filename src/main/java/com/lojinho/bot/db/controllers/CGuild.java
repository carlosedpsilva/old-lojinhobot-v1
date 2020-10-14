package com.lojinho.bot.db.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.model.OGuild;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

/*
  @Fields id, discord_id, name, owner, active, banned
*/

public class CGuild {
  private static Map<Long, Integer> guildIdCache = new ConcurrentHashMap<>();
  private static Map<Integer, Long> discordIdCache = new ConcurrentHashMap<>();

  public static int getCachedId(MessageChannel channel) {
    if (channel instanceof TextChannel) {
      return getCachedId(((TextChannel) channel).getGuild().getIdLong());
    }
    return 0;
  }

  public static int getCachedId(long discordId) {
    if (!guildIdCache.containsKey(discordId)) {
      OGuild guild = findBy(discordId);
      if (guild.id == 0) {
        guild.discord_id = String.valueOf(discordId);
        guild.name = Long.toString(discordId);
        insert(guild);
      }
      guildIdCache.put(discordId, guild.id);
    }
    return guildIdCache.get(discordId);
  }

  public static String getCachedDiscordId(int id) {
    if (!discordIdCache.containsKey(id)) {
      OGuild guild = findById(id);
      if (guild.id == 0) {
        return "0";
      }
      discordIdCache.put(id, Long.parseLong(guild.discord_id));
    }
    return Long.toString(discordIdCache.get(id));
  }

  public static OGuild findBy(long discordId) {
    return findBy(String.valueOf(discordId));
  }

  public static OGuild findBy(String discordId) {
    OGuild s = new OGuild();
    try (ResultSet rs = DbHandler.INSTANCE.select(
        "SELECT id, discord_id, name, owner,active,banned  " + "FROM guilds " + "WHERE discord_id = ? ", discordId)) {
      if (rs.next()) {
        s = loadRecord(rs);
      }
      rs.getStatement().close();
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CGuild OGuild findBy discordId");
    }
    return s;
  }

  public static OGuild findById(int id) {
    OGuild s = new OGuild();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT id, discord_id, name, owner,active,banned  " + "FROM guilds " + "WHERE id = ? ", id)) {
      if (rs.next()) {
        s = loadRecord(rs);
      }
      rs.getStatement().close();
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CGuild OGuild findById id");
    }
    return s;
  }

  public static void update(OGuild record) {
    if (record.id == 0) {
      insert(record);
      return;
    }
    try {
      DbHandler.INSTANCE.query(
          "UPDATE guilds SET discord_id = ?, name = ?, owner = ?, active = ?, banned = ? " + "WHERE id = ? ",
          record.discord_id, record.name, record.owner == 0 ? null : record.owner, record.active, record.banned,
          record.id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insert(OGuild record) {
    try {
      record.id = DbHandler.INSTANCE.insert(
          "INSERT INTO guilds (discord_id, name, owner, active, banned)" +
          "VALUES (?, ?, ?, ?, ?)", record.discord_id,
          record.name, record.owner == 0 ? null : record.owner, record.active, record.banned);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static int getActiveGuildCount() {
    int amount = 0;
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT COUNT(id) AS `amount` FROM guilds WHERE active = true")) {
      while (rs.next()) {
        amount = rs.getInt("amount");
      }
      rs.getStatement().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return amount;
  }

  public static List<OGuild> getBannedGuilds() {
    List<OGuild> list = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * FROM guilds WHERE banned = true")) {
      while (rs.next()) {
        list.add(loadRecord(rs));
      }
      rs.getStatement().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  private static OGuild loadRecord(ResultSet rs) throws SQLException {
    OGuild s = new OGuild();
    s.id = rs.getInt("id");
    s.discord_id = rs.getString("discord_id");
    s.name = rs.getString("name");
    s.owner = rs.getInt("owner");
    s.active = rs.getBoolean("active");
    s.banned = rs.getBoolean("banned");
    return s;
  }
}
