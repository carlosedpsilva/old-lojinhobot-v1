package com.lojinho.bot.db.model.log;

import java.awt.*;
import java.sql.Timestamp;

public class OModAction {
  public int id = 0;
  public int guildId = 0;
  public int userId = 0;
  public int moderatorId = 0;
  public String messageId = "";
  public Timestamp createdAt = null;
  public Timestamp expires = null;
  public PunishType punishment = PunishType.KICK;
  public String reason = "";
  public boolean active = true;
  public String moderatorName = "";
  public String userName = "";

  public void setPunishment(int punishment) {
    this.punishment = PunishType.fromId(punishment);
}

  public enum PunishType {
    WARN(1, "Warn", "avisado(a)", "Adiciona um strike de warn ao usuário", new Color(0xA8CF00)),
    MUTE(2, "Mute", "mutado(a)", "Adiciona o cargo de mute configurado ao usuário", new Color(0xffff00)),
    KICK(3, "Kick", "expulso(a)", "Remove o usuário do servidor", new Color(0xffa700)),
    SOFT_BAN(4, "Soft-ban", "exilado(a)", "Remove o usuário do servidor e apaga suas mensagens", new Color(0xff6800)),
    TMP_BAN(5, "Temp-ban", "banido(a) temporariamente", "Remove o usuário do servidor, impossibilitado de entrar por algum tempo", new Color(0xFF4700)),
    BAN(6, "Ban", "banido(a)", "Remove o usuário permanentemente do servidor", new Color(0xff0000));

    private final int id;
    private final String keyword;
    private final String verb;
    private final String description;
    private final Color color;

    PunishType(int id, String keyword, String verb, String description, Color color) {
      this.id = id;
      this.keyword = keyword;
      this.verb = verb;
      this.description = description;
      this.color = color;
    }

    public static PunishType fromId(int id) {
      for (PunishType et : values()) {
        if (id == et.getId()) {
          return et;
        }
      }
      return KICK;
    }

    public int getId() {
      return id;
    }

    public String getDescription() {
      return description;
    }

    public String getKeyword() {
      return keyword;
    }

    public Color getColor() {
      return color;
    }

    public String getVerb() {
      return verb;
    }
  }
}
