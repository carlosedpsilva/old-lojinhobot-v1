package com.lojinho.bot.db;

public interface DatabaseManager {
  DatabaseManager INSTANCE = new MySQLDataSource();

  String getPrefix(long guildId);

  void setPrefix(long guildId, String newPrefix);
}
