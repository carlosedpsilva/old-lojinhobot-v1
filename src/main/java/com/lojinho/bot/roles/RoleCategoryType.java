package com.lojinho.bot.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import com.lojinho.bot.roles.settings.InterfaceSetting;
import com.lojinho.bot.roles.settings.NoSetting;
import com.lojinho.bot.roles.settings.OnJoinSetting;
import com.lojinho.bot.roles.settings.SelfAssignSetting;

import net.dv8tion.jda.api.entities.Guild;

public enum RoleCategoryType {
  DEFAULT("Categoria Padrão Sem Nome", "Cargos atribuídos manualmente", new NoSetting()),
  ONJOIN("Categoria de Cargos Iniciais Sem Nome", "Cargos atribúidos ao entrar no servidor", new OnJoinSetting()),
  INTERFACE("Categoria de Interface Sem Nome", "Cargos mantidos permanentemente", new InterfaceSetting()),
  SELF_ASSIGN("Categoria de Self Assign Sem Nome", "Cargos auto-atribuíveis por comando e por reação",
      new SelfAssignSetting(), RCSettingTag.USAGE_TYPE, RCSettingTag.ASSIGN_TYPE),;

  private final String defaultName;
  private final String description;
  private final IRoleCategorySetting categorySetting;
  private final HashSet<RCSettingTag> tags;

  RoleCategoryType(String defaultName, String description, IRoleCategorySetting categorySetting, RCSettingTag... tags) {
    this.defaultName = defaultName;
    this.description = description;
    this.categorySetting = categorySetting;
    this.tags = new HashSet<>();
    Collections.addAll(this.tags, tags);
  }

  public static RoleCategoryType getBy(String name) {
    for (RoleCategoryType categoryType : values()) {
      if (categoryType.name().equals(name)) {
        return categoryType;
      }
    }
    return RoleCategoryType.DEFAULT;
  }

  public static RoleCategoryType getBy(int code) {
    for (RoleCategoryType categoryType : values()) {
      if (categoryType.getId() == code) {
        return categoryType;
      }
    }
    return RoleCategoryType.DEFAULT;
  }

  public int getId() {
    return ordinal();
  }

  public String getDefaultName() {
    return defaultName;
  }

  public String getDescription() {
    return description;
  }

  public boolean containsConfiguration(Guild guild, Map<String, ArrayList<String>> input) {
    return categorySetting.containsConfiguration(guild, input);
  }

  public boolean isValidValue(Guild guild, Map<String, ArrayList<String>> input) {
    return categorySetting.validateConfigution(guild, input);
  }

  public IRoleCategorySetting getCategoryType() {
    return categorySetting;
  }

  public boolean hasSetting(String tag) {
    return tags.contains(RCSettingTag.valueOf(tag));
  }

  public boolean hasSetting(RCSettingTag tag) {
    return tags.contains(tag);
  }

  public HashSet<RCSettingTag> getTags() {
    return tags;
  }

}
