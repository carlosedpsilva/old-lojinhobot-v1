package com.lojinho.bot.roles;

import java.util.ArrayList;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;

public interface IRoleCategorySetting {
  String typeName();

  boolean containsConfiguration(Guild guild, Map<String, ArrayList<String>> input);

  boolean validateConfigution(Guild guild, Map<String, ArrayList<String>> input);
}
