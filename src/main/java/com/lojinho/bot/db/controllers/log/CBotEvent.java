package com.lojinho.bot.db.controllers.log;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lojinho.bot.core.Logger;
import com.lojinho.bot.db.DbHandler;
import com.lojinho.bot.db.model.log.OBotEvent;

public class CBotEvent {

  public static OBotEvent findById(String id) {
    OBotEvent s = new OBotEvent();
    try (ResultSet rs = DbHandler.INSTANCE.select(
        "SELECT id, created_on, event_group, sub_group, data, log_level " +
            "FROM bot_events " +
            "WHERE id = ?", id)) {
      if (rs.next()) {
        s = fillRecord(rs);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return s;
  }

  public static List<OBotEvent> getEventsAfter(int id) {
    List<OBotEvent> list = new ArrayList<>();
    try (ResultSet rs = DbHandler.INSTANCE.select("SELECT * FROM bot_events WHERE id > ?", id)) {
      while (rs.next()) {
        list.add(fillRecord(rs));
      }
      rs.getStatement().close();
    } catch (Exception e) {
      Logger.fatal(e);
    }
    return list;
  }

  public static OBotEvent fillRecord(ResultSet rs) throws SQLException {
    OBotEvent s = new OBotEvent();
    s.id = rs.getInt("id");
    s.createdOn = rs.getTimestamp("created_on");
    s.group = rs.getString("event_group");
    s.subGroup = rs.getString("sub_group");
    s.data = rs.getString("data");
    s.logLevel = OBotEvent.Level.fromId(rs.getInt("log_level"));
    return s;
  }

  public static void insert(String group, String subGroup, String data) {
    insert(OBotEvent.Level.INFO, group, subGroup, data);
  }

  public static void insert(OBotEvent.Level logLevel, String group, String subGroup, String data) {
    OBotEvent oBotEvent = new OBotEvent();
    oBotEvent.group = group;
    oBotEvent.subGroup = subGroup;
    oBotEvent.data = data;
    oBotEvent.logLevel = logLevel;
    insert(oBotEvent);
  }

  public static void insert(OBotEvent record) {
    try {
      record.id = DbHandler.INSTANCE.insert(
          "INSERT INTO bot_events(created_on, log_level, event_group, sub_group, data)" +
              "VALUES (?,?,?,?,?)",
          new Date(System.currentTimeMillis()), record.logLevel.getId(), record.group, record.subGroup, record.data);
    } catch (SQLException e) {
      e.printStackTrace();
      // LogToDiscord?
    }
  }
}
