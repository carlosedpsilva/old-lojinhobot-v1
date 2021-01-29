package com.lojinho.bot.command.commands.roles;

import java.util.List;

import com.lojinho.bot.command.meta.AbstractCommand;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.permission.UserRank;
import com.lojinho.bot.util.DisUtil;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class RoleCategoryCmd extends AbstractCommand {

  public RoleCategoryCmd() {
    super();
  }

  @Override
  public String getDescription() {
    return "Atribuir/Mostrar cargos e categorias de cargos deste servidor";
  }

  @Override
  public String getCommand() {
    return "rolecategory";
  }

  @Override
  public String[] getUsage() {
    return new String[] { "rolecategory                                     // mostra as categorias criadas",
        "rolecategory <category>                          // mostra informações sobre a categoria",
        "rolecategory create <key> [options]              // cria uma categoria",
        "rolecategory delete <key>                        // deleta a categoria com o identificador especificado",
        "rolecategory add <key> <role[,role,...]>         // adicionar cargos a uma categoria",
        "rolecategory remove <key> <role[,role,...]>      // remover cargos de uma categoria",
        "rolecategory edit <key> <options>                // modificar uma categoria", "",
        "Opções para Criação/Modificação de Categorias:",
        "[--key <keyname>]                                -  Renomeia o identificador de uma categoria",
        "[--name <name>]                                  -  Renomeia uma categoria",
        "[--type <default|onjoin|inteface|selfassign>]    -  Altera o tipo da categoria",
        "[--usage <command|reaction|both>]                -  Altera o modo de uso da categoria",
        "[--assign <stack|switch>]                        -  Altera o modo de atribuição da categoria", };
  }

  @Override
  public String[] getAliases() {
    return new String[] { "rc", };
  }

  @Override
  public String execute(LojinhoBot bot, List<String> args, MessageChannel channel, Message message) {
    Guild guild = ((TextChannel) channel).getGuild(); // this guild?
    UserRank rank = bot.security.getUserRankForGuild(message.getAuthor(), guild);

    // bot_admin ? is managing another guild?
    if (rank.isAtLeast(UserRank.BOT_ADMIN) && args.size() >= 1) {
      Guild foundGuild = DisUtil.findGuild(guild, args.get(0));
      if (foundGuild != null) {
        args.remove(0); // remove guild mention for default args
        guild = foundGuild;
      }
    }

    // [ loj.rc ] mostra as categorias criadas
    if (args.size() == 0 || args.get(0) == "page") {

      // Send Embed
      if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
          Permission.MESSAGE_EMBED_LINKS)) {
        // build embed
      }

      // Send Message
      // build message

    }

    switch (args.get(0)) {
      // [ loj.rc create ]
      case "createcategory":
      case "create":
      case "c":

        if (args.size() < 2)
          return ""; // insufficient number of arguments

        break;
      // [ loj.rc delete ]
      case "delcategory":
      case "delete":
      case "delcat":
      case "del":
      case "d":

        if (args.size() < 2)
          return ""; // insufficient number of arguments

        break;

      // [ loj.rc add ]
      case "addrole":
      case "add":

        if (args.size() < 3)
          return ""; // insufficient number of arguments

        break;

      // [ loj.rc remove ]
      case "remrole":
      case "remove":
      case "rem":

        if (args.size() < 3)
          return ""; // insufficient number of arguments

        break;

      // [ loj.rc edit ] modificar uma categoria
      case "modify":
      case "edit":
      case "e":

        if (args.size() < 4)
          return ""; // insufficient number of arguments

        break;

      default:
        // [ loj.rc <category> ] mostra informações sobre a categoria
        // Send Embed
        if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_EMBED_LINKS)) {
          // build embed
        }

        // Send Message
        // build message

        break;
    }

    return "";
  }

}
