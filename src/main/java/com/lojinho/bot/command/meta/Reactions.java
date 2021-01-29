package com.lojinho.bot.command.meta;

import com.lojinho.bot.util.Emojibet;

public enum Reactions {

  DEFAULT(ReactionType.USER_INPUT, Emojibet.STAR, "null");

  private final ReactionType reactionType;
  private final String emote;
  private final String description;

  Reactions(ReactionType reactionType, String emote, String description) {
    this.reactionType = reactionType;
    this.emote = emote;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getEmote() {
    return emote;
  }

  public ReactionType getReactionType() {
    return reactionType;
  }
}
