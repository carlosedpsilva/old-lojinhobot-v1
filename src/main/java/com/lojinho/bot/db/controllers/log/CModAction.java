package com.lojinho.bot.db.controllers.log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.lojinho.bot.core.Logger;
import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.controllers.CUser;
import com.lojinho.bot.db.model.log.OModAction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class CModAction {

  public static final SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");

  public static OModAction findById(int modActionId) {
    OModAction record = new OModAction();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * " + "FROM mod_actions " + "WHERE id = ?", modActionId)) {
      if (rs.next()) {
        record = fillRecord(rs);
      }
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return record;
  }

  public static OModAction findLastFor(int guildId, int moderatorId) {
    OModAction record = new OModAction();
    try (ResultSet rs = DbHandler.INSTANCE.select(
      "SELECT * " +
        "FROM mod_actions " +
        "WHERE guild_id = ? AND moderator_id = ? ORDER BY id DESC LIMIT 1", guildId, moderatorId)) {
          if (rs.next()) {
            record = fillRecord(rs);
          }
          rs.getStatement().close();
        } catch (SQLException e) {
          Logger.fatal(e);
        }
        return record;
  }

  private static OModAction fillRecord(ResultSet resultset) throws SQLException {
    OModAction record = new OModAction();
    record.id = resultset.getInt("id");
    record.guildId = resultset.getInt("guild_id");
    record.userId = resultset.getInt("user_id");
    record.userName = resultset.getString("user_name");
    record.moderatorId = resultset.getInt("moderator");
    record.moderatorName = resultset.getString("moderator_name");
    record.active = resultset.getBoolean("active");
    record.messageId = resultset.getString("message_id");
    record.reason = resultset.getString("reason");
    record.createdAt = resultset.getTimestamp("created_at");
    record.expires = resultset.getTimestamp("expires");
    record.setPunishment(resultset.getInt("punishment"));
    return record;
  }

  public static int insert(Guild guild, User targetUser, User moderator, OModAction.PunishType punishType, Timestamp expires) {
    OModAction s = new OModAction();
    s.guildId = CGuild.getCachedId(guild.getIdLong());
    s.userId = CUser.getCachedId(targetUser.getIdLong());
    s.userName = targetUser.getName() + "\\#" + targetUser.getDiscriminator();
    s.moderatorId = CUser.getCachedId(moderator.getIdLong());
    s.moderatorName = moderator.getName() + "\\#" + moderator.getDiscriminator();
    s.punishment = punishType;
    s.expires = expires;
    s.createdAt = new Timestamp(System.currentTimeMillis());
    s.active = true;
    s.messageId = "1";
    return insert(s);
  }

  public static int insert(OModAction record) {
    try {
      return DbHandler.INSTANCE.insert(
          "INSERT INTO mod_actions(guild_id, user_id, user_name, moderator_id, moderator_name, message_id, created_at, reason, punishment, expires, active) "
              + "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
          record.guildId, record.userId, record.userName, record.moderatorId, record.moderatorName, record.messageId,
          record.createdAt, record.reason, record.punishment, record.expires, record.active);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static void update(OModAction record) {
    try {
      DbHandler.INSTANCE.insert(
          "UPDATE mod_actions SET guild_id = ?, user_id = ?, "
              + "moderator_id = ?, message_id = ?, created_at = ?, reason = ?, punishment = ?, "
              + "expires = ?, active = ? " + "WHERE id = ?",
          record.guildId, record.userId,
          record.moderatorId, record.messageId, record.createdAt, record.reason,
          record.punishment.getId(), record.expires, record.active, record.id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static MessageEmbed buildLogMessage(Guild guild, int modActionId) {
    return buildLogMessage(guild, findById(modActionId));
  }

  public static MessageEmbed buildLogMessage(Guild guild, OModAction modAction) {
    EmbedBuilder b = new EmbedBuilder();
    // stuff
    return b.build();
  }
}
