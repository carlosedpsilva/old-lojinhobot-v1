package com.lojinho.bot.guildsettings.types;

import java.util.Collections;
import java.util.HashSet;

import com.lojinho.bot.guildsettings.IGuildSettingType;

import net.dv8tion.jda.api.entities.Guild;

public class EnumSettingType implements IGuildSettingType {
  private final HashSet<String> options;

  public EnumSettingType(String... values) {
    options = new HashSet<>();
    Collections.addAll(options, values);
  }

  @Override
  public String typeName() {
    return "enum";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    return value != null && options.contains(value.toLowerCase());
  }

  @Override
  public String fromInput(Guild guild, String value) {
    return value;
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    return value;
  }

  public String[] getValidOptions() {
    return options.toArray(new String[] {});
  }

}
