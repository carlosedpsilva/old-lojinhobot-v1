package com.lojinho.bot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.lojinho.bot.command.commands.core.HelpCommand;
import com.lojinho.bot.command.commands.core.PingCommand;
import com.lojinho.bot.command.commands.info.AvatarCommand;
import com.lojinho.bot.command.commands.info.InviteCommand;
import com.lojinho.bot.command.commands.info.SupportCommand;
import com.lojinho.bot.command.commands.moderation.BanCommand;
import com.lojinho.bot.command.commands.moderation.DelmsgCommand;
import com.lojinho.bot.command.commands.moderation.KickCommand;
import com.lojinho.bot.command.commands.moderation.PrefixCommand;
import com.lojinho.bot.command.commands.moderation.SoftBanCommand;
import com.lojinho.bot.command.commands.moderation.UnbanCommand;
import com.lojinho.bot.command.commands.moderation.WarnCommand;
import com.lojinho.bot.command.commands.owner.ShutdownCommand;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Gerenciador de Comandos do Bot
 */
public class CommandManager {
  private final List<ICommand> commands = new ArrayList<>();

  public CommandManager() {
    // Core
    addCommand(new ShutdownCommand());
    addCommand(new HelpCommand(this));
    addCommand(new PingCommand());
    // Info
    addCommand(new AvatarCommand());
    addCommand(new InviteCommand());
    addCommand(new SupportCommand());
    // moderation
    addCommand(new PrefixCommand());
    addCommand(new DelmsgCommand());
    addCommand(new WarnCommand());
    addCommand(new KickCommand());
    addCommand(new SoftBanCommand());
    addCommand(new BanCommand());
    addCommand(new UnbanCommand());
  }

  private void addCommand(ICommand cmd) {
    boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

    if (nameFound) {
      throw new IllegalArgumentException("Já existe um comando com este nome");
    }

    commands.add(cmd);
  }

  /** Retorna uma lista de comandos */
  public List<ICommand> getCommands() {
    return commands;
  }

  /** Retorna um comando */
  @Nullable
  public ICommand getCommand(String search) {
    String searchLower = search.toLowerCase();

    for (ICommand cmd : this.commands) {
      if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
        return cmd;
      }
    }
    return null;
  }

  /** Retorna um HashMap de categorias */
  public LinkedHashMap<String, String> getCategories() {
    LinkedHashMap<String, String> categories = new LinkedHashMap<>();
    for (ICommand command : this.getCommands()) {
      categories.put(command.getName(), command.getCategory());
    }
    return categories;
  }

  /**
   * Faz o tratamento das mensagens recebidas e executa o comando se
   * correspondente
   */
  public void handle(GuildMessageReceivedEvent event, String prefix) {
    String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");

    if (event.getAuthor().isBot()) {
      return;
    }

    String invoke = split[0].toLowerCase();
    ICommand cmd = this.getCommand(invoke);

    if (cmd != null) {
      event.getChannel().sendTyping().queue();
      List<String> args = Arrays.asList(split).subList(1, split.length);

      CommandContext ctx = new CommandContext(event, args);

      cmd.handle(ctx);
    }
  }
}
