package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;

import net.dv8tion.jda.api.entities.Guild;

public class NoSettingType implements IGuildSettingType {

  @Override
  public String typeName() {
    return "n/a";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    return true;
  }

  @Override
  public String fromInput(Guild guild, String value) {
    return value;
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    return value;
  }

}
