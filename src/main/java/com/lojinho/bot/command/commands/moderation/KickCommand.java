package com.lojinho.bot.command.commands.moderation;

import java.util.List;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class KickCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Message message = ctx.getMessage();
        final Member member = ctx.getMember();
        final List<String> args = ctx.getArgs();

        if (args.size() < 2 || message.getMentionedMembers().isEmpty()) {
            channel.sendMessage("está faltando argumentos").queue();
            return;
        }

        final Member target = message.getMentionedMembers().get(0);

        if (!member.canInteract(target) || !member.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("Você não tem permissão para kickar alguem >:(").queue();
            return;
        }

        final Member selfMember = ctx.getSelfMember();

        if (!selfMember.canInteract(target) || !selfMember.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("eu não tenho permissão para kickar esse usuario").queue();
            return;
        }

        final String reason = String.join(" ", args.subList(1, args.size()));

        ctx.getGuild().kick(target, reason).reason(reason).queue(
                (__) -> channel.sendMessage("o usuario foi kickado").queue(),
                (error) -> channel.sendMessageFormat("não foi possivel kickar %s", error.getMessage()).queue());

    }

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public String getTitle() {
        return "Kick Command";
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getHelp() {
        return "Expulsa um usuario do servidor.";
    }

    @Override
    public String getUsage() {
        return Config.get("PREFIX") + this.getName() + " <@menção> [razão]";
    }

    @Override
    public String getParameters() {
        return "`@menção` - O usuário a ser banido\n" + "`razão` - A razão para a expulsão.";
    }
}
