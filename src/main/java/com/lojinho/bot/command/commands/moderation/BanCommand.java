package com.lojinho.bot.command.commands.moderation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class BanCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();

        // Tratamento dos opções
        LinkedHashMap<String, Integer> optionIndexes = new LinkedHashMap<String, Integer>();
        ArrayList<List<String>> argsSections = new ArrayList<List<String>>();

        {
            int i = 0;
            List<String> listOfArgs = null;
            for (String arg : args) {
                listOfArgs = new ArrayList<String>();
                if (arg.startsWith("--")) {
                    optionIndexes.put(arg.replaceFirst("(?i)" + Pattern.quote("--"), ""), i);
                    argsSections.add(listOfArgs);
                    i++;
                } else {
                    listOfArgs.add(arg);
                }
            }
            argsSections.add(listOfArgs);
        }

        // Default parameters missing
        if (optionIndexes.containsValue(0)) {
            return;
        }

        // Tratamento de menções
        List<Member> members = ctx.getGuild().getMembers();
        List<Member> mentionedMembers = new ArrayList<Member>();
        int cont = 0;

        for (String arg : argsSections.get(0)) {
            int i = 0;
            for (Member target : members) {
                if (arg.equals(String.valueOf(target.getUser().getAsMention()))
                        || arg.equals(String.valueOf(target.getUser().getIdLong()))
                        || arg.equals(target.getUser().getAsTag()) || arg.equals(target.getUser().getName())) {
                    if (++i > 1) {
                        channel.sendMessage(String.format(
                                "Muitos membros encontrados com '%s', refine sua busca (ex. usando name#discriminator)",
                                arg)).queue();
                    } else {
                        mentionedMembers.add(target);
                        cont++;
                    }
                }
            }
        }

        // Tratamento de permissões
        Member member = ctx.getMember();
        Member selfMember = ctx.getGuild().getSelfMember();

        for (Member target : mentionedMembers) {
            if (!member.hasPermission(Permission.BAN_MEMBERS) || !member.canInteract(target)) {
                channel.sendMessage("Você não possui permissão para utilizar este comando.").queue();
                return;
            }

            if (!selfMember.hasPermission(Permission.BAN_MEMBERS) || !selfMember.canInteract(target)) {
                channel.sendMessage("Não possuo permissões para banir ou não posso banir este usuário.").queue();
                return;
            }
        }

        // Ban Process

        String reason = null;

        String type = "Single";

        if (optionIndexes.containsKey("reason")) {
            reason = String.join(" ", argsSections.get(optionIndexes.get("reason")));
        }

        if (mentionedMembers.size() > 1) {
            type = "Multiple";
        }

        for (Member target : mentionedMembers) {
            ctx.getGuild().ban(target, 1)
                    .reason(String.format("Ban type: %s, by: %#s, with reason: %s", type, ctx.getAuthor(), reason))
                    .queue();
        }

        channel.sendMessage(String.format("Adm Hammer swinged successufuly. %d usuário(s) banido(s)", cont)).queue();

    }

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public String getTitle() {
        return "Ban Command";
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getHelp() {
        return "Exile o membro indesejado 😡";
    }

    @Override
    public String getUsage() {
        return Config.get("PREFIX") + this.getName() + " <user> [razão]";
    }

    @Override
    public String getParameters() {
        return "`@menção` - O usuário a ser banido\n" + "`razão` - A razão para o banimento.";
    }

}
