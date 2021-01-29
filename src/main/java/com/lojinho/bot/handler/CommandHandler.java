package com.lojinho.bot.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.lojinho.bot.command.meta.AbstractCommand;
import com.lojinho.bot.command.meta.CommandCategory;
import com.lojinho.bot.command.meta.CommandVisibility;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.controllers.CUser;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.util.DisUtil;

import org.reflections.Reflections;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * Gerenciador de Comandos do Bot
 */
public class CommandHandler {
  public final static String ALL_COMMANDS = "all-commands";
  private static final HashMap<String, AbstractCommand> commands = new HashMap<>();
  private static final HashMap<String, AbstractCommand> commandsAlias = new HashMap<>();

  public static void initilize() {
    loadCommands();
    loadAliases();
  }

  /**
   * Carrega os comandos
   */
  public static void loadCommands() {
    Reflections reflections = new Reflections("com.lojinho.bot.command");
    Set<Class<? extends AbstractCommand>> classes = reflections.getSubTypesOf(AbstractCommand.class);
    for (Class<? extends AbstractCommand> s : classes) {
      try {
        if (Modifier.isAbstract(s.getModifiers())) {
          continue;
        }
        String packageName = s.getPackage().getName();
        AbstractCommand c = s.getConstructor().newInstance();
        c.setCommandCategory(CommandCategory.fromPackage(packageName.substring(packageName.lastIndexOf(".") + 1)));
        if (!c.isEnabled()) {
          continue;
        }
        if (!isCommandCategoryEnabled(c.getCommandCategory())) {
          continue;
        }
        if (!commands.containsKey(c.getCommand())) {
          commands.put(c.getCommand(), c);
        }

      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Carrega os aliases para os comandos
   */
  public static void loadAliases() {
    for (AbstractCommand command : commands.values()) {
      for (String alias : command.getAliases()) {
        if (!commandsAlias.containsKey(alias)) {
          commandsAlias.put(alias, command);
        } else {
          LojinhoBot.LOGGER.warn(String.format("Alias duplicado encontrado! Os comandos '%s' e '%s' usam o alias '%s'",
              command.getCommand(), commandsAlias.get(alias), alias));
        }
      }
    }
  }

  public static void process(LojinhoBot bot, MessageChannel channel, User author, Message incomingMessage) {

    String outMsg = "";
    boolean commandSuccess = true;
    boolean startedWithMention = false;
    int guildId = 0;
    String inputMessage = incomingMessage.getContentRaw();
    String commandUsed = "-";

    if (inputMessage.startsWith(bot.mentionMe)) {
      inputMessage.replaceFirst(bot.mentionMe, "").trim();
      startedWithMention = true;
    } else if (inputMessage.startsWith(bot.mentionMeAlias)) {
      inputMessage.replaceFirst(bot.mentionMeAlias, "").trim();
      startedWithMention = true;
    }

    if (channel instanceof TextChannel) {
      if (!((TextChannel) channel).canTalk()) {
        return;
      }
    }

    guildId = CGuild.getCachedId((((TextChannel) channel).getGuild().getIdLong()));

    // separar o comando dos args // (?:([^\s\"]+)|\"((?:\w+|\\\"|[^\"])+)")
    String[] input = null;
    if (startedWithMention)
      input = DisUtil.filterMention(inputMessage).toLowerCase().split("\\s+", 2);
    else
      input = DisUtil.filterPrefix(inputMessage, ((TextChannel) channel)).toLowerCase().split("\\s+", 2);
    List<String> args = new ArrayList<>();
    if (input.length == 2) {
      Collections.addAll(args, input[1].split(" +"));
    }

    if (commands.containsKey(input[0]) || commandsAlias.containsKey(input[0])) {
      AbstractCommand command = commands.containsKey(input[0]) ? commands.get(input[0]) : commandsAlias.get(input[0]);
      commandUsed = command.getCommand();

      long cooldown = 0;

      if (command.canBeDisabled() && isDisabled(guildId, channel.getIdLong(), command.getCommand())) {
        commandSuccess = false;
        // show unknown commands config? (blacklisted msg)
      } else if (cooldown > 0) {
        // still in cooldown
      } else if (!hasRightVisibility(channel, command.getVisibility())) {
        if (channel instanceof PrivateChannel) {
          // not for private
        } else {
          // not for public
        }
      } else {
        String commandOutput = command.execute(bot, args, channel, incomingMessage);
        if (!commandOutput.isEmpty()) {
          outMsg = commandOutput;
        }
        // command log?
      }
    } // else if (show unknown commands config?)
    if (!outMsg.isEmpty()) {
      channel.sendMessage(outMsg).queue();
      ;
    }
    if (commandSuccess) {
      if (channel instanceof TextChannel) {
        // commandlog channel
      }
      LojinhoBot.LOGGER
          .info(String.format("Comando executado em um canal público | user: %s | comando executado: %s | outMsg: %s",
              author.getName(), commandUsed, outMsg));
    } else {
      LojinhoBot.LOGGER
          .info(String.format("Comando executado em um canal privado | user: %s | comando executado: %s | outMsg: %s",
              author.getName(), commandUsed, outMsg));
    }
    CUser.registerCommandUse(CUser.getCachedId(author.getIdLong()));
  }

  public static boolean isCommand(TextChannel channel, String msg, String mentionMe, String mentionMeAlias) {
    return msg.startsWith(DisUtil.getCommandPrefix(channel)) || msg.startsWith(mentionMe)
        || msg.startsWith(mentionMeAlias);
  }

  private static boolean hasRightVisibility(MessageChannel channel, CommandVisibility visibility) {
    if (channel instanceof PrivateChannel) {
      return visibility.isForPrivate();
    }
    return visibility.isForPublic();
  }

  private static boolean isDisabled(int guildId, long channelId, String commandName) {
    if (guildId == 0) {
      return false;
    }

    // blacklisted?

    return false;
  }

  /**
   * Verifica se a categoria de comando está habilitada
   *
   * @param category a catetegoria a ser checada
   * @return enabled?
   */
  public static boolean isCommandCategoryEnabled(CommandCategory category) {
    // Implementar configuração de habilitar/desabilitar categorias em BotConfig
    switch (category) {
      default:
        return true;
    }
  }
}
