package com.lojinho.bot.command.commands.roles;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

public class SelfRoleCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    return;
  }

  @Override
  public String getCategory() {
    return "Roles";
  }

  @Override
  public String getTitle() {
    return "SelfRole Command";
  }

  @Override
  public String getName() {
    return "selfrole";
  }

  @Override
  public String getHelp() {
    return "Auto-atribuição de cargos.";
  }

  @Override
  public String getUsage() {
    return null;
  }

  @Override
  public String getParameters() {
    return null;
  }

}
