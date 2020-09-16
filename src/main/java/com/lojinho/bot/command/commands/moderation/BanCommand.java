package com.lojinho.bot.command.commands.moderation;

import java.util.List;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class BanCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Member selfMember = ctx.getGuild().getSelfMember();
        List<Member> mentionedMembers = ctx.getMessage().getMentionedMembers();
        final List<String> args = ctx.getArgs();

        if (mentionedMembers.isEmpty() || args.size() < 2) {
            channel.sendMessage("Está Faltando argumentos ").queue();
            return;
        }

        Member target = mentionedMembers.get(0);
        String reason = String.join(" ", args.subList(1, args.size()));

        if (!member.hasPermission(Permission.BAN_MEMBERS) || !member.canInteract(target)) {
            channel.sendMessage("Você não tem permissão para banir alguem >:(").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.BAN_MEMBERS) || !selfMember.canInteract(target)) {
            channel.sendMessage("Eu não posso banir este usuario ou eu não tenho a permissão de banir usuarios").queue();
            return;
        }

        ctx.getGuild().ban(target, 1)
                .reason(String.format("Ban by: %#s, with reason: %s", ctx.getAuthor(), reason)).queue();

        channel.sendMessage("Success!").queue();

    }
    
    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getHelp() {
        
        return "bane o usuario do servidor\n" +
        "Uso: " + Config.get("PREFIX") + getName() + "<user> <razão>";
    }
    
}
