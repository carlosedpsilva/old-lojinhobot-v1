package com.lojinho.bot.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Utils {

  public static Member findMember(GuildMessageReceivedEvent event, Member first, String content) {
    List<Member> found = FinderUtil.findMembers(content, event.getGuild());
    if (found.isEmpty() && !content.isEmpty()) {
      return null;
    }

    if (found.size() > 1 && !content.isEmpty()) {
      event.getChannel().sendMessage(String.format(
          "Muitos membros encontrados com '%s', refine sua busca (ex. usando name#discriminator)\n**Usuários encontrados:** %s",
          found.stream().limit(7).map(m -> m.getUser().getName() + "#" + m.getUser().getDiscriminator())
              .collect(Collectors.joining(", "))))
          .queue();

      return null;
    }

    if (found.size() == 1) {
      return found.get(0);
    }

    return first;
  }

  public static User findBannedUser(GuildMessageReceivedEvent event, User first, String content) {
    List<User> found = FinderUtil.findBannedUsers(content, event.getGuild());
    if (found.isEmpty() && !content.isEmpty()) {
      return null;
    }

    if (found.size() > 1 && !content.isEmpty()) {
      event.getChannel().sendMessage(String.format(
          "Muitos usuários encontrados com '%s', refine sua busca (ex. usando name#discriminator)\n**Usuários encontrados:** %s",
          content,
          found.stream().limit(7).map(u -> u.getName() + "#" + u.getDiscriminator()).collect(Collectors.joining(", "))))
          .queue();

      return null;
    }

    if (found.size() == 1) {
      return found.get(0);
    }

    return first;
  }
}
