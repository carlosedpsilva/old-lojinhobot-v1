package com.lojinho.bot.db.model.role;

// CREATE TABLE `guild_role_categories` (
//   `id` int(11) NOT NULL AUTO_INCREMENT,
//   `category_type` int(11) NOT NULL UNIQUE,
//   `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
//   PRIMARY KEY (`id`)

public class ORoleCategory {
  public int id;
  public int guild_id;
  public int category_type;
  public String category_key;

  public ORoleCategory() {
    id = 0;
    guild_id = 0;
    category_type = 0;
    category_key = "";
  }
}
