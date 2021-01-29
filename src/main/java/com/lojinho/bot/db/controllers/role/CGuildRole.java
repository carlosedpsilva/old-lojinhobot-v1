package com.lojinho.bot.db.controllers.role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.model.role.OGuildRole;

import net.dv8tion.jda.api.entities.Role;

// CREATE TABLE `guild_roles` (
//   `id` int(11) NOT NULL AUTO_INCREMENT,
//   `discord_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL UNIQUE
//   `guild_id` int(11) NOT NULL,
//   `role_category` int(11) NOT NULL,
//   PRIMARY KEY (`id`)

public class CGuildRole {
  private static Map<Long, Integer> roleIdCache = new ConcurrentHashMap<>();
  private static Map<Integer, Long> discordIdCache = new ConcurrentHashMap<>();

  public static int getCachedId(Role role) {
    return getCachedId(role.getIdLong());

  }

  public static int getCachedId(long discordId) {
    if (!roleIdCache.containsKey(discordId)) {
      OGuildRole role = findBy(discordId);
      if (role.id == 0) {
        role.discord_id = String.valueOf(discordId);
        insert(role);
      }
      roleIdCache.put(discordId, role.id);
    }
    return roleIdCache.get(discordId);
  }

  public static String getCachedDiscordId(int id) {
    if (!discordIdCache.containsKey(id)) {
      OGuildRole role = findById(id);
      if (role.id == 0) {
        return "0";
      }
      discordIdCache.put(id, Long.parseLong(role.discord_id));
    }
    return Long.toString(discordIdCache.get(id));
  }

  public static OGuildRole findBy(long discordId) {
    return findBy(String.valueOf(discordId));
  }

  public static OGuildRole findBy(String discordId) {
    OGuildRole s = new OGuildRole();
    try (ResultSet rs = DbHandler.INSTANCE.select(
        "SELECT id, discord_id, guild_id, role_category  " + "FROM guild_roles " + "WHERE discord_id = ? ",
        discordId)) {
      if (rs.next()) {
        s = loadRecord(rs);
      }
      rs.getStatement().close();
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CGuildRole OGuildRole findBy discordId");
    }
    return s;
  }

  public static OGuildRole findById(int id) {
    OGuildRole s = new OGuildRole();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT id, discord_id, guild_id, role_category " + "FROM guild_roles " + "WHERE id = ? ", id)) {
      if (rs.next()) {
        s = loadRecord(rs);
      }
      rs.getStatement().close();
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CGuildRole OGuildRole findById id");
    }
    return s;
  }

  public static void update(OGuildRole record) {
    if (record.id == 0) {
      insert(record);
      return;
    }
    try {
      DbHandler.INSTANCE.query(
          "UPDATE guild_roles SET discord_id = ?, guild_id = ?, role_category = ? " + "WHERE id = ? ", record.id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insert(OGuildRole record) {
    try {
      record.id = DbHandler.INSTANCE.insert(
          "INSERT INTO guild_roles (discord_id, guild_id, role_category)" + "VALUES (?, ?, ?)", record.discord_id,
          record.guild_id, record.role_category);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static OGuildRole loadRecord(ResultSet rs) throws SQLException {
    OGuildRole s = new OGuildRole();
    s.id = rs.getInt("id");
    s.discord_id = rs.getString("discord_id");
    s.guild_id = rs.getInt("guild_id");
    s.role_category = rs.getInt("role_category");
    return s;
  }
}
