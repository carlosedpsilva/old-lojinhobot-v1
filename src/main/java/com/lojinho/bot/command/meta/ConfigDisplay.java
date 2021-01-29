package com.lojinho.bot.command.meta;

import com.lojinho.bot.permission.UserRank;

public class ConfigDisplay {
  public final String tag;
  public final UserRank rank;

  public ConfigDisplay(String tag, UserRank rank) {
    this.tag = tag;
    this.rank = rank;
  }
}