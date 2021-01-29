package com.lojinho.bot.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lojinho.bot.db.controllers.role.CRoleReaction;
import com.lojinho.bot.db.model.role.ORoleReaction;
import com.lojinho.bot.db.model.role.ORoleReactionKey;
import com.lojinho.bot.main.LojinhoBot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class RoleReactionHandler {
  // {guild_id, {message_id, {emoji_id, role_id}}}
  private final Map<Long, Map<Long, Map<String, ArrayList<Long>>>> listeners;

  // Register Handler
  public RoleReactionHandler(LojinhoBot discordBot) {
    listeners = new ConcurrentHashMap<>();
  }

  private synchronized boolean isListening(long guildId, long messageId) {
    return listeners.containsKey(guildId) && listeners.get(guildId).containsKey(messageId);
  }

  private boolean isListeningToReaction(long guildId, long messageId, String emote) {
    return listeners.get(guildId).get(messageId).containsKey(emote);
  }

  public synchronized boolean handle(long messageId, TextChannel channel, Member invoker,
      MessageReaction.ReactionEmote rEmote, boolean isAdding) {

    long guildId = channel.getGuild().getIdLong();
    initGuild(guildId, false);

    String emote;
    if (rEmote.getId() == null)
      emote = rEmote.getName();
    else
      emote = rEmote.getId();

    if (!isListening(guildId, messageId))
      return false;

    if (isListeningToReaction(guildId, messageId, emote)) {
      ArrayList<Role> roles = new ArrayList<>();
      for (Long roleId : listeners.get(guildId).get(messageId).get(emote))
        roles.add(channel.getGuild().getRoleById(roleId));

      if (isAdding)
        for (Role role : roles)
          channel.getGuild().addRoleToMember(invoker, role).queue();
      else
        for (Role role : roles)
          channel.getGuild().removeRoleFromMember(invoker, role).queue();
    }
    return false;
  }

  /**
   * Obter do banco de dados os listeners
   * 
   * @param guildId
   * @param forceReload
   * @return
   */
  public synchronized boolean initGuild(long guildId, boolean forceReload) {
    if (!forceReload && listeners.containsKey(guildId))
      return true;

    if (forceReload)
      removeGuild(guildId);

    List<ORoleReactionKey> keys = CRoleReaction.getKeysForGuild(guildId);
    for (ORoleReactionKey key : keys) {
      if (key.message_id <= 0)
        continue;
      addMessage(guildId, key.message_id);

      List<ORoleReaction> reactions = CRoleReaction.getReactionsForKey(key.id);
      for (ORoleReaction r : reactions)
        addMessageReaction(guildId, key.message_id, r.emote_id, r.role_id);
    }

    return false;
  }

  /**
   * Remove todos os listeners dessa guild
   * 
   * @param guildId
   */
  public synchronized void removeGuild(long guildId) {
    if (listeners.containsKey(guildId)) {
      listeners.remove(guildId);
    }
  }

  /**
   * Adicionar mensagem aos listeners
   * 
   * @param guildId
   * @param messageId
   */
  public synchronized void addMessage(long guildId, long messageId) {
    if (!listeners.containsKey(guildId)) {
      listeners.put(guildId, new ConcurrentHashMap<>());
    }
    if (!listeners.get(guildId).containsKey(messageId)) {
      listeners.get(guildId).put(messageId, new ConcurrentHashMap<>());
    }
  }

  /**
   * Remover mensagem dos listeners
   * 
   * @param guild
   * @param messageId
   */
  public synchronized void removeMessage(long guildId, long messageId) {
    if (listeners.containsKey(guildId))
      listeners.get(guildId).remove(messageId);
  }

  /**
   * Adicionar cargo à reação do listener de uma mensagem
   * 
   * @param guildId
   * @param messageId
   * @param emote
   * @param roleId
   */
  private void addMessageReaction(long guildId, long messageId, String emote, long roleId) {
    if (!listeners.get(guildId).get(messageId).containsKey(emote)) {
      listeners.get(guildId).get(messageId).put(emote, new ArrayList<Long>());
    }
    if (!listeners.get(guildId).get(messageId).get(emote).contains(roleId)) {
      listeners.get(guildId).get(messageId).get(emote).add(roleId);
    }
  }

  /**
   * Remover reação do listener de uma mensagem
   * 
   * @param guildId
   * @param messageId
   * @param emote
   * @param roleId
   */
  private void removeMessageReaction(long guildId, long messageId, String emote) {
    listeners.get(guildId).get(messageId).remove(emote);
  }
}
