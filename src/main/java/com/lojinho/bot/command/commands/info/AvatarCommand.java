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
			Member member;
			try {
				member = ctx.getMessage().getMentionedMembers().get(0);

				final String avatar = member.getUser().getAvatarUrl() + ("?size=1024");
				final EmbedBuilder eb = new EmbedBuilder();

				eb.setImage(avatar).setColor(Color.orange);
				ctx.getChannel().sendMessage(eb.build()).queue();
			} catch (final IndexOutOfBoundsException ex) {
				final EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Usuário não encontrado").setDescription("Você deve mencionar um usuário.")
						.setColor(new Color(16750336));
				ctx.getChannel().sendMessage(eb.build()).queue();
			}

		}
	}

	@Override
	public String getCategory() {
		return "Info";
	}

	@Override
	public String getTitle() {
		return "Avatar Command";
	}

	@Override
	public String getName() {
		return "avatar";
	}

	@Override
	public String getHelp() {
		return "Exibe o avatar de um usuário.";
	}

	@Override
	public String getUsage() {
		return Config.get("PREFIX") + this.getName() + " [@menção]";
	}

	@Override
	public String getParameters() {
		return "`@menção` - O usuário que você deseja verificar o avatar. Deve conter uma <@!menção>.";
	}

}
