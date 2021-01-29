package com.lojinho.bot.permission;

public enum UserRank {
  BANNED_USER("Will be ignored"), BOT("Will be ignored"), USER("Regular user"), GUILD_ADMIN("Admin in a guild"),
  GUILD_OWNER("Owner of a guild"), BOT_ADMIN("Bot administrator"), CREATOR("Creator");

  private final String description;

  UserRank(String description) {
    this.description = description;
  }

  /**
   * Encontra um rank pelo nome
   *
   * @param search o cargo a ser buscado
   * @return rank || null
   */
  public static UserRank findRank(String search) {
    for (UserRank rank : values()) {
      if (rank.name().equalsIgnoreCase(search)) {
        return rank;
      }
    }
    return null;
  }

  public boolean isAtLeast(UserRank rank) {
    return this.ordinal() >= rank.ordinal();
  }

  public boolean isHigherThan(UserRank rank) {
    return this.ordinal() > rank.ordinal();
  }

  public String description() {
    return description;
  }
}
