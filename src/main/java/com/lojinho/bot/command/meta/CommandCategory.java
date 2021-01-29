package com.lojinho.bot.command.meta;

import com.lojinho.bot.permission.UserRank;
import com.lojinho.bot.util.Emojibet;

public enum CommandCategory {
  OWNER("owner", Emojibet.MAN_IN_SUIT, "Development", UserRank.CREATOR),
  MODERATION("moderation", Emojibet.POLICE, "Administration", UserRank.GUILD_ADMIN),
  INFORMATIVE("informative", Emojibet.INFORMATION, "Information"), ROLES("roles", Emojibet.ORANGE_CIRCLE, "Roles"),
  UNKNOWN("nopackage", Emojibet.QUESTION_MARK, "Misc");

  private final String packageName;
  private final String emoticon;
  private final String displayName;
  private final UserRank rankRequired;

  CommandCategory(String packageName, String emoticon, String displayName) {
    this.packageName = packageName;
    this.emoticon = emoticon;
    this.displayName = displayName;
    this.rankRequired = UserRank.USER;
  }

  CommandCategory(String packageName, String emoticon, String displayName, UserRank rankRequired) {
    this.packageName = packageName;
    this.emoticon = emoticon;
    this.displayName = displayName;
    this.rankRequired = rankRequired;
  }

  public static CommandCategory getFirstWithPermission(UserRank rank) {
    if (rank == null) {
      return INFORMATIVE;
    }
    for (CommandCategory category : values()) {
      if (rank.isAtLeast(category.getRankRequired())) {
        return category;
      }
    }
    return INFORMATIVE;
  }

  public static CommandCategory fromPackage(String packageName) {
    if (packageName != null) {
      for (CommandCategory cc : values()) {
        if (packageName.equalsIgnoreCase(cc.packageName)) {
          return cc;
        }
      }
    }
    return UNKNOWN;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getEmoticon() {
    return emoticon;
  }

  public UserRank getRankRequired() {
    return rankRequired;
  }
}
