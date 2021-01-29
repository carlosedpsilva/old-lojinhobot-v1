package com.lojinho.bot.db.model.role;

// CREATE TABLE `guild_roles` (
//   `id` int(11) NOT NULL AUTO_INCREMENT,
//   `discord_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL UNIQUE
//   `guild_id` int(11) NOT NULL,
//   `role_category` int(11) NOT NULL,
//   PRIMARY KEY (`id`)

public class OGuildRole {
  public int id = 0;
  public int guild_id = 0;
  public int role_category = 0;
  public String discord_id = "";

  public OGuildRole() {
    id = 0;
    guild_id = 0;
    role_category = 0;
    discord_id = "";
  }
}
