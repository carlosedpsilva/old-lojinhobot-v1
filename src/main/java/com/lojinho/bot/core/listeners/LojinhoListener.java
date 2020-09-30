package com.lojinho.bot.core.listeners;

import javax.annotation.Nonnull;

import com.lojinho.bot.command.CommandManager;
import com.lojinho.bot.db.DatabaseManager;

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

    if (event.getAuthor().isBot()) {
      return;
    }

    final long guildId = event.getGuild().getIdLong();
    String prefix = DatabaseManager.INSTANCE.getPrefix(guildId);
    String raw = event.getMessage().getContentRaw();

    if (raw.startsWith(prefix)) {
      manager.handle(event, prefix);
    }
  }

}
