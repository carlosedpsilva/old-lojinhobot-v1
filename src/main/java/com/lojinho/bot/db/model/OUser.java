package com.lojinho.bot.db.model;

public class OUser {
  public int id;
  public String discord_id;
  public String name;
  public int commands_used;
  public boolean banned;

  public OUser() {
    id = 0;
    discord_id = "";
    name = "";
    commands_used = 0;
    banned = false;
  }

  public boolean isBanned() {
    return banned;
  }
}
