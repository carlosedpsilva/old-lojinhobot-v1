package com.lojinho.bot.command.commands;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

import net.dv8tion.jda.api.JDA;

public class PingCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    JDA jda = ctx.getJDA();
    System.out.println("chegaste aq");
    jda.getRestPing().queue((ping) -> ctx.getChannel()
        .sendMessageFormat("Reset ping: %sms \nWS ping: %sms ", ping, jda.getGatewayPing()).queue());

  }

  @Override
  public String getName() {
    return "ping";
  }
}
