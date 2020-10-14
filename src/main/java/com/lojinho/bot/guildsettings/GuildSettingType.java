package com.lojinho.bot.guildsettings;

import com.lojinho.bot.guildsettings.types.BooleanSettingType;
import com.lojinho.bot.guildsettings.types.NoSettingType;
import com.lojinho.bot.guildsettings.types.NumberBetweenSettingType;
import com.lojinho.bot.guildsettings.types.RoleSettingType;
import com.lojinho.bot.guildsettings.types.TextChannelSettingType;
import com.lojinho.bot.guildsettings.types.VoiceChannelSettingType;

public class GuildSettingType {
  public static final IGuildSettingType INTERNAL = new NoSettingType();
  public static final IGuildSettingType TOGGLE = new BooleanSettingType();
  public static final IGuildSettingType PERCENTAGE = new NumberBetweenSettingType(0, 100);
  public static final IGuildSettingType VOLUME = new NumberBetweenSettingType(0, 10);
  public static final IGuildSettingType TEXT_CHANNEL_OPTIONAL = new TextChannelSettingType(true);
  public static final IGuildSettingType TEXT_CHANNEL_MANDATORY = new TextChannelSettingType(false);
  public static final IGuildSettingType ROLE_OPTIONAL = new RoleSettingType(true);
  public static final IGuildSettingType ROLE_MANDATORY = new RoleSettingType(false);
  public static final IGuildSettingType VOICE_CHANNEL_OPTIONAL = new VoiceChannelSettingType(true);
  public static final IGuildSettingType VOICE_CHANNEL_MANDATORY = new VoiceChannelSettingType(false);
}
