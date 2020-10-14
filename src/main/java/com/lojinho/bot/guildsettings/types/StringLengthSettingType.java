package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;

import net.dv8tion.jda.api.entities.Guild;

public class StringLengthSettingType implements IGuildSettingType {
  private final int min, max;

  public StringLengthSettingType(int min, int max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public String typeName() {
    return "string-lenght";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    return value != null && value.length() >= min && value.length() <= max;
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
