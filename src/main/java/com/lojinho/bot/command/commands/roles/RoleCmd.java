package com.lojinho.bot.command.commands.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lojinho.bot.command.meta.AbstractCommand;
import com.lojinho.bot.command.meta.CommandReactionListener;
import com.lojinho.bot.command.meta.CommandVisibility;
import com.lojinho.bot.command.meta.ICommandReactionListener;
import com.lojinho.bot.command.meta.PaginationInfo;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.permission.UserRank;
import com.lojinho.bot.util.DisUtil;
import com.lojinho.bot.util.Misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class RoleCmd extends AbstractCommand implements ICommandReactionListener<PaginationInfo<?>> {
  int PERM_PER_PAGE = 6;

  public RoleCmd() {
    super();
  }

  @Override
  public String getDescription() {
    return "Mostrar/modificar cargos do servidor";
  }

  @Override
  public String getCommand() {
    return "role";
  }

  @Override
  public String[] getUsage() {
    return new String[] { "role                                       // mostra os cargos auto-atribuíveis do servidor",
        "role <role|category|user>                  // mostra informações sobre o cargo/categoria, ou os cargos de um usuário",
        "role assign <options>                      // atribuir cargo/categoria",
        "role deassign <options>                    // desatribuir cargo/categoria",
        "role create <role> <options>               // criar um cargo",
        "role edit <role> <options>                 // modificar um cargo",
        "role delete <role>                         // deletar um cargo",
        "role permissions [role]                    // mostra as permissões de um ou todos os cargos", "",
        "Modificadores de Atrinuição/Desatribuição:",
        "[--role <role[,role,...]>]                 -  Cargo(s) a serem atribuídos/desatribuídos",
        "[--category <category[,category,...]>]     -  Categoria(s) a serem atribuídas/desatribuídas",
        "[--bots]                                   -  Comando apenas afetará bots (todos)",
        "[--users]                                  -  Comando apenas afetará usuários (todos)",
        "[--user <user[,user,...]>]                 -  Comando apenas afetará usuários especificados",
        "[--inrole <role[,role,...]>]               -  Comando apenas afetará alvos com o(s) cargo(s) especificado(s)",
        "[--notinrole <role[,role,...]>]            -  Comando apenas afetará alvos sem o(s) cargo(s) especificado(s)",
        "[--noroles]                                -  Comando apenas afetará alvos sem nenhum cargo", "",
        "Modificadores de Modificação de Cargo:", "[--name]                                   -  Renomeia um cargo",
        "[--color <color>]                          -  Muda a cor de um cargo",
        "[--addperm <perm[,perm,...]>]              -  Adiciona as permissões especificadas ao cargo",
        "[--resetperm <perm[,perm,...]>]            -  Redefine as permissões especificadas do cargo",
        "[--revogueperm <perm[,perm,...]>]          -  Revoga as permissões especificadas do cargo", "", "Observação:",
        "Este comando não irá te permitir auto-atribuir ou modificar cargos que possuem nível de permissão igual ou superior ao seu, ou do LojinhoBot", };
  }

  @Override
  public String[] getAliases() {
    return new String[] { "cargo", "r" };
  }

  @Override
  public CommandVisibility getVisibility() {
    return CommandVisibility.PUBLIC;
  }

  @Override
  public String execute(LojinhoBot bot, List<String> args, MessageChannel channel, Message message) {
    Guild guild = ((TextChannel) channel).getGuild(); // this guild?
    Member target = message.getMember(); // self or other if aplicable
    UserRank rank = bot.security.getUserRankForGuild(message.getAuthor(), ((TextChannel) channel).getGuild());

    // bot_admin ? is managing another guild?
    if (rank.isAtLeast(UserRank.BOT_ADMIN) && args.size() >= 1) {
      Guild foundGuild = DisUtil.findGuild(((TextChannel) channel), args.get(0));
      if (foundGuild != null) {
        args.remove(0); // remove guild mention for default args
        guild = foundGuild;
      }
    }

    // guild_admin ? is mentioning another user?
    if ((rank.isAtLeast(UserRank.GUILD_ADMIN) || target.hasPermission(Permission.MANAGE_ROLES)) && args.size() > 1
        && DisUtil.isUserMention(args.get(0))) {
      Member foundMember = DisUtil.findMember(((TextChannel) channel), args.get(0));
      if (foundMember != null) {
        args.remove(0);
        target = foundMember;
      }
    }

    // [ loj.role permissions ] // mostrar permissões
    if (args.size() > 0 && args.get(0).equalsIgnoreCase("permissions")) {

      boolean showAllPerms = false;

      // permissions for role ?
      Role role = null;
      if (args.size() > 1) {
        role = DisUtil.findRole(guild, args.get(1));
      }

      if (role != null) {
        // show permissions for role
        // Send Embed
        if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_EMBED_LINKS)) {
          // build embed
        }

        // Send Message
        // build message
      }

      if (target.hasPermission(Permission.MANAGE_ROLES))
        showAllPerms = true;

      // show discord permissions
      // Send Embed
      if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
          Permission.MESSAGE_EMBED_LINKS)) {
        // build embed
      }

      // Send Message
      // build message
    }

    // [ loj.role ] Mostrar cargos autoatribuíveis da guilda
    if (args.size() == 0 || args.get(0).equals("page")) {
      // Role[] selfAssignRoles =
      // GRoleCategories.get(guild).getRolesBy(RoleCategoryType.SELF_ASSIGN);

      // GRoleCategories | .get(guild)
      // getCategoryBy(arg) // name, id ou name#id
      // getSettingsFor(guild, ORoleCategory)
      // getRolesBy(arg) // ORoleCategory, RoleCategoryType

      // ORoleCategory.getId
      // ORoleCategory.getType
      // ORoleCategory.getGuild
      // ORoleCategory.getName

      // show discord permissions
      // Send Embed
      if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
          Permission.MESSAGE_EMBED_LINKS)) {
        // build embed
      }

      // Send Message
      // build message
      return "";
    }

    // [ loj.role <arg> ]
    switch (args.get(0).toLowerCase()) {
      // atribuir cargo/categoria
      // role assign <role|category> <options>
      case "assign":
      case "join":
      case "self":
      case "add":
      case "me":
      case "a":
      case "+": {

        if (args.size() < 2)
          return ""; // insufficient number of arguments

        break;
      }
      // desatribuir cargo/categoria
      // role deassign <role|category> <options>
      case "deassign":
      case "remove":
      case "leave":
      case "not":
      case "da":
      case "-": {

        if (args.size() < 2)
          return ""; // insufficient number of arguments

        return "";
      }
      // criar um cargo
      // role create <role> <options>
      case "newrole":
      case "create":
      case "new":
      case "c":

        if (!target.equals(message.getMember()))
          return ""; // not applicable for mention

        if (!target.hasPermission(Permission.MANAGE_ROLES))
          return "Você não possui permissões para utilizar este comando.";

        // new role | tratamento de opções
        return "";

      // modificar um cargo
      // role edit <role> <options>
      case "modify":
      case "edit":
      case "e":

        if (!target.equals(message.getMember()))
          return ""; // not applicable for mention

        if (!target.hasPermission(Permission.MANAGE_ROLES))
          return "Você não possui permissões para utilizar este comando.";

        // modify role | tratamento de opções
        break;

      // deletar um cargo
      // role delete <role>
      case "delrole":
      case "delete":
      case "del":
      case "d":

        if (!target.equals(message.getMember()))
          return ""; // not applicable for mention

        if (!target.hasPermission(Permission.MANAGE_ROLES))
          return "Você não possui permissões para utilizar este comando.";

        // delete role | tratamento de opções
        break;

      default:
        // [ loj.role <role> ] mostra informações sobre o cargo
        // Send Embed
        if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_EMBED_LINKS)) {
          // build embed
        }

        // Send Message
        // build message

        // ###########################

        // [ loj.role <category> ] mostra informações sobre o cargo
        // Send Embed
        if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_EMBED_LINKS)) {
          // build embed
        }

        // Send Message
        // build message

        // ###########################

        // [ loj.role <user> ] mostra os cargos de um usuário
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

  @Override
  public CommandReactionListener<PaginationInfo<?>> getReactionListener(User user, PaginationInfo<?> initialData) {
    return null;
  }

}
