package com.lojinho.bot.command.commands.moderation;

import java.util.List;

import com.lojinho.bot.command.CommandContext;
import com.lojinho.bot.command.ICommand;
import com.lojinho.bot.data.Config;
import com.lojinho.bot.db.DatabaseManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PrefixCommand implements ICommand {

  @Override
  public void handle(CommandContext ctx) {
    final TextChannel channel = ctx.getChannel();
    final List<String> args = ctx.getArgs();
    final Member member = ctx.getMember();
    final long guildId = ctx.getGuild().getIdLong();

    // Tratar permissões
    if (!member.hasPermission(Permission.MANAGE_CHANNEL)) {
      channel.sendMessage("Você deve possuir a permissão Gerenciar Canais para usar este comando").queue();
      return;
    }

    if (args.isEmpty()) {
      channel.sendMessage("Prefix: " + DatabaseManager.INSTANCE.getPrefix(guildId)).queue();
      return;
    }

    final String newPrefix = String.join("", args);
    this.updatePrefix(guildId, newPrefix);
    channel.sendMessage("Prefixo alterado com sucesso!").queue();
  }

  @Override
  public String getCategory() {
    return "Moderation";
  }

  @Override
  public String getTitle() {
    return "Prefix Command";
  }

  @Override
  public String getName() {
    return "prefix";
  }

  @Override
  public String getHelp() {
    return "Mostra ou altera o prefixo utilizado neste servidor.";
  }

  @Override
  public String getUsage() {
    return Config.get("PREFIX") + this.getName() + "|prefixo|";
  }

  @Override
  public String getParameters() {
    return "`prefixo` - O novo prefixo a ser definido para este servidor.";
  }

  private void updatePrefix(long guildId, String newPrefix) {
    DatabaseManager.INSTANCE.setPrefix(guildId, newPrefix);
  }

}
