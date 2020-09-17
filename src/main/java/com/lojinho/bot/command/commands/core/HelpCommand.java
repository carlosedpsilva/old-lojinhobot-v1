package com.lojinho.bot.command.commands.core;

import java.awt.Color;
import java.util.List;

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

    if (args.isEmpty())
    {
      eb.setTitle("Comandos do Incrivel LojinhoBot")
          .setDescription("Aqui vai uma incrivel lista de comandos que esse incrivel bot possui")
          .setColor(new Color(16750336));
          for(ICommand command : manager.getCommands())
          {
            if (command.getHelp() != null) {
              eb.addField("`" + command.getName() + "`", "" + command.getHelp(), false);
            }
          }
      channel.sendMessage(eb.build()).queue();
      return;
    }

    String search = args.get(0);
    ICommand command = manager.getCommand(search);
    if (command != null && command.getHelp() != null) {
      eb.addField("`" + command.getName() + "`", "" + command.getHelp(), false)
              .setColor(new Color(16750336));
      channel.sendMessage(eb.build()).queue();
    }
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getHelp() {
    return "Mostra uma lista com os comandos do LojinhoBot\n" + "Uso: " + Config.get("PREFIX") + "help [comando]";
  }
  }
