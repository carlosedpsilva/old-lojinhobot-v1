package com.lojinho.bot.core.listeners;

import javax.annotation.Nonnull;

import com.lojinho.bot.command.CommandManager;
import com.lojinho.bot.data.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Classe que extende a classe abstrata ListenerAdapter do JDA e detecta
 * eventos.
 */
public class LojinhoListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(LojinhoListener.class);
  private final CommandManager manager = new CommandManager();

  /** Bot is ready event */
  @Override
  public void onReady(@Nonnull ReadyEvent event) {
    LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
  }

  /** Mensagem recebida no servidor */
  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    String raw = event.getMessage().getContentRaw();

    System.out
        .println("Msg recebida de " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());

    if (raw.startsWith(Config.get("PREFIX")) && !event.getAuthor().isBot()) {
      manager.handle(event);
    }
  }

}
