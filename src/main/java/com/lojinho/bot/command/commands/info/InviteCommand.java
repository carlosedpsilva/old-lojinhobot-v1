package com.lojinho.bot.command.commands.info;

import java.awt.Color;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;

import com.lojinho.bot.data.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class InviteCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setTitle("Clique aqui para adicionar-me em outro servidor",
        "https://discord.com/oauth2/authorize?client_id=566710691806052360&scope=bot&permissions=8")
        .setDescription("Agradeço a preferencia").setColor(new Color(16750336))
        .setImage("https://i.pinimg.com/originals/de/13/5b/de135bd63be0e749c8fe7aee7f5f083a.jpg");
    ctx.getChannel().sendMessage("link para me adicionar enviado na DM").queue();
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

  @Override
  public String getHelp() {
    return "Me adicione no seu servidor!\n" + "Uso: " + Config.get("PREFIX") + "invite";
  }

}
