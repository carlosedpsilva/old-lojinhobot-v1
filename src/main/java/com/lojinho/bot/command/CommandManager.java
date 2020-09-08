package com.lojinho.bot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.lojinho.bot.command.commands.AvatarCommand;
import com.lojinho.bot.command.commands.PingCommand;
import com.lojinho.bot.command.commands.HelpCommand;

import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandManager {
  private final List<ICommand> commands = new ArrayList<>();

  public CommandManager() {
    addCommand(new PingCommand());
    addCommand(new AvatarCommand());
    addCommand(new HelpCommand());
  }

  private void addCommand(ICommand cmd) {
    boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

    if (nameFound) {
      throw new IllegalArgumentException("Já existe um comando com este nome");
    }

    commands.add(cmd);
  }

  @Nullable
  private ICommand getCommand(String search) {
    String searchLower = search.toLowerCase();

    for (ICommand cmd : this.commands) {
      if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
        return cmd;
      }
    }
    return null;
  }

  public void handle(GuildMessageReceivedEvent event) {
    String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
        .split("\\s+");

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
