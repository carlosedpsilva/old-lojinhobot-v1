package com.lojinho.bot.command.commands.moderation;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class DelmsgCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Member selfMember = ctx.getGuild().getSelfMember();
        

        if(!member.hasPermission(Permission.MESSAGE_MANAGE)){
            channel.sendMessage("Você precisa da permissão para alterar as mensagens").queue();
            
            return;
        }

        if(!selfMember.hasPermission(Permission.MESSAGE_MANAGE)){
            channel.sendMessage("Eu preciso da permissão para alterar as mensagens").queue();

            return;
        }

        if(ctx.getArgs().isEmpty()) {
            channel.sendMessage("uso correto é: " + Config.get("PREFIX") + getName() + "<quantidade>").queue();

            return;
        }

        int quant;
        String arg = ctx.getArgs().get(0);

        try {
            quant = Integer.parseInt(arg);
        } catch(NumberFormatException ignored){
            channel.sendMessageFormat("´%s´ não é um numero valido", arg).queue();

            return;
        }

        if(quant < 2 || quant > 100) {
            channel.sendMessage(" a quantidade tem que ser entre 2 e 100").queue();

            return;
        }

        channel.getIterableHistory()
            .takeAsync(quant+1)
            .thenApplyAsync((messages) -> {
                List<Message> goodMessages = messages.stream()
                        .filter((m) -> m.getTimeCreated().isBefore(
                                OffsetDateTime.now().plus(2, ChronoUnit.WEEKS)
                        ))
                        .collect(Collectors.toList());

                channel.purgeMessages(goodMessages);

                return goodMessages.size();
            })
            .whenCompleteAsync(
                (count, thr) -> channel.sendMessageFormat("Apagado `%d` messages", count-1).queue(
                    (message) -> message.delete().queueAfter(10, TimeUnit.SECONDS)
                )
            )
            .exceptionally((thr) -> {
                String cause = "";

            if (thr.getCause() != null) {
                cause = " caused by: " + thr.getCause().getMessage();
            }

            channel.sendMessageFormat("Error: %s%s", thr.getMessage(), cause).queue();

            return 0;
        });
        
    }
    @Override
    public String getName() {
        return "delmsg";
    }
    @Override
    public String getHelp() {
        return "deleta o numero de mensagens solcitado, numa faixa de 2 a 100 mensagens \n"  + "Uso: " + Config.get("PREFIX") + getName() + "<quantidade>";
    }
    
}
