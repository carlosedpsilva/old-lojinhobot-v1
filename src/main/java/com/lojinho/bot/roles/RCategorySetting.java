package com.lojinho.bot.roles;

import com.lojinho.bot.roles.settings.InterfaceSetting;
import com.lojinho.bot.roles.settings.NoSetting;
import com.lojinho.bot.roles.settings.OnJoinSetting;
import com.lojinho.bot.roles.settings.SelfAssignSetting;

public class RCategorySetting {
  public static final IRoleCategorySetting DEFAULT = new NoSetting();
  public static final IRoleCategorySetting ONJOIN = new OnJoinSetting();
  public static final IRoleCategorySetting INTERFACE = new InterfaceSetting();
  public static final IRoleCategorySetting SELF_ASSIGN = new SelfAssignSetting();
}
