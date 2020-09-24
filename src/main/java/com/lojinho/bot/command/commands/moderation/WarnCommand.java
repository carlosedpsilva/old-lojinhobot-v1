package com.lojinho.bot.command.commands.moderation;

import java.awt.Color;
import java.util.List;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class WarnCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {

        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Member selfMember = ctx.getGuild().getSelfMember();
        List<Member> mentionedMembers = ctx.getMessage().getMentionedMembers();
        TextChannel logChannel = ctx.getGuild().getTextChannelById(756186559832653955L);
        final List<String> args = ctx.getArgs();

        if (mentionedMembers.isEmpty()) {
            channel.sendMessage("Está Faltando argumentos ").queue();
            return;
        }
        Member target = mentionedMembers.get(0);
        String reason = String.join(" ", args.subList(1, args.size()));

        if (!member.canInteract(target)) {
            channel.sendMessage("Você não tem permissão para dar warn neste usuario ").queue();
            return;
        }

        if (!selfMember.canInteract(target)) {
            channel.sendMessage("Eu não posso dar warn este usuario").queue();
            return;
        }
        channel.sendMessage("Warn enviado!\n log criado no canal " + logChannel.getAsMention()).queue();

        // logChannel.sendMessage("Warn enviado para: " + target.getAsMention() + "\n
        // Conteudo: "+ reason ).queue();
        EmbedBuilder log = new EmbedBuilder();
        log.setTitle("Warn enviado").setColor(Color.YELLOW)
                .addField("Usuario: ", "\t" + target.getUser().getAsMention(), false)
                .addField("banido por: ", "\t" + ctx.getAuthor().getAsMention(), false)
                .addField("Motivo: ", "\t" + reason, false);
        logChannel.sendMessage(log.build()).queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Voce recebeu um aviso:").setColor(Color.orange).setDescription(reason).setImage(
                "https://scontent-atl3-1.cdninstagram.com/v/t51.2885-15/e35/c10.0.300.300a/117671453_736111576978517_4063377491529020764_n.jpg?_nc_ht=scontent-atl3-1.cdninstagram.com&_nc_cat=105&_nc_ohc=FZjZ3LJeKWAAX_gJCPz&oh=02ab01882c97d8c9ab6386e0c27e1b5a&oe=5F67DD61");
        sendPrivateMessage(target.getUser(), eb.build());

    }

    public void sendPrivateMessage(User user, MessageEmbed content) {
        user.openPrivateChannel().queue((Channel) -> {

            Channel.sendMessage(content).queue();
        });
    }

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public String getTitle() {
        return "Warn Command";
    }

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public String getHelp() {
        return "envia uma mensagem de aviso ao usuario marcado" + "Uso: " + Config.get("PREFIX") + this.getName()
                + "<user>";
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
