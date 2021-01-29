package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;

import net.dv8tion.jda.api.entities.Guild;

public class NumberBetweenSettingType implements IGuildSettingType {
  private final int min, max;

  public NumberBetweenSettingType(int min, int max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public String typeName() {
    return "number-between";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    try {
      int vol = Integer.parseInt(value);
      if (vol >= min && vol <= max) {
        return true;
      }
    } catch (Exception ignored) {
    }
    return false;
  }

  @Override
  public String fromInput(Guild guild, String value) {
    try {
      int vol = Integer.parseInt(value);
      if (vol >= min && vol <= max) {
        return "" + vol;
      }
    } catch (Exception ignored) {
    }
    return "";
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    return value;
  }

}
