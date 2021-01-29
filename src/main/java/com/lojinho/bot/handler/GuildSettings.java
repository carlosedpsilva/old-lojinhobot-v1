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
    this.id = record.id;
    settingInstance.put(guild, this);
    loadSettings();
  }

  /**
   * Obtém o valor de uma configuração para a guild especificada salvo no cache
   *
   * @param channel o canal a se obter a guild a se obter o valor da configuração
   * @param setting a GSetting a se obter o valor da configuração
   * @return o valor da configuração
   */
  public static String getFor(MessageChannel channel, GSetting setting) {
    if (channel != null && channel instanceof TextChannel) {
      return getFor(((TextChannel) channel).getGuild().getIdLong(), setting);
    }
    return DefaultGuildSettings.getDefault(setting);
  }

  /**
   * Obtém o valor de uma configuração para a guild especificada salvo no cache
   *
   * @param guild   a guild a se obter o valor da configuração
   * @param setting a GSetting a se obter o valor da configuração
   * @return o valor da configuração
   */
  public static String getFor(Guild guild, GSetting setting) {
    return getFor(guild.getIdLong(), setting);
  }

  /**
   * Obtém o valor de uma configuração para a guild especificada salvo no cache
   *
   * @param guild   a guild a se obter o valor da configuração
   * @param setting a GSetting a se obter o valor da configuração
   * @return o valor da configuração
   */
  public static String getFor(long guild, GSetting setting) {
    return GuildSettings.get(guild).getOrDefault(setting);
  }

  /**
   * Similar ao {@link #getFor(MessageChannel, GSetting)} mas especificamente
   * apenas para configurações booleanas. Obtém o valor da configuração desta
   * guild salva no cache.
   *
   * @param channel o canal a se obter a guild a se obter o valor da configuração
   *                booleana
   * @param setting a GSetting a se obter o valor da configuração booleana
   * @return o valor da configuração booleana
   */
  public static boolean getBoolFor(MessageChannel channel, GSetting setting) {
    return setting.getSettingType() instanceof BooleanSettingType && "true".equals(getFor(channel, setting));
  }

  /**
   * Similar ao {@link #getFor(Guild, GSetting)} mas especificamente apenas para
   * configurações booleanas. Obtém o valor da configuração desta guild salva no
   * cache.
   *
   * @param guild   a guild a se obter o valor da configuração booleana
   * @param setting a GSetting a se obter o valor da configuração booleana
   * @return o valor da configuração booleana
   */
  public static boolean getBoolFor(Guild guild, GSetting setting) {
    return setting.getSettingType() instanceof BooleanSettingType && "true".equals(getFor(guild, setting));
  }

  /**
   * Similar ao {@link #getFor(long, GSetting)} mas especificamente apenas para
   * configurações booleanas. Obtém o valor da configuração desta guild salva no
   * cache.
   *
   * @param guild   a guild a se obter o valor da configuração booleana
   * @param setting a GSetting a se obter o valor da configuração booleana
   * @return o valor da configuração booleana
   */
  public static boolean getBoolFor(long guild, GSetting setting) {
    return setting.getSettingType() instanceof BooleanSettingType && "true".equals(getFor(guild, setting));
  }

  /**
   * Obtém o objeto GuildSettings desta guild. O objeto é criado caso inexistente.
   * 
   * @param channel o canal para se obter a guild a se obter o objeto
   *                GuildSettings
   * @return o objeto GuildSettings salvo em cache ou um novo GuildSettings
   */
  public static GuildSettings get(MessageChannel channel) {
    if (channel != null && channel instanceof TextChannel) {
      return GuildSettings.get(((TextChannel) channel).getGuild());
    }
    return null;
  }

  /**
   * Obtém o objeto GuildSettings desta guild. O objeto é criado caso inexistente.
   * 
   * @param guild a guild a se obter o objeto GuildSettings
   * @return o objeto GuildSettings salvo em cache ou um novo GuildSettings
   */
  public static GuildSettings get(Guild guild) {
    return get(guild.getIdLong());
  }

  /**
   * Obtém o objeto GuildSettings desta guild salvo no cache. O objeto é criado
   * caso inexistente.
   * 
   * @param guild a guild a se obter o objeto GuildSettings
   * @return o objeto GuildSettings salvo em cache ou um novo GuildSettings
   */
  public static GuildSettings get(long guild) {
    if (settingInstance.containsKey(guild)) {
      return settingInstance.get(guild);
    } else {
      return new GuildSettings(guild);
    }
  }

  /**
   * Remove o objeto GuildSettings da guild especificada do cache
   * 
   * @param channel o canal a se obter a guild a ser removida do cache
   */
  public static void remove(MessageChannel channel) {
    if (channel != null && channel instanceof TextChannel) {
      remove(((TextChannel) channel).getGuild());
    }
  }

  /**
   * Remove o objeto GuildSettings da guild especificada do cache
   * 
   * @param guild a guild a ser removida do cache
   */
  public static void remove(Guild guild) {
    remove(guild.getIdLong());
  }

  /**
   * Remove o objeto GuildSettings da guild especificada do cache
   * 
   * @param guild a guild a ser removida do cache
   */
  public static void remove(long guild) {
    if (settingInstance.containsKey(guild)) {
      settingInstance.remove(guild);
    }
  }

  /**
   * Obtém o valor da configuração desta guild salva no cache
   * 
   * @param setting a GSetting a se obter o valor da configuração
   * @return o valor da configuração especificada
   */
  public String getOrDefault(String key) {
    return getOrDefault(GSetting.valueOf(key.toUpperCase()));
  }

  /**
   * Obtém o valor da configuração desta guild salva no cache
   * 
   * @param setting a GSetting a se obter o valor da configuração
   * @return o valor da configuração especificada
   */
  public String getOrDefault(GSetting setting) {
    return settings[setting.ordinal()] == null ? setting.getDefaultValue() : settings[setting.ordinal()];
  }

  /**
   * Inicializa as configurações desta GuildSettings salvando padrões e obtidos no
   * banco de dados no cache static settingInstance
   */
  private void loadSettings() {
    if (initialized || id <= 0) {
      return;
    }
    for (GSetting setting : GSetting.values()) {
      settings[setting.ordinal()] = DefaultGuildSettings.getDefault(setting);
    }
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT config_name, config_value " + "FROM guild_settings " + "WHERE guild_id = ? ", id)) {
      while (rs.next()) {
        String key = rs.getString("config_name").toUpperCase();
        String value = rs.getString("config_value");
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

  /**
   * Retorna os valores das configurações desta guild
   * 
   * @return as configurações para a guild obtida
   */
  public String[] getSettings() {
    return settings;
  }

  /**
   * Retorna o valor padão de uma dada GSetting
   * 
   * @param key a GSetting a se obter o valor padrão
   * @return o valor padrão para a GSetting especificado
   */
  public String getDefaultValue(String key) {
    if (DefaultGuildSettings.isValidKey(key)) {
      return DefaultGuildSettings.get(key).getDefaultValue();
    }
    return "";
  }

  /**
   * Obtém a descrição da GSetting
   * 
   * @param key a GSetting a se obter o tipo de GSetting
   * @return a descrição da GSetting especificada
   */
  public String getDescription(String key) {
    if (DefaultGuildSettings.isValidKey(key)) {
      return DefaultGuildSettings.get(key).getDescription();
    }
    return "";
  }

  /**
   * Obtém o tipo da GSetting
   * 
   * @param key a GSetting a se obter seu tipo
   * @return o tipo da GSetting especificada
   */
  public IGuildSettingType getSettingsType(String key) {
    return DefaultGuildSettings.get(key).getSettingType();
  }

  /**
   * Obtém o valor padrão de uma dada GSetting de uma guild
   * 
   * @param guild utilitário para validar o valor em toDisplay(guild, value)
   * @param key   a GSetting a se obter o valor padrão
   * @return o valor padrão da GSetting especificada
   */
  public String getDisplayValue(Guild guild, String key) {
    return DefaultGuildSettings.get(key.toUpperCase()).toDisplay(guild, getOrDefault(key));
  }

  /**
   * Define um novo valor para uma GSetting de uma guild. O valor é gravado no
   * banco de dados se for diferente do valor padrão, e removido se for igual.
   * 
   * @param guild   a guild a ser alterada as configurações
   * @param setting a configuração a ser alterada
   * @param value   o valor da configuração a ser definido
   * @return se a configuração foi salva com sucesso
   */
  public boolean set(Guild guild, String setting, String value) {
    return DefaultGuildSettings.isValidKey(setting) && set(guild, GSetting.valueOf(setting.toUpperCase()), value);
  }

  /**
   * Define um novo valor para uma GSetting de uma guild. O valor é gravado no
   * banco de dados se for diferente do valor padrão, e removido se for igual.
   * 
   * @param guild   a guild a ser alterada as configurações
   * @param setting a configuração a ser alterada
   * @param value   o valor da configuração a ser definido
   * @return se a configuração foi salva com sucesso
   */
  public boolean set(Guild guild, GSetting setting, String value) {
    if (setting.isValidValue(guild, value)) {
      if (!DefaultGuildSettings.getDefault(setting).equals(value)) {
        try {
          String dbValue = setting.getValue(guild, value);
          DbHandler.INSTANCE.insert("INSERT INTO guild_settings (guild_id, config_name, config_value) VALUES (?, ?, ?) "
              + "ON DUPLICATE KEY UPDATE config_value = ?", id, setting.name().toLowerCase(), dbValue, dbValue);
          settings[setting.ordinal()] = dbValue;
          return true;
        } catch (SQLException e) {
          e.printStackTrace();
        }
      } else {
        try {
          DbHandler.INSTANCE.query("DELETE FROM guild_settings WHERE guild_id = ? AND config_name = ?", id,
              setting.name().toLowerCase());
          settings[setting.ordinal()] = DefaultGuildSettings.getDefault(setting);
          return true;
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  /**
   * Deleta do banco de dados as configurções desta guild
   */
  public synchronized void reset() {
    try {
      DbHandler.INSTANCE.query("DELETE FROM guild_settings WHERE guild_id = ? ", id);
      initialized = false;
      loadSettings();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
