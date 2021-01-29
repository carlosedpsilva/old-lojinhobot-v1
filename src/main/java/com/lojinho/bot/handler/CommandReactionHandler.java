package com.lojinho.bot.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lojinho.bot.command.meta.CommandReactionListener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class CommandReactionHandler {
  // {guild_id, {message_id, listener}}
  private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, CommandReactionListener<?>>> reactions;

  public CommandReactionHandler() {
    reactions = new ConcurrentHashMap<>();
  }

  public void addReactionListener(long guildId, Message message, CommandReactionListener<?> handler) {
    if (handler == null) {
      return;
    }

    if (message.getChannelType().equals(ChannelType.TEXT)) {
      if (!PermissionUtil.checkPermission(message.getTextChannel(), message.getGuild().getSelfMember(),
          Permission.MESSAGE_ADD_REACTION)) {
        return;
      }
    }

    if (!reactions.contains(guildId)) {
      reactions.put(guildId, new ConcurrentHashMap<>());
    }
    if (!reactions.get(guildId).containsKey(message.getIdLong())) {
      for (String emote : handler.getEmotes()) {
        message.addReaction(emote).queue();

      }
      reactions.get(guildId).put(message.getIdLong(), handler);
    }
  }

  /**
   * Handles the reaction
   *
   * @param channel   TextChannel of the message
   * @param messageId id of the message
   * @param userId    id of the user reacting
   * @param reaction  the reaction
   */
  public void handle(TextChannel channel, long messageId, long userId, MessageReaction reaction, boolean isAdding) {
    CommandReactionListener<?> listener = reactions.get(channel.getGuild().getIdLong()).get(messageId);

    if (!isAdding) {
      return;
    }

    if (!listener.isActive() || listener.getExpiresInTimestamp() < System.currentTimeMillis()) {
      reactions.get(channel.getGuild().getIdLong()).remove(messageId);

    } else if (listener.hasReaction(reaction.getReactionEmote().getName()) && listener.getUserId() == userId) {
      reactions.get(channel.getGuild().getIdLong()).get(messageId).updateLastAction();
      Message message = channel.retrieveMessageById(messageId).complete();
      listener.react(reaction.getReactionEmote().getName(), message);
    }
  }

  /**
   * Verifica se há um evento para esta mensagem
   *
   * @param guildId   guild-id da mensagem
   * @param messageId id da mensagem
   * @return se há um evento para esta mensagem
   */
  public boolean canHandle(long guildId, long messageId) {
    return reactions.containsKey(guildId) && reactions.get(guildId).containsKey(messageId);
  }

  public synchronized void removeGuild(long guildId) {
    reactions.remove(guildId);
  }

  /**
   * Delete expired handlers
   */
  public synchronized void cleanCache() {
    long now = System.currentTimeMillis();
    for (Iterator<Map.Entry<Long, ConcurrentHashMap<Long, CommandReactionListener<?>>>> iterator = reactions.entrySet()
        .iterator(); iterator.hasNext();) {
      Map.Entry<Long, ConcurrentHashMap<Long, CommandReactionListener<?>>> mapEntry = iterator.next();
      mapEntry.getValue().values().removeIf(listener -> !listener.isActive() || listener.getExpiresInTimestamp() < now);
      if (mapEntry.getValue().values().isEmpty()) {
        reactions.remove(mapEntry.getKey());
      }
    }

  }
}