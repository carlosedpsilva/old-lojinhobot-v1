package com.lojinho.bot.roles.settings;

import java.util.ArrayList;
import java.util.Map;

import com.lojinho.bot.roles.IRoleCategorySetting;

import net.dv8tion.jda.api.entities.Guild;

public class NoSetting implements IRoleCategorySetting {

  @Override
  public String typeName() {
    return "default";
  }

  @Override
  public boolean containsConfiguration(Guild guild, Map<String, ArrayList<String>> input) {
    return true;
  }

  @Override
  public boolean validateConfigution(Guild guild, Map<String, ArrayList<String>> input) {
    return true;
  }
}
