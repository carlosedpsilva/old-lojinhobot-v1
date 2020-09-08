package com.lojinho.bot.command.commands;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class InviteCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    EmbedBuilder eb = new EmbedBuilder();
    sendPrivateMessage(ctx.getAuthor(), eb.build());
  }

  public void sendPrivateMessage(User user, MessageEmbed content) {
    user.openPrivateChannel().queue((Channel) -> {
      Channel.sendMessage(content).queue();
    });
  }

  @Override
  public String getName() {
    return "invite";
  }

}
