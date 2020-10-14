package com.lojinho.bot.guildsettings;

import java.util.Collections;
import java.util.HashSet;

import com.lojinho.bot.guildsettings.types.StringLengthSettingType;

import net.dv8tion.jda.api.entities.Guild;

public enum GSetting {
  COMMAND_PREFIX("loj.", new StringLengthSettingType(1, 4),
      "Prefixo para utilizar os comandos (entre 1 e 4 caracteres)", GSettingTag.COMMAND, GSettingTag.MODERATION),;

  private final String defaultValue;
  private final IGuildSettingType settingType;
  private final String description;
  private final HashSet<GSettingTag> tags;

  GSetting(String defaultValue, IGuildSettingType settingType, String description, GSettingTag... tags) {
    this.defaultValue = defaultValue;
    this.settingType = settingType;
    this.description = description;
    this.tags = new HashSet<>();
    Collections.addAll(this.tags, tags);
  }

  public boolean isInternal() {
    return tags.contains(GSettingTag.INTERNAL);
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getDescription() {
    return description;
  }

  public boolean hasTag(String tag) {
    return tags.contains(GSettingTag.valueOf(tag));
  }

  public boolean hasTag(GSettingTag tag) {
    return tags.contains(tag);
  }

  public HashSet<GSettingTag> getTags() {
    return tags;
  }

  /**
   * Verifica se a entrada é válida
   *
   * @param guild - servidor para o caso de ser necessário na verificação
   * @param input - entrada a ser verificada
   * @return se é uma entrada válida
   */
  public boolean isValidValue(Guild guild, String input) {
    return settingType.validate(guild, input);
  }

  public String getValue(Guild guild, String input) {
    return settingType.fromInput(guild, input);
  }

  public String toDisplay(Guild guild, String value) {
    return settingType.toDisplay(guild, value);
  }

  public IGuildSettingType getSettingType() {
    return settingType;
  }
}