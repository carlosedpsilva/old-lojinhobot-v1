package com.lojinho.bot.command.commands.owner;

import java.util.List;

import com.lojinho.bot.command.meta.AbstractCommand;
import com.lojinho.bot.core.ExitCode;
import com.lojinho.bot.main.Launcher;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.permission.UserRank;
import com.lojinho.bot.util.Emojibet;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ExitCommand extends AbstractCommand {
  public ExitCommand() {
    super();
  }

  @Override
  public String getDescription() {
    return "Desliga o bot";
  }

  @Override
  public String getCommand() {
    return "exit";
  }

  @Override
  public String[] getUsage() {
    return new String[] {};
  }

  @Override
  public String[] getAliases() {
    return new String[] { "shutdown", "turnoff", "brexit" };
  }

  @Override
  public String execute(LojinhoBot bot, List<String> args, MessageChannel channel, Message message) {
    UserRank rank = bot.security.getUserRank(message.getAuthor());
    if (rank.isAtLeast(UserRank.BOT_ADMIN)) {
      LojinhoBot.LOGGER.info("Desligando");
      Launcher.stop(ExitCode.STOP);
      return Emojibet.THUMBS_UP;
    }
    return "Permissões insuficientes";
  }

}
