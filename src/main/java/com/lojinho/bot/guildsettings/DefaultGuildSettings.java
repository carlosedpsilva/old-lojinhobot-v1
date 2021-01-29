package com.lojinho.bot.guildsettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class DefaultGuildSettings {
  private static final TreeSet<String> tags = new TreeSet<>();
  private static boolean initialized = false;

  static {
    initSettings();
  }

  /**
   * Obtém todas as tags utilizadas nas GSettings
   * 
   * @return TreeSet de tags utilizadas nas GSettings
   */
  public static TreeSet<String> getAllTags() {
    return new TreeSet<>(tags);
  }

  /**
   * Obtém o valor padrão de uma dada GSetting
   * 
   * @param key a GSetting a se obter o valor padrão
   * @return o valor padrão para a GSetting especificado
   */
  public static String getDefault(String key) {
    return GSetting.valueOf(key).getDefaultValue();
  }

  /**
   * Obtém o length de GSettings incluindo as com a tag INTERNAL
   * 
   * @return GSettings length
   */
  public static int countSettings() {
    return countSettings(true);
  }

  /**
   * Obtém o length de GSettings
   * 
   * @param includeReadOnly incluir GSettings com a tag INTERNAL ou não
   * @return GSettings length
   */
  public static int countSettings(boolean includeReadOnly) {
    if (includeReadOnly) {
      return GSetting.values().length;
    }
    return (int) Arrays.stream(GSetting.values()).filter((gSetting) -> !gSetting.isInternal()).count();
  }

  /**
   * Obtém as GSettings que não são INTERNAL
   * 
   * @return GSettings modificáveis
   */
  public static List<String> getWritableKeys() {
    ArrayList<String> set = new ArrayList<>();
    for (GSetting setting : GSetting.values()) {
      if (setting.isInternal()) {
        continue;
      }
      set.add(setting.name());
    }
    return set;
  }

  public static List<String> getAllKeys(String tag, boolean includeReadOnly) {
    if (tag != null) {
      ArrayList<String> set = new ArrayList<>();
      for (GSetting setting : GSetting.values()) {
        if (setting.hasTag(tag) && (!setting.hasTag(GSettingTag.INTERNAL) || includeReadOnly))
          set.add(setting.name());
      }
      return set;
    }
    return getAllKeys(includeReadOnly);
  }

  public static List<String> getAllKeys(boolean includeReadOnly) {
    ArrayList<String> set = new ArrayList<>();
    for (GSetting setting : GSetting.values()) {
      if (!setting.hasTag(GSettingTag.INTERNAL) || includeReadOnly)
        set.add(setting.name());
    }
    return set;
  }

  /**
   * Obtém todas as GSettings
   * 
   * @return todas as GSettings
   */
  public static List<String> getAllKeys() {
    ArrayList<String> set = new ArrayList<>();
    for (GSetting setting : GSetting.values()) {
      set.add(setting.name());
    }
    return set;
  }

  /**
   * Obtém uma GSetting
   * 
   * @param key o nome da GSetting
   * @return a GSetting especificada
   */
  public static GSetting get(String key) {
    return GSetting.valueOf(key.toUpperCase());
  }

  /**
   * Obtém o valor padrão de uma dada GSetting
   * 
   * @param setting a GSetting a se obter o valor padrão da configuração
   * @return o valor padrão da GSetting especificada
   */
  public static String getDefault(GSetting setting) {
    return setting.getDefaultValue();
  }

  /**
   * Verifica se o parâmetro passado é uma GSetting
   * 
   * @param key o parâmetro a ser validado
   * @return se é uma GSetting
   */
  public static boolean isValidKey(String key) {
    try {
      GSetting.valueOf(key.toUpperCase());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Inicializa as tags utilizadas
   */
  private static void initSettings() {
    if (initialized) {
      return;
    }
    for (GSetting setting : GSetting.values()) {
      for (GSettingTag tag : setting.getTags()) {
        if (!tags.contains(tag.name())) {
          tags.add(tag.name());
        }
      }
      initialized = true;
    }
  }
}
