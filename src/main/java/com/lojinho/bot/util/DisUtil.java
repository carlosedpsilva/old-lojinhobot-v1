package com.lojinho.bot.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.controllers.CGuildMember;
import com.lojinho.bot.db.controllers.CUser;
import com.lojinho.bot.db.controllers.role.CGuildRole;
import com.lojinho.bot.db.model.OGuild;
import com.lojinho.bot.db.model.OGuildMember;
import com.lojinho.bot.db.model.OUser;
import com.lojinho.bot.db.model.role.OGuildRole;
import com.lojinho.bot.guildsettings.DefaultGuildSettings;
import com.lojinho.bot.guildsettings.GSetting;
import com.lojinho.bot.handler.GuildSettings;
import com.lojinho.bot.main.LojinhoBot;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DisUtil {
  private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{18})>");
  private static final Pattern channelPattern = Pattern.compile("<#!?([0-9]{18})>");
  private static final Pattern rolePattern = Pattern.compile("<@&([0-9]{18})>");
  private static final Pattern anyMention = Pattern.compile("<[@#][&!]?([0-9]{18})>");
  private static final Pattern discordId = Pattern.compile("([0-9]{18})");

  // ********************** OTHER **********************

  public static boolean isEmote(LojinhoBot bot, String emote) {
    if (EmojiUtils.isEmoji(emote) || Misc.isGuildEmote(emote)) {
      return true;
    }
    if (emote.matches("\\d+")) {
      return bot.getJda().getEmoteById(emote) != null;
    }
    return false;
  }

  // ********************** COMMAND UTILS **********************

  /**
   * Obtém o prefixo para dado canal, guild ou guildId
   *
   * @param search channel, guild ou guildId
   * @return the command prefix
   */
  public static String getCommandPrefix(TextChannel channel) {
    if (channel instanceof TextChannel) {
      return getCommandPrefix(((TextChannel) channel).getGuild());
    }
    return DefaultGuildSettings.getDefault(GSetting.COMMAND_PREFIX);
  }

  public static String getCommandPrefix(Guild guild) {
    return getCommandPrefix(guild.getIdLong());
  }

  public static String getCommandPrefix(long guildId) {
    return GuildSettings.get(guildId).getOrDefault(GSetting.COMMAND_PREFIX);
  }

  /**
   * Filtra a menção de um comando de uma dada String
   *
   * @param command o texto a ser filtrado
   * @return text with the prefix filtered
   */
  public static String filterMention(String command) {
    Matcher matcher = mentionUserPattern.matcher(command);
    if (matcher.find()) {
      if (command.startsWith(matcher.group(0))) {
        return command.substring(matcher.group(0).length()).trim();
      }
    }
    return command;
  }

  /**
   * Filtra o prefixo de um comando de uma dada String
   *
   * @param command o texto a ser filtrado
   * @param channel o canal de onde o texto veio
   * @return text with the prefix filtered
   */
  public static String filterPrefix(String command, TextChannel channel) {
    String prefix = getCommandPrefix(channel);
    if (command.startsWith(prefix)) {
      return command.substring(prefix.length()).trim();
    }
    return command;
  }

  // ********************** MENTION MATCHERS **********************

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
   * Verificar se dado String possui um discordId
   * 
   * @param input - a menção a ser verificada
   * @return true or false
   */
  public static boolean hasDiscordId(String input) {
    return discordId.matcher(input).find();
  }

  /**
   * Extrai a sequência de digitos de um id
   *
   * @param id raw id
   * @return Match or null
   */
  public static Long extractIdLong(String id) {
    return extractId(id) != null ? Long.valueOf(extractId(id)) : null;
  }

  public static String extractId(String id) {
    Matcher matcher = discordId.matcher(id);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
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

  // ********************** FINDER UTILS **********************

  /**
   * Obter uma guild com base em uma String
   * 
   * @param channel
   * @param content
   * @return
   */
  public static Guild findGuild(TextChannel channel, String content) {
    return findGuild(channel.getGuild(), content);
  }

  public static Guild findGuild(Guild guild, String content) {
    if (hasDiscordId(content)) {
      OGuild dbGuild = CGuild.findBy(extractId(content));
      if (dbGuild.id > 0)
        return guild.getJDA().getGuildById(dbGuild.discord_id);
      return guild.getJDA().getGuildById(extractId(content));
    }
    return null;
  }

  /**
   * Obter um textchannel com base em uma String
   * 
   * @param channel
   * @param content
   * @return
   */
  public static TextChannel findTextChannel(TextChannel channel, String content) {
    List<TextChannel> found = findTextChannels(channel, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none

    if (found.size() > 1 && !content.isEmpty()) {
      channel.sendMessage(String.format(
          "Muitos canais encontrados encontrados com '%s', refine sua busca (ex. usando id do canal)\n**Canais encontrados:** %s",
          content, found.stream().limit(7).map(t -> t.getName() + ":" + t.getId()).collect(Collectors.joining("; "))))
          .queue();
      return null; // found too many
    }
    // found one
    return found.get(0);
  }

  public static TextChannel findTextChannel(Guild guild, String content) {
    List<TextChannel> found = findTextChannels(guild, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none
    // found at least one
    return found.get(0);
  }

  public static List<TextChannel> findTextChannels(TextChannel channel, String content) {
    return findTextChannels(channel.getGuild(), content);
  }

  public static List<TextChannel> findTextChannels(Guild guild, String content) {
    List<TextChannel> found = FinderUtil.findTextChannels(content, guild);
    if (found.isEmpty() && !content.isEmpty())
      return null; // found none
    return found;
  }

  /**
   * Obter um voicechannel com base em uma String
   * 
   * @param channel
   * @param content
   * @return
   */
  public static VoiceChannel findVoiceChannel(TextChannel channel, String content) {
    List<VoiceChannel> found = findVoiceChannels(channel, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none

    if (found.size() > 1 && !content.isEmpty()) {
      channel.sendMessage(String.format(
          "Muitos canais encontrados encontrados com '%s', refine sua busca (ex. usando id do canal)\n**Canais encontrados:** %s",
          content, found.stream().limit(7).map(t -> t.getName() + ":" + t.getId()).collect(Collectors.joining("; "))))
          .queue();
      return null; // found too many
    }
    // found one
    return found.get(0);
  }

  public static VoiceChannel findVoiceChannel(Guild guild, String content) {
    List<VoiceChannel> found = findVoiceChannels(guild, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none
    // found at least one
    return found.get(0);
  }

  public static List<VoiceChannel> findVoiceChannels(TextChannel channel, String content) {
    return findVoiceChannels(channel.getGuild(), content);
  }

  public static List<VoiceChannel> findVoiceChannels(Guild guild, String content) {
    List<VoiceChannel> found = FinderUtil.findVoiceChannels(content, guild);
    if (found.isEmpty() && !content.isEmpty())
      return null; // found none
    return found;
  }

  /**
   * Obter um cargo com base em uma String
   * 
   * @param channel
   * @param content
   * @return
   */
  public static Role findRole(TextChannel channel, String content) {
    List<Role> found = findRoles(channel, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none

    if (found.size() > 1 && !content.isEmpty()) {
      channel.sendMessage(String.format(
          "Muitos cargos encontrados encontrados com '%s', refine sua busca (ex. usando id do cargo)\n**Canais encontrados:** %s",
          content, found.stream().limit(7).map(r -> r.getName() + ":" + r.getId()).collect(Collectors.joining("; "))))
          .queue();
      return null; // found too many
    }
    // found one
    return found.get(0);
  }

  public static Role findRole(Guild guild, String content) {
    if (isRoleMention(content)) {
      OGuildRole s = CGuildRole.findBy(extractId(content));
      if (s.id > 0)
        return guild.getRoleById(s.discord_id);
      return guild.getRoleById(extractId(content));
    }
    List<Role> found = findRoles(guild, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none
    // found at least one
    return found.get(0);
  }

  public static List<Role> findRoles(TextChannel channel, String content) {
    return findRoles(channel.getGuild(), content);
  }

  public static List<Role> findRoles(Guild guild, String content) {
    List<Role> found = FinderUtil.findRoles(content, guild);
    if (found.isEmpty() && !content.isEmpty())
      return null; // found none
    return found;
  }

  /**
   * Obter um membro com base em uma String
   * 
   * @param channel
   * @param content
   * @return
   */
  public static Member findMember(TextChannel channel, String content) {
    List<Member> found = findMembers(channel, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none

    if (found.size() > 1 && !content.isEmpty()) {
      channel.sendMessage(String.format(
          "Muitos usuários encontrados com '%s', refine sua busca (ex. usando name#discriminator)\n**Usuários encontrados:** %s",
          content, found.stream().limit(7).map(m -> m.getUser().getName() + "#" + m.getUser().getDiscriminator())
              .collect(Collectors.joining(", "))))
          .queue();
      return null; // found too many
    }
    // found one
    return found.get(0);
  }

  public static Member findMember(Guild guild, String content) {
    if (isUserMention(content)) {
      OGuildMember s = CGuildMember.findBy(guild.getIdLong(), extractIdLong(content));
      if (s.userId > 0)
        return guild.getMemberById(s.userId);
      return guild.getMemberById(extractId(content));
    }
    List<Member> found = findMembers(guild, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none
    // found at least one
    return found.get(0);
  }

  public static List<Member> findMembers(TextChannel channel, String content) {
    return findMembers(channel.getGuild(), content);
  }

  public static List<Member> findMembers(Guild guild, String content) {
    List<Member> found = FinderUtil.findMembers(content, guild);
    if (found.isEmpty() && !content.isEmpty())
      return null; // found none
    return found;
  }

  /**
   * Obter um usuário com base em uma String
   * 
   * @param channel
   * @param content
   * @return
   */
  public static User findUser(TextChannel channel, String content) {
    List<User> found = findUsers(channel, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none

    if (found.size() > 1 && !content.isEmpty()) {
      channel.sendMessage(String.format(
          "Muitos usuários encontrados com '%s', refine sua busca (ex. usando name#discriminator)\n**Canais encontrados:** %s",
          content,
          found.stream().limit(7).map(u -> u.getName() + "#" + u.getDiscriminator()).collect(Collectors.joining(", "))))
          .queue();
      return null; // found too many
    }
    // found one
    return found.get(0);
  }

  public static User findUser(Guild guild, String content) {
    if (isUserMention(content)) {
      OUser s = CUser.findBy(extractId(content));
      if (s.id > 0)
        return guild.getJDA().getUserById(s.discord_id);
      return guild.getJDA().getUserById(extractId(content));
    }
    List<User> found = findUsers(guild, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none
    // found at least one
    return found.get(0);
  }

  public static List<User> findUsers(TextChannel channel, String content) {
    return findUsers(channel.getGuild(), content);
  }

  public static List<User> findUsers(Guild guild, String content) {
    List<User> found = FinderUtil.findUsers(content, guild.getJDA());
    if (found.isEmpty() && !content.isEmpty())
      return null; // found none
    return found;
  }

  /**
   * Obter um usuário banido com base em uma String
   * 
   * @param guild
   * @param content
   * @return
   */
  public static User findBannedUser(TextChannel channel, String content) {
    List<User> found = findBannedUsers(channel, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none

    if (found.size() > 1 && !content.isEmpty()) {
      channel.sendMessage(String.format(
          "Muitos usuários encontrados com '%s', refine sua busca (ex. usando name#discriminator)\n**Canais encontrados:** %s",
          content,
          found.stream().limit(7).map(u -> u.getName() + "#" + u.getDiscriminator()).collect(Collectors.joining(", "))))
          .queue();
      return null; // found too many
    }
    // found one
    return found.get(0);
  }

  public static User findBannedUser(Guild guild, String content) {
    List<User> found = findBannedUsers(guild, content);
    if (found == null || (found.isEmpty() && !content.isEmpty()))
      return null; // found none
    // found at least one
    return found.get(0);
  }

  public static List<User> findBannedUsers(TextChannel channel, String content) {
    return findBannedUsers(channel.getGuild(), content);
  }

  public static List<User> findBannedUsers(Guild guild, String content) {
    List<User> found = FinderUtil.findBannedUsers(content, guild);
    if (found.isEmpty() && !content.isEmpty())
      return null; // found none
    return found;
  }
}
