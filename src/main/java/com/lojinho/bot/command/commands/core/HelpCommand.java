package com.lojinho.bot.command.commands.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.CommandManager;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class HelpCommand implements ICommand {

  // Para acessar os comandos
  private final CommandManager manager;

  public HelpCommand(CommandManager manager) {
    this.manager = manager;
  }

  @Override
  public void handle(CommandContext ctx) {

    List<String> args = ctx.getArgs();
    TextChannel channel = ctx.getChannel();
    EmbedBuilder eb = new EmbedBuilder();

    if (args.isEmpty()) {
      ArrayList<String> categories = new ArrayList<String>(
          manager.getCategories().values().stream().distinct().collect(Collectors.toSet()));

      eb.setAuthor("LojinhoBot Help", null, ctx.getSelfUser().getAvatarUrl())

          .setDescription(
              "Lista completa de comandos. Para mais detalhes utilize " + Config.get("PREFIX") + "help <comando>");

      for (String category : categories) {
        List<String> commandsInThisCategory = new ArrayList<String>();
        for (String entry : manager.getCategories().keySet()) {
          if (category.equals(manager.getCategories().get(entry))) {
            commandsInThisCategory.add(entry);
          }
        }

        commandsInThisCategory = commandsInThisCategory.stream().map(s -> "`" + s).collect(Collectors.toList());
        commandsInThisCategory = commandsInThisCategory.stream().map(s -> s + "`").collect(Collectors.toList());

        eb.addField("" + category, "" + commandsInThisCategory.stream().collect(Collectors.joining(" ")), false)
            .setColor(new Color(16750336));
      }

      channel.sendMessage(eb.build()).queue();
      return;
    }

    String search = args.get(0);
    ICommand command = manager.getCommand(search);
    if (command != null && command.getHelp() != null) {
      eb.setAuthor(command.getTitle() + " Help", null, ctx.getSelfUser().getAvatarUrl())
          .setDescription(command.getHelp()).addField("**Uso**", "" + command.getUsage(), false);

      if (command.getParameters() != null) {
        eb.addField("**Parâmetros**", "" + command.getParameters(), false);
      }

      eb.setFooter("<> obrigatório, [] opcional").setColor(new Color(16750336));
      channel.sendMessage(eb.build()).queue();
    }
  }

  @Override
  public String getCategory() {
    return "Core";
  }

  @Override
  public String getTitle() {
    return "Help Command";
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getHelp() {
    return "Mostra uma lista com os comandos do LojinhoBot";
  }

  @Override
  public String getUsage() {
    return Config.get("PREFIX") + this.getName() + " [comando]";
  }

  @Override
  public String getParameters() {
    return "`commando` - O nome do comando que deseja-se obter informação sobre.";
  }
}
