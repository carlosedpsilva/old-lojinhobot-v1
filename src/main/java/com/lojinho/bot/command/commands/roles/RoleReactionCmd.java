package com.lojinho.bot.command.commands.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.lojinho.bot.command.meta.AbstractCommand;
import com.lojinho.bot.command.meta.CommandReactionListener;
import com.lojinho.bot.command.meta.ICommandReactionListener;
import com.lojinho.bot.command.meta.PaginationInfo;
import com.lojinho.bot.db.controllers.role.CRoleCategory;
import com.lojinho.bot.db.controllers.role.CRoleReaction;
import com.lojinho.bot.db.model.role.OGuildRole;
import com.lojinho.bot.db.model.role.ORoleCategory;
import com.lojinho.bot.db.model.role.ORoleReactionKey;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.permission.UserRank;
import com.lojinho.bot.roles.RoleCategoryType;
import com.lojinho.bot.util.DisUtil;
import com.lojinho.bot.util.Emojibet;
import com.lojinho.bot.util.Misc;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class RoleReactionCmd extends AbstractCommand implements ICommandReactionListener<PaginationInfo<?>> {

  @Override
  public String getDescription() {
    return "Gerenciar/mostrar as mensagens de reação de atribuição do servidor";
  }

  @Override
  public String getCommand() {
    return "rolereaction";
  }

  @Override
  public String[] getUsage() {
    return new String[] { "",
        "rolereaction                               // mostra mensagens com reações de atribuição",
        "rolereaction <key>                         // mostra informações sobre a mensagem com reações de atribuição",
        "rolereaction <key> <emote>                 // mostra informações sobre a reação de atribuição",
        "rolereaction add <key> <emote> <action>    // adiciona uma reação de atribuição a uma mensagem",
        "rolereaction remove <key> <emote>          // remove uma reação de atribuição de uma mensagem",
        "rolereaction edit <key> <options>          // modifica uma reação de atribuição de uma mensagem",
        "rolereaction delete <key>                  // remove todas as reações de atribuição de uma mensagem", "",
        "rolereaction delete <key> <emote>          // remove uma reação de atribuição de uma mensagem", "",
        "Opções para Criação/Modificação:",
        "<--key <keyvalue>>                         -  Define a mensagem da reação de atribuição",
        "<--emote <emote>>                          -  Define o emote da reação de atribuição",
        "<--action <toggle,give,remove>>            -  Define o tipo da atribuição",
        "[--role <role[,role,...]>]                 -  Comando apenas afetará os cargos especificados",
        "[--category <category[,category,...]>]     -  Comando apenas afetará os categoria especificados",
        "[--addrole <role[,role,...]>]              -  Adiciona os cargos especificados à atribuição",
        "[--remrole <role[,role,...]>]              -  Remove os cargos especificadas da atribuição",
        "[--addcategory <category[,category,...]>]  -  Adiciona os cargos das categorias especificadas à atribuição",
        "[--remcategory <category[,category,...]>]  -  Remove os cargos das categorias especificadas da atribuição", };
  }

  @Override
  public String[] getAliases() {
    return new String[] { "rr", };
  }

  @Override
  public String execute(LojinhoBot bot, List<String> args, MessageChannel channel, Message message) {
    Guild guild = ((TextChannel) channel).getGuild(); // this guild?
    Member target = message.getMember(); // self or other if aplicable
    UserRank rank = bot.security.getUserRankForGuild(message.getAuthor(), guild);

    // bot_admin ? is managing another guild?
    if (rank.isAtLeast(UserRank.BOT_ADMIN) && args.size() >= 1) {
      Guild foundGuild = DisUtil.findGuild(((TextChannel) channel), args.get(0));
      if (foundGuild != null) {
        args.remove(0); // remove guild mention for default args
        guild = foundGuild;
      }
    }

    // [ loj.rr ] mostra mensagens com reações de atribuição
    if (args.size() == 0 || args.get(0).equals("page")) {
      // CRoleReaction.getKeysForGuild(guild)

      // Send Embed
      if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
          Permission.MESSAGE_EMBED_LINKS)) {
        // build embed
      }

      // Send Message
      // build message
      return "";
    }

    // [ loj.rr <arg> ]
    switch (args.get(0).toLowerCase()) {
      // adiciona uma reação de atribuição a uma mensagem
      // rr add <key> <emote> <action> <options>
      case "addreaction":
      case "create":
      case "add":
      case "a":
      case "+": {
        if (args.size() < 4)
          return ""; // insufficient number of arguments

        if (!PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_HISTORY))
          return "Eu não possuo permissões para buscar uma mensagem";
        // Message treatment

        // Emote treatment
        if (!DisUtil.isEmote(bot, args.get(2)))
          return "Nenhum emote encontrado com " + args.get(2);
        boolean isNormalEmote = EmojiUtils.isEmoji(args.get(2));
        String emoteId = Misc.getGuildEmoteId(args.get(2));
        if (!isNormalEmote && bot.getJda().getEmoteById(emoteId) == null)
          return "Não foi possível encontrar o emote " + args.get(2);

        // Options treatment
        Map<String, List<String>> argOptions = Misc.getArgOptionsOf(args.subList(4, args.size()));
        if (!argOptions.containsKey("role") && !argOptions.containsKey("category") && !argOptions.containsKey("addrole")
            && !argOptions.containsKey("addcategory"))
          return "Especifique pelo menos um cargo ou categoria com a --addrole ou --addcategory\noptions: "
              + argOptions;

        List<Role> roles = new ArrayList<>();

        // add/remove categories
        if (argOptions.containsKey("category"))
          for (String categorySearch : argOptions.get("category")) {
            ORoleCategory category = CRoleCategory.findBy(guild, categorySearch);
            if (category.id != 0 && category.category_type == RoleCategoryType.SELF_ASSIGN.getId())
              for (OGuildRole dbRole : CRoleCategory.getRolesForKey(guild, category.id))
                if (!(guild.getRoleById(dbRole.discord_id) == null))
                  roles.add(guild.getRoleById(dbRole.discord_id));
          }
        if (argOptions.containsKey("addcategory"))
          for (String categorySearch : argOptions.get("addcategory")) {
            ORoleCategory category = CRoleCategory.findBy(guild, categorySearch);
            if (category.id != 0 && category.category_type == RoleCategoryType.SELF_ASSIGN.getId())
              for (OGuildRole dbRole : CRoleCategory.getRolesForKey(guild, category.id))
                if (!(guild.getRoleById(dbRole.discord_id) == null))
                  roles.add(guild.getRoleById(dbRole.discord_id));
          }
        if (argOptions.containsKey("remcategory"))
          for (String categorySearch : argOptions.get("remcategory")) {
            ORoleCategory category = CRoleCategory.findBy(guild, categorySearch);
            if (category.id != 0 && category.category_type == RoleCategoryType.SELF_ASSIGN.getId())
              for (OGuildRole dbRole : CRoleCategory.getRolesForKey(guild, category.id))
                if (!(guild.getRoleById(dbRole.discord_id) == null))
                  roles.remove(guild.getRoleById(dbRole.discord_id));
          }

        // add/remove roles
        if (argOptions.containsKey("role"))
          for (String roleSearch : argOptions.get("role"))
            if (!(DisUtil.findRole(((TextChannel) channel), roleSearch) == null))
              roles.add(DisUtil.findRole(guild, roleSearch));
            else // too many roles found
              return "";
        if (argOptions.containsKey("addrole"))
          for (String roleSearch : argOptions.get("addrole"))
            if (!(DisUtil.findRole(((TextChannel) channel), roleSearch) == null))
              roles.add(DisUtil.findRole(guild, roleSearch));
            else // too many roles found
              return "";
        if (argOptions.containsKey("remrole"))
          for (String roleSearch : argOptions.get("remrole"))
            if (!(DisUtil.findRole(((TextChannel) channel), roleSearch) == null))
              roles.remove(DisUtil.findRole(guild, roleSearch));
            else // too many roles found
              return "";

        if (roles.size() == 0)
          return "Nenhum cargo foi coincidiu com os critérios inseridos";

        // Add
        ORoleReactionKey key = CRoleReaction.findOrCreate(guild, args.get(1));
        for (Role role : roles)
          CRoleReaction.addReactionRole(key.id, isNormalEmote ? args.get(2) : emoteId, isNormalEmote, role.getIdLong());

        PaginationInfo<?> paginationInfo = new PaginationInfo<>(1, 1, guild);

        channel.retrieveMessageById(args.get(1))
            .queue(msg -> bot.commandReactionHandler.addReactionListener(((TextChannel) channel).getGuild().getIdLong(),
                msg, getReactionListener(message.getAuthor(), paginationInfo)));

        return String.format("Sucesso. Adicionados %d cargos à atribuição pelo emote %s da mensagem %s", roles.size(),
            args.get(2), key.message_key);

      }

      // remove uma reação de atribuição de uma mensagem
      // rr remove <key> <emote>
      case "remreaction":
      case "remove":
      case "r":
      case "-": {
        if (args.size() < 3)
          return ""; // insufficient number of arguments

        // Emote treatment
        if (!DisUtil.isEmote(bot, args.get(2)))
          return "Nenhum emote encontrado com " + args.get(2);
        boolean isNormalEmote = EmojiUtils.isEmoji(args.get(2));
        String emoteId = Misc.getGuildEmoteId(args.get(2));
        if (!isNormalEmote && bot.getJda().getEmoteById(emoteId) == null)
          return "Não foi possível encontrar o emote " + args.get(2);

        // Options treatment
        Map<String, List<String>> argOptions = Misc.getArgOptionsOf(args.subList(4, args.size()));
        if (!argOptions.containsKey("addrole") && !argOptions.containsKey("addcategory"))
          return "Especifique pelo menos um cargo ou categoria com a --addrole ou --addcategory";
        List<Role> roles = new ArrayList<>();

        if (argOptions.containsKey("addcategory"))
          for (String categorySearch : argOptions.get("addcategory")) {
            ORoleCategory category = CRoleCategory.findBy(guild, categorySearch);
            if (category.id != 0 && category.category_type == RoleCategoryType.SELF_ASSIGN.getId())
              for (OGuildRole dbRole : CRoleCategory.getRolesForKey(guild, category.id))
                if (!(guild.getRoleById(dbRole.discord_id) == null))
                  roles.add(guild.getRoleById(dbRole.discord_id));
          }

        if (argOptions.containsKey("addrole"))
          for (String roleSearch : argOptions.get("addrole"))
            if (!(DisUtil.findRole(guild, roleSearch) == null))
              roles.add(DisUtil.findRole(guild, roleSearch));

        break;
      }

      // modifica uma reação de atribuição de uma mensagem
      // rr edit <key> <options>
      case "change":
      case "modify":
      case "edit":
      case "e":

        if (args.size() < 4)
          return ""; // insufficient number of arguments

        break;

      // remove todas as reações de atribuição de uma mensagem
      // rr delete <key>
      case "delreaction":
      case "delete":
      case "del":
      case "d":

        if (args.size() < 2)
          return ""; // insufficient number of arguments

        break;

      default:
        // [ rr <key> ] mostra informações sobre a mensagem com reações de atribuição
        // ORoleReaction = CRoleReaction.findBy(guild, arg.get(0))

        // Send Embed
        if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_EMBED_LINKS)) {
          // build embed
        }

        // Send Message
        // build message

        // ###########################

        // [ rr <key> <emote> ] mostra informações sobre a mensagem com reações de
        // atribuição
        // CRoleReaction.findBy(guild, arg.get(0)).getReactions();

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

  // hardcoded for testing
  @Override
  public CommandReactionListener<PaginationInfo<?>> getReactionListener(User user, PaginationInfo<?> data) {
    CommandReactionListener<PaginationInfo<?>> listener = new CommandReactionListener<>(549281389044826113L, true,
        data);

    listener.setExpiresIn(TimeUnit.MINUTES, 2);

    listener.registerReaction(Emojibet.WATERMELON, o -> {
      Guild guild = data.getGuild();
      Member target = guild.getMemberById(listener.getUserId());
      List<Role> roles = new ArrayList<>();
      roles.add(guild.getRoleById(784862304323764244L));
      roles.add(guild.getRoleById(784862395536244786L));
      roles.add(guild.getRoleById(784862527389171774L));

      if (guild.getMembersWithRoles(roles).contains(target))
        for (Role role : roles)
          guild.removeRoleFromMember(target, role).queue();
      else
        for (Role role : roles)
          guild.addRoleToMember(target, role).queue();

      o.removeReaction(Emojibet.NEXT_TRACK, user).complete();
    });

    return listener;
  }

}
