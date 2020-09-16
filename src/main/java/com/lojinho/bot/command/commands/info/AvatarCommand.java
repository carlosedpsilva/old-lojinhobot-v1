package com.lojinho.bot.command.commands.info;

import java.awt.Color;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class AvatarCommand implements ICommand {

	@Override
	public void handle(final CommandContext ctx) {
		final String[] message = ctx.getMessage().getContentRaw().split(" ");
		if (message.length == 1) {
			final String avatar = ctx.getAuthor().getAvatarUrl() + ("?size=1024");
			final EmbedBuilder eb = new EmbedBuilder();
			eb.setImage(avatar).setColor(new Color(16750336));
			ctx.getChannel().sendMessage(eb.build()).queue();
		} else if (message.length == 2) {
			Member name;
			try {

				name = ctx.getMessage().getMentionedMembers().get(0);
				final String avatar = name.getUser().getAvatarUrl() + ("?size=1024");
				final EmbedBuilder eb = new EmbedBuilder();
				eb.setImage(avatar).setColor(Color.orange);
				ctx.getChannel().sendMessage(eb.build()).queue();
			} catch (final IndexOutOfBoundsException ex) {
				System.out.println("Exception ocorreu");
				final EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("erro").setDescription("voce deve colocar o nome como marcação").setColor(new Color(16750336));
				ctx.getChannel().sendMessage(eb.build()).queue();
			}

		}
	}

	@Override
	public String getName() {
		return "avatar";
	}

	@Override
	public String getHelp() {
		return "Retorna o avatar de perfil\n" + "Uso: " + Config.get("PREFIX") + "avatar [@menção]";
	}

}
