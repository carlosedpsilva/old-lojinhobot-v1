package com.lojinho.bot.db.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.model.OGuildMember;

public class CGuildMember {

  public static OGuildMember findBy(long guildDiscordId, long userDiscordId) {
    return findBy(CGuild.getCachedId(guildDiscordId), CUser.getCachedId(userDiscordId));
  }

  public static OGuildMember findBy(int guildId, int userId) {
    OGuildMember record = new OGuildMember();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT *  " + "FROM guild_member " + "WHERE guild_id = ? AND user_id = ? ", guildId, userId)) {
      if (rs.next()) {
        record = fillRecord(rs);
      } else {
        record.guildId = guildId;
        record.userId = userId;
      }
      rs.getStatement().close();
    } catch (Exception e) {
      System.out.println("Erro fatal ao executar CGuildMember OGuildMember findBy guildId userId:");
      e.printStackTrace();
    }
    return record;
  }

  private static OGuildMember fillRecord(ResultSet resultset) throws SQLException {
    OGuildMember record = new OGuildMember();
    record.guildId = resultset.getInt("guild_id");
    record.userId = resultset.getInt("user_id");
    record.joinDate = resultset.getTimestamp("join_date");
    return record;
  }

  public static void insertOrUpdate(OGuildMember record) {
    try {
      DbHandler.INSTANCE.insert(
          "INSERT INTO guild_member (guild_id, user_id, join_date) "
              + "VALUES ( ?, ?, ? ) ON DUPLICATE KEY UPDATE join_date = ?",
          record.guildId, record.userId, record.joinDate, record.joinDate);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
