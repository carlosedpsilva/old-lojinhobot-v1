package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;
import com.lojinho.bot.util.Emojibet;
import com.lojinho.bot.util.Misc;

import net.dv8tion.jda.api.entities.Guild;

public class BooleanSettingType implements IGuildSettingType {

  @Override
  public String typeName() {
    return "toggle";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    return value != null && (Misc.isFuzzyTrue(value) || Misc.isFuzzyFalse(value));
  }

  @Override
  public String fromInput(Guild guild, String value) {
    return Misc.isFuzzyTrue(value) ? "true" : "false";
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    return "true".equals(value) ? Emojibet.OKE_SIGN : "desabilitado";
  }

}
