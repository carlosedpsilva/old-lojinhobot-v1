package com.lojinho.bot.command.commands.moderation;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

public class SoftBanCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {

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
    return "help";
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
