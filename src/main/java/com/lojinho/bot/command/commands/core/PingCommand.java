package com.lojinho.bot.command.commands.core;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

import com.lojinho.bot.data.Config;
import net.dv8tion.jda.api.JDA;

public class PingCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    JDA jda = ctx.getJDA();
    jda.getRestPing().queue((ping) -> ctx.getChannel()
        .sendMessageFormat("Reset ping: %sms \nWS ping: %sms ", ping, jda.getGatewayPing()).queue());

  }

  @Override
  public String getName() {
    return "ping";
  }

  @Override
  public String getHelp() {
    return "Retorna Pong! e o ping no momento que foi solicitado\n" + "Uso: " + Config.get("PREFIX") + "ping";
  }
}
