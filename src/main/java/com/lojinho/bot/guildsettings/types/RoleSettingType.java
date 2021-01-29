package com.lojinho.bot.guildsettings.types;

import com.lojinho.bot.guildsettings.IGuildSettingType;
import com.lojinho.bot.util.DisUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class RoleSettingType implements IGuildSettingType {
  private final boolean allowNull;

  public RoleSettingType(boolean allowNull) {
    this.allowNull = allowNull;
  }

  @Override
  public String typeName() {
    return "discord-role";
  }

  @Override
  public boolean validate(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return true;
    }
    // Busca por menção
    if (DisUtil.isRoleMention(value)) {
      return guild.getRoleById(DisUtil.mentionToId(value)) != null;
    }
    // Busca pelo value
    return DisUtil.findRole(guild, value) != null;
  }

  @Override
  public String fromInput(Guild guild, String value) {
    if (allowNull && (value == null || value.isEmpty() || value.equalsIgnoreCase("false"))) {
      return "";
    }
    // Busca por menção
    if (DisUtil.isRoleMention(value)) {
      Role role = guild.getRoleById(DisUtil.mentionToId(value));
      if (role != null) {
        return role.getId();
      }
    }
    // Busca pelo value
    Role role = DisUtil.findRole(guild, value);
    if (role != null) {
      return role.getId();
    }
    return "";
  }

  @Override
  public String toDisplay(Guild guild, String value) {
    if (value == null || value.isEmpty() || !value.matches("\\d{10,}")) {
      return "desabilitado";
    }
    // Busca pelo value
    Role role = guild.getRoleById(value);
    if (role != null) {
      return role.getName();
    }
    return "desabilitado";
  }
}
