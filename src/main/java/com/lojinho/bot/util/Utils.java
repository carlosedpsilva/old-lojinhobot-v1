package com.lojinho.bot.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Utils {
  private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{8,})>");
  private static final Pattern channelPattern = Pattern.compile("<#!?([0-9]{4,})>");
  private static final Pattern rolePattern = Pattern.compile("<@&([0-9]{4,})>");
  private static final Pattern anyMention = Pattern.compile("<[@#][&!]?([0-9]{4,})>");
  private static final Pattern discordId = Pattern.compile("(\\d{9,})");

  /**
   * Tenta encontrar um usuário banido em um dado servidor
   *
   * @param event   - jda event
   * @param first   - membro a ser retornado por padrão para o método funcionar
   * @param content - o input a ser usado para encontrar o membro
   * @return Member or null
   */
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

    // Pra funcionar
    return first;
  }

  /**
   * Tenta encontrar um usuário banido em um dado servidor
   *
   * @param event   - jda event
   * @param first   - usuário a ser retornado por padrão para o método funcionar
   * @param content - o input a ser usado para encontrar o usuário
   * @return User or null
   */
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

    // Pra funcionar
    return first;
  }

  /**
   * Tenta encontrar um canal de texto pelo nome em um dado servidor
   *
   * @param guild       - o servidor onde buscar
   * @param channelName - o canal a ser encontrado
   * @return TextChannel or null
   */
  public static TextChannel findChannel(Guild guild, String channelName) {
    for (TextChannel channel : guild.getTextChannels()) {
      if (channel.getName().equalsIgnoreCase(channelName)) {
        return channel;
      }
    }
    return null;
  }

  /**
   * Tenta encontrar um canal de voz pelo nome em um dado servidor
   *
   * @param guild       - o servidor onde buscar
   * @param channelName - o canal a ser encontrado
   * @return TextChannel or null
   */
  public static VoiceChannel findVoiceChannel(Guild guild, String channelName) {
    for (VoiceChannel channel : guild.getVoiceChannels()) {
      if (channel.getName().equalsIgnoreCase(channelName)) {
        return channel;
      }
    }
    return null;
  }

  /**
   * Tenta encontrar um cargo em um dado servidor
   * 
   * @param guild    - o servidor onde buscar
   * @param roleName - o cargo a ser encontrado
   * @return role or null
   */
  public static Role findRole(Guild guild, String roleName) {
    List<Role> roles = guild.getRoles();
    Role containsRole = null;
    for (Role role : roles) {
      if (role.getName().equalsIgnoreCase(roleName)) {
        return role;
      }
      if (containsRole == null && role.getName().contains(roleName)) {
        containsRole = role;
      }
    }
    return containsRole;
  }

  /**
   * Tenta encontrar um cargo em um servidor
   * 
   * @param guild    - o servidor onde buscar
   * @param roleName - o cargo a ser encontrado
   * @return role or null
   */
  public static Role hasRole(Guild guild, String roleName) {
    List<Role> roles = guild.getRoles();
    Role containsRole = null;
    for (Role role : roles) {
      if (role.getName().equalsIgnoreCase(roleName)) {
        return role;
      }
      if (containsRole == null && role.getName().contains(roleName)) {
        containsRole = role;
      }
    }
    return containsRole;
  }

  /**
   * Verificar se dada menção é de um usuário
   * 
   * @param input - a menção a ser verificada
   * @return true or false
   */
  public static boolean isUserMention(String input) {
    return mentionUserPattern.matcher(input).find();
  }

  /**
   * Verificar se dada menção é de um canal
   * 
   * @param input - a menção a ser verificada
   * @return true or false
   */
  public static boolean isChannelMention(String input) {
    return channelPattern.matcher(input).matches();
  }

  /**
   * Verificar se dada menção é de um cargo
   * 
   * @param input - a menção a ser verificada
   * @return true or false
   */
  public static boolean isRoleMention(String input) {
    return rolePattern.matcher(input).find();
  }

  /**
   * Converte qualquer menção para um id
   *
   * @param mention a menção a ser filtrada
   * @return uma versão simplificada da menção
   */
  public static String mentionToId(String mention) {
    String id = "";
    Matcher matcher = anyMention.matcher(mention);
    if (matcher.find()) {
      id = matcher.group(1);
    }
    return id;
  }
}
