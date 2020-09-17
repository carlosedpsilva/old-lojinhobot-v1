package com.lojinho.bot.command.commands.moderation;

import java.util.List;
import java.util.stream.Collectors;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UnbanCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();


        if (!ctx.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("Você precisa da permissão de ban para poder usar esse comando").queue();
            return;
        }

        if (!ctx.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("Eu não tenho permissão para banir").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage("uso: `" + Config.get("PREFIX") + getName() + " <username/user id/username#disc>`").queue();
            return;
        }

        String argsJoined = String.join(" ", args);
        ctx.getGuild().retrieveBanList().queue((bans) -> {
            List<User> goodUsers = bans.stream().filter((ban) ->  isCorrectUser(ban, argsJoined))
                    .map(Guild.Ban::getUser).collect(Collectors.toList());

            if (goodUsers.isEmpty()) {
                channel.sendMessage("este usuario não está banido").queue();
                return;
            }

            User target = goodUsers.get(0);

            String mod = String.format("%#s", ctx.getAuthor());
            String bannedUser = String.format("%#s", target);

            ctx.getGuild().unban(target)
                    .reason("desbanido By " + mod).queue();

            channel.sendMessage("usuario " + bannedUser + " desbanido.").queue();

        });
		
    }

    @Override
	public String getName() {
		return "unban";
    }
    
    @Override
	public String getHelp() {
		
		return "Desexila um membro banido\n" + "Uso: " + Config.get("PREFIX") + this.getName() + "<user>";
	}

	private boolean isCorrectUser(Guild.Ban ban, String arg) {
	    User bannedUser = ban.getUser();

	    return bannedUser.getName().equalsIgnoreCase((arg)) || bannedUser.getId().equalsIgnoreCase(arg)
                || String.format("%#s", bannedUser).equalsIgnoreCase(arg);
    }
    
}