package com.lojinho.bot.roles;

import java.util.Collections;
import java.util.HashSet;

public enum RCSettingTag {
  USAGE_TYPE(0, "Comando ou Reação", "COMMAND", "REACTION", "BOTH"),
  ASSIGN_TYPE(1, "Atribuição Múltipla ou Alternável", "STACK", "SWITCH");

  private int id;
  private String description;
  private HashSet<String> settings;

  RCSettingTag(int id, String description, String... options) {
    this.id = id;
    this.description = description;
    this.settings = new HashSet<>();
    Collections.addAll(this.settings, options);
  }
}
