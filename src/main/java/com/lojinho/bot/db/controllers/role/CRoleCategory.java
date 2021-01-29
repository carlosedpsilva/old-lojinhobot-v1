package com.lojinho.bot.db.controllers.role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lojinho.bot.core.Logger;
import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.model.role.OGuildRole;
import com.lojinho.bot.db.model.role.ORoleCategory;
import com.lojinho.bot.db.model.role.ORoleCategorySettings;

import net.dv8tion.jda.api.entities.Guild;

public class CRoleCategory {

  /**
   * Obter uma categoria de cargos de um servidor
   * 
   * @param guild
   * @param key
   * @return
   */
  public static ORoleCategory findBy(Guild guild, String key) {
    return findBy(guild.getIdLong(), key);
  }

  public static ORoleCategory findBy(long guild, String key) {
    return findBy(CGuild.getCachedId(guild), key);
  }

  public static ORoleCategory findBy(int guild, String key) {
    ORoleCategory s = new ORoleCategory();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT * FROM guild_role_categories " + "WHERE guild_id = ? AND category_key = ?", guild, key)) {
      if (rs.next()) {
        s = fillRecord(rs);
      }
      rs.getStatement().close();
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return s;
  }

  /**
   * Obter as cateogrias de cargos deste servidor
   * 
   * @param guild
   * @return
   */
  public static List<ORoleCategory> getKeysForGuild(Guild guild) {
    return getKeysForGuild(guild.getIdLong());
  }

  public static List<ORoleCategory> getKeysForGuild(long guildId) {
    return getKeysForGuild(CGuild.getCachedId(guildId));
  }

  public static List<ORoleCategory> getKeysForGuild(int guildId) {
    List<ORoleCategory> keys = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * FROM guild_role_categories " + "WHERE guild_id = ?",
        guildId)) {
      while (rs.next()) {
        keys.add(fillRecord(rs));
      }
      rs.getStatement().close();
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return keys;
  }

  /**
   * Obter os cargos de um categoria de cargos deste servidor
   * 
   * @param guild
   * @param keyId
   * @return
   */
  public static List<OGuildRole> getRolesForKey(Guild guild, int keyId) {
    return getRolesForKey(guild.getIdLong(), keyId);
  }

  public static List<OGuildRole> getRolesForKey(long guildId, int keyId) {
    return getRolesForKey(CGuild.getCachedId(guildId), keyId);
  }

  public static List<OGuildRole> getRolesForKey(int guildId, int keyId) {
    List<OGuildRole> roles = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT * FROM guild_roles " + "WHERE guild_id = ? AND role_category = ?", keyId)) {
      while (rs.next()) {
        roles.add(fillRoleRecord(rs));
      }
      rs.getStatement().close();
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return roles;
  }

  public static List<ORoleCategorySettings> getSettingsForKey(int keyId) {
    List<ORoleCategorySettings> settings = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT * FROM guild_role_category_settings " + "WHERE role_category = ?", keyId)) {
      while (rs.next()) {
        settings.add(fillSettingRecord(rs));
      }
      rs.getStatement().close();
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return settings;
  }

  private static ORoleCategory fillRecord(ResultSet rs) throws SQLException {
    ORoleCategory record = new ORoleCategory();
    record.id = rs.getInt("id");
    record.guild_id = rs.getInt("guild_id");
    record.category_type = rs.getInt("category_type");
    record.category_key = rs.getString("category_key");
    return record;
  }

  private static OGuildRole fillRoleRecord(ResultSet rs) throws SQLException {
    OGuildRole record = new OGuildRole();
    record.id = rs.getInt("id");
    record.discord_id = rs.getString("discord_id");
    record.guild_id = rs.getInt("guild_id");
    record.role_category = rs.getInt("role_category");
    return record;
  }

  private static ORoleCategorySettings fillSettingRecord(ResultSet rs) throws SQLException {
    ORoleCategorySettings record = new ORoleCategorySettings();
    record.role_category = rs.getInt("role_category");
    record.config_name = rs.getString("config_name");
    record.config_value = rs.getString("config_value");
    return record;
  }

}
