package com.lojinho.bot.command.commands;

import java.awt.Color;


import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class SupportCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    EmbedBuilder eb = new EmbedBuilder();

    eb.setTitle("Lojinho bot Server", "https://discord.gg/CvPvNEb")
    .setDescription("clique para ser redirecinado ao servidor do Lojinho Bot")
    .setColor(new Color(16750336));
    ctx.getChannel().sendMessage("Servidor de suporte enviado na DM").queue();
    sendPrivateMessage(ctx.getAuthor(), eb.build());

  }

  public void sendPrivateMessage(User user, MessageEmbed content) {
    user.openPrivateChannel().queue((Channel) -> {
      Channel.sendMessage(content).queue();
    });
  }

  @Override
  public String getName() {
    return "support";
  }

}
