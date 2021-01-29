package com.lojinho.bot.db.controllers.role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lojinho.bot.core.Logger;
import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.model.role.ORoleReaction;
import com.lojinho.bot.db.model.role.ORoleReactionKey;

import net.dv8tion.jda.api.entities.Guild;

public class CRoleReaction {

  /**
   * Encontrar ou criar uma mensagem de reação de atribuição
   * 
   * @param guild
   * @param key
   * @return uma mensagem de reação de atribuição do registro ou nova
   */
  public static ORoleReactionKey findOrCreate(Guild guild, String key) {
    return findOrCreate(guild.getIdLong(), key);
  }

  public static ORoleReactionKey findOrCreate(long guild, String key) {
    ORoleReactionKey rec = findBy(guild, key);
    if (rec.id == 0) {
      rec.guild_id = CGuild.getCachedId(guild);
      rec.message_key = key;
      insert(rec);
    }
    return rec;
  }

  /**
   * Obter uma mensagem de reação de atribuição
   * 
   * @param guild
   * @param key
   * @return o registro da mensagem de reação de atribuição
   */
  public static ORoleReactionKey findBy(long guild, String key) {
    return findBy(CGuild.getCachedId(guild), key);
  }

  public static ORoleReactionKey findBy(int guild, String key) {
    ORoleReactionKey s = new ORoleReactionKey();
    try (ResultSet rs = DbHandler.INSTANCE
        .select("SELECT * " + "FROM reaction_role_key " + "WHERE guild_id = ? AND message_key = ?", guild, key)) {
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
   * Obter as mensagens de reação de atribuição deste servidor
   * 
   * @param guild
   * @return
   */
  public static List<ORoleReactionKey> getKeysForGuild(Guild guild) {
    return getKeysForGuild(guild.getIdLong());
  }

  public static List<ORoleReactionKey> getKeysForGuild(long guildId) {
    return getKeysForGuild(CGuild.getCachedId(guildId));
  }

  public static List<ORoleReactionKey> getKeysForGuild(int guildId) {
    List<ORoleReactionKey> keys = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * " + "FROM role_reaction_keys " + "WHERE guild_id = ?",
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
   * Obter as reações de uma mensagem de reação de atribuição
   * 
   * @param keyId
   * @return
   */
  public static List<ORoleReaction> getReactionsForKey(int keyId) {
    List<ORoleReaction> reactions = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * " + "FROM role_reaction_key_reactions " + "WHERE role_reaction_key = ?",
        keyId)) {
      while (rs.next()) {
        reactions.add(fillReactionRecord(rs));
      }
      rs.getStatement().close();
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return reactions;
  }

  public static void addReactionRole(int roleReactionKeyId, String emote, boolean isNormalEmote, long roleId) {
    try {
      DbHandler.INSTANCE.insert(
          "INSERT INTO role_reaction_key_reactions (role_reaction_key, role_id, emote_id, is_normal_emote) " + "VALUES (?, ?, ?, ?)",
          roleReactionKeyId, roleId, emote, isNormalEmote);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void removeReactionRole(int roleReactionRoleKey, String emote, long roleId) {
    try {
      DbHandler.INSTANCE.query(
          "DELETE FROM role_reaction_key_reactions WHERE role_reaction_key = ? AND emote_id = ? AND role_id = ?",
          roleReactionRoleKey, emote, roleId);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void removeReaction(int roleReactionRoleKey, String emote) {
    try {
      DbHandler.INSTANCE.query("DELETE FROM role_reaction_key_reactions WHERE role_reaction_key = ? AND emote_id = ?",
          roleReactionRoleKey, emote);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static ORoleReactionKey fillRecord(ResultSet rs) throws SQLException {
    ORoleReactionKey record = new ORoleReactionKey();
    record.id = rs.getInt("id");
    record.guild_id = rs.getInt("guild_id");
    record.channel_id = rs.getLong("channel_id");
    record.message_id = rs.getLong("message_id");
    record.message_key = rs.getString("message_key");
    return record;
  }

  private static ORoleReaction fillReactionRecord(ResultSet rs) throws SQLException {
    ORoleReaction record = new ORoleReaction();
    record.id = rs.getInt("id");
    record.reaction_role_key = rs.getInt("reaction_role_key");
    record.emote_id = rs.getString("emote_id");
    record.isNormalEmote = rs.getBoolean("is_normal_emote");
    record.role_id = rs.getLong("role_id");
    return record;
  }

  public static void update(ORoleReactionKey record) {
    try {
      DbHandler.INSTANCE.query(
          "UPDATE role_reaction_keys SET message_key = ?, channel_id = ?, message_id = ? WHERE id = ?",
          record.message_key, record.channel_id, record.message_id, record.id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insert(ORoleReactionKey record) {
    try {
      record.id = DbHandler.INSTANCE.insert(
          "INSERT INTO role_reaction_keys (guild_id, channel_id, message_id, message_key) " + "VALUES (?, ?, ?, ?)",
          record.guild_id, record.channel_id, record.message_id, record.message_key);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
