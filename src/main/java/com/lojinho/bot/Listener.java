package com.lojinho.bot;

import javax.annotation.Nonnull;

import com.lojinho.bot.command.CommandManager;
import com.lojinho.bot.data.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
  private final CommandManager manager = new CommandManager();

  @Override
  public void onReady(@Nonnull ReadyEvent event) {
    LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
  }

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    System.out.println(
        "We received a message from " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());

    String prefix = Config.get("PREFIX");
    String raw = event.getMessage().getContentRaw();

    // Shutdown
    if (raw.equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("OWNER_ID"))) {
      LOGGER.info("shutting down");
      event.getJDA().shutdown();
      BotCommons.shutdown(event.getJDA());

      return;
    }

    if (raw.startsWith(prefix) && event.getAuthor().isBot() == false) {
      manager.handle(event);
    }
  }

}
