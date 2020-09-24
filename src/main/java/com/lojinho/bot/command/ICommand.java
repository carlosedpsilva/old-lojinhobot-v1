package com.lojinho.bot.command;

import java.util.List;

public interface ICommand {
  void handle(CommandContext ctx);

  String getCategory();

  String getTitle();

  String getName();

  String getHelp();

  String getUsage();

  String getParameters();

  default List<String> getAliases() {
    return List.of();
  }
}