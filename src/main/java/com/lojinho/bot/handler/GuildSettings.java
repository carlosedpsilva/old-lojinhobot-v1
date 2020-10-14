package com.lojinho.bot.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.model.OGuild;
import com.lojinho.bot.guildsettings.DefaultGuildSettings;
import com.lojinho.bot.guildsettings.GSetting;
import com.lojinho.bot.guildsettings.IGuildSettingType;
import com.lojinho.bot.guildsettings.types.BooleanSettingType;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildSettings {
  private final static Map<Long, GuildSettings> settingInstance = new ConcurrentHashMap<>();
  private boolean initialized = false;

  private final int id;
  private final long guildId;
  private final String[] settings;

  private GuildSettings(Long guild) {
    this.settings = new String[GSetting.values().length];
    OGuild record = CGuild.findBy(guild);
    if (record.id == 0) {
      record.name = String.valueOf(guild);
      record.discord_id = String.valueOf(guild);
      record.owner = 1;
      CGuild.insert(record);
    }
    this.guildId = guild;
    this.id = record.id;
    settingInstance.put(guild, this);
    loadSettings();
  }

  /**
   * Simplified method to get the setting for a channel instead of guild
   *
   * @param channel the channel to check
   * @param setting the Setting
   * @return the setting
   */
  public static String getFor(MessageChannel channel, GSetting setting) {
    if (channel != null && channel instanceof TextChannel) {
      return GuildSettings.get(((TextChannel) channel).getGuild()).getOrDefault(setting);
    }
    return DefaultGuildSettings.getDefault(setting);
  }

  /**
   * Similar to {@link #getFor(MessageChannel, GSetting)} but more specifically
   * only for boolean type settings
   *
   * @param channel the channel to check
   * @param setting the setting
   * @return boolean value of the setting
   */
  public static boolean getBoolFor(MessageChannel channel, GSetting setting) {
    return setting.getSettingType() instanceof BooleanSettingType && "true".equals(getFor(channel, setting));
  }

  public static void remove(long guildId) {
    if (settingInstance.containsKey(guildId)) {
      settingInstance.remove(guildId);
    }
  }

  /**
   * @param channel o canal pelo qual buscar as configurações
   * @return Objeto GuildSettings se for um canal, caso contrário null
   */
  public static GuildSettings get(MessageChannel channel) {
    if (channel != null && channel instanceof TextChannel) {
      return GuildSettings.get(((TextChannel) channel).getGuild());
    }
    return null;
  }

  public static GuildSettings get(Guild guild) {
    return get(guild.getIdLong());
  }

  public static GuildSettings get(long guild) {
    if (settingInstance.containsKey(guild)) {
      return settingInstance.get(guild);
    } else {
      return new GuildSettings(guild);
    }
  }

  /**
   * @param setting the setting
   * @return the setting or default value
   */
  public String getOrDefault(GSetting setting) {
    return settings[setting.ordinal()] == null ? setting.getDefaultValue() : settings[setting.ordinal()];
  }

  public String getOrDefault(String key) {
    return getOrDefault(GSetting.valueOf(key.toUpperCase()));
  }

  private void loadSettings() {
    if (initialized || id <= 0) {
      return;
    }
    for (GSetting setting : GSetting.values()) {
      settings[setting.ordinal()] = null;
    }
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT name, config " + "FROM guild_settings " + "WHERE guild_id = ? ",
        id)) {
      while (rs.next()) {
        String key = rs.getString("name").toUpperCase();
        String value = rs.getString("config");
        if (DefaultGuildSettings.isValidKey(key)) {
          settings[GSetting.valueOf(key).ordinal()] = value;
        }
      }
      rs.getStatement().close();
      initialized = true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String getDescription(String key) {
    if (DefaultGuildSettings.isValidKey(key)) {
      return DefaultGuildSettings.get(key).getDescription();
    }
    return "";
  }

  public IGuildSettingType getSettingsType(String key) {
    return DefaultGuildSettings.get(key).getSettingType();
  }

  public String getDisplayValue(Guild guild, String key) {
    return DefaultGuildSettings.get(key.toUpperCase()).toDisplay(guild, getOrDefault(key));
  }

  public boolean set(Guild guild, String setting, String value) {
    return DefaultGuildSettings.isValidKey(setting) && set(guild, GSetting.valueOf(setting.toUpperCase()), value);
  }

  public boolean set(Guild guild, GSetting setting, String value) {
    if (setting.isValidValue(guild, value)) {
      try {
        String dbValue = setting.getValue(guild, value);
        DbHandler.INSTANCE.insert("INSERT INTO guild_settings (guild_id, name, config) VALUES (?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE config = ?", id, setting.name().toLowerCase(), dbValue, dbValue);
        settings[setting.ordinal()] = dbValue;
        return true;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public String[] getSettings() {
    return settings;
  }

  public String getDefaultValue(String key) {
    if (DefaultGuildSettings.isValidKey(key)) {
      return DefaultGuildSettings.get(key).getDefaultValue();
    }
    return "";
  }

  public synchronized void reset() {
    try {
      DbHandler.INSTANCE.query("DELETE FROM guild_settings WHERE guild = ? ", id);
      initialized = false;
      loadSettings();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
