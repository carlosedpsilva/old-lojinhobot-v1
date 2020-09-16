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
          .setColor(new Color(16750336))
          .addField("`loj.ping`", "Retorna Pong! e o ping no momento que foi solicitado", false)
          .addField("`loj.avatar`", "Retorna o avatar de perfil", false)
          .addField("`loj.invite`", "Me adicione no seu servidor!", false)
          .addField("`loj.support`", "Contatos para suporte sobre o bot", false);
      ctx.getChannel().sendMessage(eb.build()).queue();
      return;
    }

    String search = args.get(0);
    ICommand command = manager.getCommand(search);
    if (command.getHelp() != null)
    {
      eb.addField("`" + command.getName() + "`", "" + command.getHelp(), false)
              .setColor(new Color(16750336));
      ctx.getChannel().sendMessage(eb.build()).queue();
    }
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getHelp() {
    return "Mostra uma lista com os comandos do LojinhoBot\n" + "Uso: " + Config.get("TOKEN") + "help [comando]";
  }

}