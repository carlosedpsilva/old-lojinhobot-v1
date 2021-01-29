package com.lojinho.bot.main;

import com.kaaz.configuration.ConfigurationOption;

public class BotConfig {

  public static final long GUILD_OWNER_MIN_ACCOUNT_AGE = 7;

  public static final int GUILD_MIN_USERS = 5;

  public static final int MAX_MESSAGE_SIZE = 2000;

  public static long DELETE_MESSAGES_AFTER = 120_000;

  public static String TEMPLATE_QUOTE = "%";

  // bot enabled?
  @ConfigurationOption
  public static boolean BOT_ENABLED = false;

  // display name
  @ConfigurationOption
  public static String BOT_NAME = "LojinhoBot";

  // token para fazer login no discord
  @ConfigurationOption
  public static String BOT_TOKEN = "token here";

  // default prefix
  @ConfigurationOption
  public static String BOT_COMMAND_PREFIX = "loj.";

  // bot server
  @ConfigurationOption
  public static String BOT_GUILD_ID = "603622898413862913";

  // owner id
  @ConfigurationOption
  public static String CREATOR_ID = "232110733578469376";
}
