package com.lojinho.bot.command.commands.moderation;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;
import com.lojinho.bot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class SoftBanCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    final TextChannel channel = ctx.getChannel();
    final List<String> args = ctx.getArgs();

    // Implementar registro de canais de log depois
    TextChannel logChannel = ctx.getGuild().getTextChannelById(756186559832653955L);

    // Tratamento de opções
    LinkedHashMap<String, Integer> optionIndexes = new LinkedHashMap<String, Integer>();
    ArrayList<List<String>> argsSections = new ArrayList<List<String>>();

    {
      int index = 0;
      if (!args.get(0).startsWith("--")) {
        index++;
      }
      List<String> listOfArgs = new ArrayList<>();
      for (String arg : args) {
        if (arg.startsWith("--")) {
          // Nova seção de opção
          optionIndexes.put(arg.replaceFirst("(?i)" + Pattern.quote("--"), ""), index);
          argsSections.add(listOfArgs);
          listOfArgs = new ArrayList<String>();
          index++;
        } else {
          listOfArgs.add(arg);
        }
      }
      // Últimos parâmetros
      if (!listOfArgs.isEmpty()) {
        argsSections.add(listOfArgs);
      }
    }

    // Default parameters missing
    if (optionIndexes.containsValue(0)) {
      return;
    }

    // Tratamento de menções
    List<String> invalidMentions = new ArrayList<>();
    List<Member> mentionedMembers = new ArrayList<>();

    for (String arg : argsSections.get(0)) {
      Member target = Utils.findMember(ctx.getEvent(), ctx.getMember(), arg);
      if (target == null) {
        invalidMentions.add(arg);
      } else {
        mentionedMembers.add(target);
      }
    }

    // Nenhum usuário encontrado
    if (mentionedMembers.isEmpty()) {
      channel.sendMessage("Nenhum usuário encontrado.").queue();
      return;
    }

    // Tratamento de permissões
    Member member = ctx.getMember();
    Member selfMember = ctx.getGuild().getSelfMember();

    for (Member target : mentionedMembers) {
      if (!member.hasPermission(Permission.BAN_MEMBERS) || !member.canInteract(target)) {
        channel.sendMessage("Você não possui permissões para utilizar este comando.").queue();
        return;
      }

      if (!selfMember.hasPermission(Permission.BAN_MEMBERS) || !selfMember.canInteract(target)) {
        channel.sendMessage("Não possuo permissões para banir ou não posso banir este(s) usuário(s).").queue();
        return;
      }
    }

    // SoftBan Process
    String reason = "Não especificado.";
    String type = "Single";
    int delDays = 1;
    String delMsg = "";

    if (optionIndexes.containsKey("reason")) {
      reason = String.join(" ", argsSections.get(optionIndexes.get("reason")));
    }

    if (mentionedMembers.size() > 1) {
      type = "Multiple";
    }

    if (optionIndexes.containsKey("days")) {
      delDays = Integer.valueOf(argsSections.get(optionIndexes.get("days")).get(0));
      delMsg = String.format("Mensagens dos últimos %d dias apagadas.", delDays);
    }

    EmbedBuilder eb;
    List<Member> errBanMembers = new ArrayList<>();
    List<Member> errUnbanMembers = new ArrayList<>();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    for (Member target : mentionedMembers) {
      eb = new EmbedBuilder();
      eb.setAuthor("SoftBan log", null, target.getUser().getAvatarUrl()).setColor(Color.decode("#ff9c00"));
      eb.setDescription(String.format("**User:** %#s\n**Moderator:** %#s\n**Type:** %s\n**Reason:** %s\n**Date:** `%s`",
          target.getUser(), member.getUser(), type, reason, dtf.format(LocalDateTime.now()).toString()));
      MessageEmbed log = eb.build();
      String auditLog = String.format("SoftBan by %#s with reason '%s'", member.getUser(), reason);
      ctx.getGuild().ban(target, delDays, auditLog).reason(auditLog).queue((success1) -> ctx.getGuild()
          .unban(target.getUser().getId()).queue((success2) -> logChannel.sendMessage(log).queue(), (error) -> {
            errUnbanMembers.add(target);
            channel.sendMessage(String.format("Erro. Não foi possível desbanir %#s", target.getUser()));
          }), (error) -> errBanMembers.add(target));
    }

    if (errBanMembers.size() != mentionedMembers.size()) {
      channel.sendMessage(
          String.format("Ação moderativa realizada com sucesso. %d usuário(s) banido(s) e %d desbanidos. %s",
              mentionedMembers.size() - errBanMembers.size(),
              mentionedMembers.size() - errBanMembers.size() - errUnbanMembers.size(), delMsg))
          .queue();
    }

    if (!errBanMembers.isEmpty()) {
      channel.sendMessage(String.format("Não foi possível banir: %s", errBanMembers.stream().limit(7)
          .map(m -> m.getUser().getName() + "#" + m.getUser().getDiscriminator()).collect(Collectors.joining(", "))))
          .queue();
    }

    if (!errUnbanMembers.isEmpty()) {
      channel.sendMessage(String.format("Não foi possível desbanir: %s", errUnbanMembers.stream().limit(7)
          .map(m -> m.getUser().getName() + "#" + m.getUser().getDiscriminator()).collect(Collectors.joining(", "))))
          .queue();
    }
  }

  @Override
  public String getCategory() {
    return "Moderation";
  }

  @Override
  public String getTitle() {
    return "SoftBan Command";
  }

  @Override
  public String getName() {
    return "softban";
  }

  @Override
  public String getHelp() {
    return "Expulsa um usuário e deleta suas mensagens das últimas 24 horas.";
  }

  @Override
  public String getUsage() {
    return Config.get("PREFIX") + this.getName() + " <@menção> [razão]";
  }

  @Override
  public String getParameters() {
    return "`@menção` - O usuário a ser banido\n" + "`razão` - A razão para o softban.";
  }

}
