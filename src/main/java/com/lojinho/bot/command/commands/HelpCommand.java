package com.lojinho.bot.command.commands;

import java.awt.*;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

import net.dv8tion.jda.api.EmbedBuilder;

public class HelpCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Comandos do Incrivel LojinhoBot")
        .setDescription("Aqui vai uma incrivel lista de comandos que esse incrivel bot possui")
        .setColor(new Color(16750336))
        .addField("`loj.ping`", "Retorna Pong! e o ping no momento que foi solicitado", false)
        .addField("`loj.avatar`", "Use só o comando para ver sua propria foto, ou marque alguem após o comando", false);
    ctx.getChannel().sendMessage(eb.build()).queue();

  }

  @Override
  public String getName() {
    return "help";
  }

}
