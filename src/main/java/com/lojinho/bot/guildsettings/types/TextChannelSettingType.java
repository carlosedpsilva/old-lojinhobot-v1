package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;
import com.lojinho.bot.util.DisUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class TextChannelSettingType implements IGuildSettingType {
  private final boolean allowNull;

  public TextChannelSettingType(boolean allowNull) {
    this.allowNull = allowNull;
  }

  @Override
  public String typeName() {
    return "text-channel";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return true;
    }
    // Busca por menção
    if (DisUtil.isChannelMention(value)) {
      return guild.getTextChannelById(DisUtil.mentionToId(value)) != null;
    }
    // Busca pelo value
    return DisUtil.findTextChannel(guild, value) != null;
  }

  @Override
  public String fromInput(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return "";
    }
    // Busca pelo value
    TextChannel channel = DisUtil.findTextChannel(guild, value);
    if (channel != null) {
      return channel.getId();
    }
    return "";
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    if (value == null || value.isEmpty() || !value.matches("\\d{10,}")) {
      return "desabilitado";
    }
    // Busca pelo value
    TextChannel channel = guild.getTextChannelById(value);
    if (channel != null) {
      return channel.getName();
    }
    return "desabilitado";
  }

}
