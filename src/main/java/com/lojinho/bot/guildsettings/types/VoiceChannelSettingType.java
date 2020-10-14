package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;
import com.lojinho.bot.util.Emojibet;
import com.lojinho.bot.util.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VoiceChannelSettingType implements IGuildSettingType {
  private final boolean allowNull;

  public VoiceChannelSettingType(boolean allowNull) {
    this.allowNull = allowNull;
  }

  @Override
  public String typeName() {
    return "voice-channel";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return true;
    }
    // Busca por menção
    if (Utils.isChannelMention(value)) {
      return guild.getVoiceChannelById(Utils.mentionToId(value)) != null;
    }
    // Busca pelo value
    return Utils.findVoiceChannel(guild, value) != null;
  }

  @Override
  public String fromInput(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return "";
    }
    // Busca por menção
    if (Utils.isChannelMention(value)) {
      VoiceChannel channel = guild.getVoiceChannelById(Utils.mentionToId(value));
      if (channel != null) {
        return channel.getId();
      }
    }
    // Busca pelo nome
    VoiceChannel channel = Utils.findVoiceChannel(guild, value);
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
    VoiceChannel voiceChannel = guild.getVoiceChannelById(value);
    if (voiceChannel != null) {
      return voiceChannel.getName();
    }
    return Emojibet.X;
  }

}
