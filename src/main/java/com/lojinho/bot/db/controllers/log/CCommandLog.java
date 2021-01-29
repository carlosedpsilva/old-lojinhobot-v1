package com.lojinho.bot.db.controllers.log;

import java.sql.Date;
import java.sql.SQLException;

import com.lojinho.bot.db.DbHandler;

public class CCommandLog {

  public static void saveLog(int userId, int guildId, String commandUsed, String commandArgs) {
    try {
      DbHandler.INSTANCE.insert(
          "INSERT INTO command_log(user_id, guild_id, command, args, execute_date" +
          "VALUES (?, ?, ?, ?, ?)", userId, guildId, commandUsed, commandArgs, new Date(System.currentTimeMillis()));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
