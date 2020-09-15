package com.lojinho.bot.command.commands;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.duncte123.botcommons.BotCommons;

public class ShutdownCommand implements ICommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownCommand.class);

  @Override
  public void handle(CommandContext ctx) {
    if (ctx.getAuthor().getId().equals(Config.get("OWNER_ID"))) {
      LOGGER.info("Shutting down ran by " + ctx.getAuthor().getName() + "#" + ctx.getAuthor().getDiscriminator());
      ctx.getJDA().shutdown();
      BotCommons.shutdown(ctx.getJDA());
    }
  }

  @Override
  public String getName() {
    return "shutdown";
  }

  @Override
  public String getHelp() {
    return null;
  }

}
