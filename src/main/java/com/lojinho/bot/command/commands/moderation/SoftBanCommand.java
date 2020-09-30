package com.lojinho.bot.command.commands.moderation;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

public class SoftBanCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    return;
  }

  @Override
  public String getCategory() {
    return "Moderation";
  }

  @Override
  public String getTitle() {
    return "SoftBan Command";
  }

  @Override
  public String getName() {
    return "softban";
  }

  @Override
  public String getHelp() {
    return "Expulsa um usuário e deleta suas mensagens das últimas 24 horas.";
  }

  @Override
  public String getUsage() {
    return Config.get("PREFIX") + this.getName() + " <@menção> [razão]";
  }

  @Override
  public String getParameters() {
    return "`@menção` - O usuário a ser banido\n" + "`razão` - A razão para o softban.";
  }

}
