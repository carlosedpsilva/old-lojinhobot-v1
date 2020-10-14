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
import com.lojinho.bot.util.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UnbanCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();

        // Tratamento de permissões
        Member member = ctx.getMember();
        Member selfMember = ctx.getGuild().getSelfMember();
        if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("Você não possui permissões para utilizar este comando.").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("Não possuo permissões para desbanir usuários.").queue();
            return;
        }

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
        List<User> bannedUsers = new ArrayList<>();

        for (String arg : argsSections.get(0)) {
            User target = Utils.findBannedUser(ctx.getEvent(), ctx.getMember().getUser(), arg);
            if (target == null) {
                invalidMentions.add(arg);
            } else {
                bannedUsers.add(target);
            }
        }

        // Nenhum usuário encontrado
        if (bannedUsers.isEmpty()) {
            channel.sendMessage("Nenhum usuário banido encontrado.").queue();
            return;
        }

        String reason = "Não especificado.";
        String type = "Single";

        if (optionIndexes.containsKey("reason")) {
            reason = String.join(" ", argsSections.get(optionIndexes.get("reason")));
        }

        if (bannedUsers.size() > 1) {
            type = "Multiple";
        }

        EmbedBuilder eb;
        List<User> errUsers = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (User target : bannedUsers) {
            eb = new EmbedBuilder();
            eb.setAuthor("Unban log", null, target.getAvatarUrl()).setColor(Color.decode("#ff9c00"));
            eb.setDescription(
                    String.format("**User:** %#s\n**Moderator:** %#s\n**Type:** %s\n**Reason:** %s\n**Date:** `%s`",
                            target, member.getUser(), type, reason, dtf.format(LocalDateTime.now()).toString()));
            MessageEmbed log = eb.build();
            String auditLog = String.format("Ban by %#s with reason '%s'", member.getUser(), reason);
            ctx.getGuild().unban(target).reason(auditLog).queue((__) -> logChannel.sendMessage(log).queue(),
                    (error) -> errUsers.add(target));
        }

        if (errUsers.size() != bannedUsers.size()) {
            channel.sendMessage(String.format("Ação moderativa realizada com sucesso. %d usuário(s) desbanido(s).",
                    bannedUsers.size() - errUsers.size())).queue();
        }

        if (!errUsers.isEmpty()) {
            channel.sendMessage(String.format("Não foi possível desbanir: %s", errUsers.stream().limit(7)
                    .map(u -> u.getName() + "#" + u.getDiscriminator()).collect(Collectors.joining(", ")))).queue();
        }

    }

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public String getTitle() {
        return "Unban Command";
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getHelp() {

        return "Desexila um membro banido\n" + "Uso: " + Config.get("PREFIX") + this.getName() + "<user>";
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