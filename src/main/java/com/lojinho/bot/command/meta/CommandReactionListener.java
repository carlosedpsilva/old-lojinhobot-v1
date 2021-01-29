package com.lojinho.bot.command.meta;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.Message;

public class CommandReactionListener<T> {

  private final LinkedHashMap<String, Consumer<Message>> reactions;
  private final long userId;
  private volatile T data;
  private long expiresIn, lastAction;
  private boolean active;
  private boolean registerRemove;

  public CommandReactionListener(long userId, boolean registerRemove, T data) {
    this.data = data;
    this.userId = userId;
    this.registerRemove = registerRemove;
    reactions = new LinkedHashMap<>();
    active = true;
    lastAction = System.currentTimeMillis();
    expiresIn = TimeUnit.MINUTES.toMillis(5);
  }

  public boolean isActive() {
    return active;
  }

  public void disable() {
    this.active = false;
  }

  /**
   * O tempo em que o listener expira que é now + specified time. Default: now +
   * 5min
   *
   * @param timeUnit time units
   * @param time     amount of time units
   */
  public void setExpiresIn(TimeUnit timeUnit, long time) {
    expiresIn = timeUnit.toMillis(time);
  }

  /**
   * Verifica se o listener possui o emoji especificado
   *
   * @param emote o emote a ser verificado
   * @return este listener faz algo com esse emote?
   */
  public boolean hasReaction(String emote) {
    return reactions.containsKey(emote);
  }

  /**
   * Reagir à reação
   *
   * @param emote   o emote usado
   * @param message a mensagem ligada à reação
   */
  public void react(String emote, Message message) {
    reactions.get(emote).accept(message);
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public boolean registerRemove() {
    return registerRemove;
  }

  /**
   * Registra um consumer para o emote especificado
   *
   * @param emote    the emote to respond to
   * @param consumer the behaviour when emote is used
   */
  public void registerReaction(String emote, Consumer<Message> consumer) {
    reactions.put(emote, consumer);
  }

  /**
   * @return list of all emotes used in this reaction listener
   */
  public Set<String> getEmotes() {
    return reactions.keySet();
  }

  /**
   * Atualiza o timestamp de quando a reação foi utilizada pela última vez
   */
  public void updateLastAction() {
    lastAction = System.currentTimeMillis();
  }

  /**
   * Quando este reaction listener expira?
   *
   * @return timestamp in millis
   */
  public Long getExpiresInTimestamp() {
    return lastAction + expiresIn;
  }

  public long getUserId() {
    return userId;
  }
}
