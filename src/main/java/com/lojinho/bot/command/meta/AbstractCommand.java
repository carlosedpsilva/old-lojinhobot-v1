package com.lojinho.bot.command.meta;

import java.util.List;

import com.lojinho.bot.main.LojinhoBot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public abstract class AbstractCommand {

  private CommandCategory commandCategory = CommandCategory.UNKNOWN;

  public AbstractCommand() {
  }

  /**
   * Uma descrição curta do método
   *
   * @return description
   */
  public abstract String getDescription();

  /**
   * O que deve ser digitado para executar o comando (sem o prefixo)
   *
   * @return command
   */
  public abstract String getCommand();

  /**
   * Como utilizar o comando?
   *
   * @return command usage
   */
  public abstract String[] getUsage();

  /**
   * Aliases para executar o comando
   *
   * @return array of aliases
   */
  public abstract String[] getAliases();

  /**
   * Categoria do comando
   * 
   * @return category
   */
  public final CommandCategory getCommandCategory() {
    return commandCategory;
  }

  /**
   * O comando será alterado para a categoria que corresponder à última parte do
   * nome do pacote
   *
   * @param newCategory category of the command
   */
  public void setCommandCategory(CommandCategory newCategory) {
    commandCategory = newCategory;
  }

  /**
   * Onde o comando pode ser utilizado?
   *
   * @return private, public, both
   */
  public CommandVisibility getVisibility() {
    return CommandVisibility.BOTH;
  }

  /**
   * Comando habilitado? Por padrão sim. Configuração global.
   *
   * @return command is enabled?
   */
  public boolean isEnabled() {
    return true;
  }

  /**
   * Se o comando pode ser adicionado à blacklist das guilds
   *
   * @return can be blacklisted?
   */
  public boolean canBeDisabled() {
    return true;
  }

  /**
   * Se o comando é apresentado na lista de comandos
   *
   * @return shows up in the !help list?
   */
  public boolean isListed() {
    return true;
  }

  /**
   * @param bot
   * @param args
   * @param channel
   * @param message
   * @return
   */
  public abstract String execute(LojinhoBot bot, List<String> args, MessageChannel channel, Message message);
}
