package com.lojinho.bot.db.model;

public class OGuild {
  public int id;
  public String discord_id;
  public String name;
  public int owner;
  public boolean active;
  public boolean banned;

  public OGuild() {
    id = 0;
    discord_id = "";
    name = "";
    owner = 0;
    active = false;
    banned = false;
  }

  public boolean isBanned() {
    return banned;
  }
}
