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

  public static TreeSet<String> getAllTags() {
    return new TreeSet<>(tags);
  }

  public static String getDefault(String key) {
    return GSetting.valueOf(key).getDefaultValue();
  }

  public static int countSettings() {
    return countSettings(true);
  }

  public static int countSettings(boolean includeReadOnly) {
    if (includeReadOnly) {
      return GSetting.values().length;
    }
    return (int) Arrays.stream(GSetting.values()).filter((gSetting) -> !gSetting.isInternal()).count();
  }

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

  public static List<String> getAllKeys() {
    ArrayList<String> set = new ArrayList<>();
    for (GSetting setting : GSetting.values()) {
      set.add(setting.name());
    }
    return set;
  }

  public static GSetting get(String key) {
    return GSetting.valueOf(key.toUpperCase());
  }

  public static String getDefault(GSetting setting) {
    return setting.getDefaultValue();
  }

  public static boolean isValidKey(String key) {
    try {
      GSetting.valueOf(key.toUpperCase());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

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
