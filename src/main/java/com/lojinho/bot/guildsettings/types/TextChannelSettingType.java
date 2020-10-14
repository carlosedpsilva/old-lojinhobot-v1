package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;
import com.lojinho.bot.util.Emojibet;
import com.lojinho.bot.util.Utils;

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
    if (Utils.isChannelMention(value)) {
      return guild.getTextChannelById(Utils.mentionToId(value)) != null;
    }
    // Busca pelo value
    return Utils.findChannel(guild, value) != null;
  }

  @Override
  public String fromInput(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return "";
    }
    // Busca por menção
    if (Utils.isChannelMention(value)) {
      TextChannel textChannel = guild.getTextChannelById(Utils.mentionToId(value));
      if (textChannel != null) {
        return textChannel.getId();
      }
    }
    // Busca pelo value
    TextChannel channel = Utils.findChannel(guild, value);
    if (channel != null) {
      return channel.getId();
    }
    return "";
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    if (value == null || value.isEmpty() || !value.matches("\\d{10,}")) {
      return Emojibet.X;
    }
    // Busca pelo value
    TextChannel channel = guild.getTextChannelById(value);
    if (channel != null) {
      return channel.getName();
    }
    return Emojibet.X;
  }

}
