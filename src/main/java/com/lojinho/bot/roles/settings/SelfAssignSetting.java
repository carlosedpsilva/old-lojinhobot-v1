package com.lojinho.bot.roles.settings;

import java.util.ArrayList;
import java.util.Map;

import com.lojinho.bot.roles.IRoleCategorySetting;

import net.dv8tion.jda.api.entities.Guild;

public class SelfAssignSetting implements IRoleCategorySetting {
  private final String[] typeConfigs = { "usagetype", "assigntype" };

  @Override
  public String typeName() {
    return "self-assign";
  }

  @Override
  public boolean containsConfiguration(Guild guild, Map<String, ArrayList<String>> input) {
    for (String config : typeConfigs) {
      if (!input.containsKey(config)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean validateConfigution(Guild guild, Map<String, ArrayList<String>> input) {
    return true;
  }
}
