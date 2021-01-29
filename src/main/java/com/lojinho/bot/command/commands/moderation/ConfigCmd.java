package com.lojinho.bot.command.commands.moderation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;
import com.lojinho.bot.command.meta.AbstractCommand;
import com.lojinho.bot.command.meta.CommandReactionListener;
import com.lojinho.bot.command.meta.CommandVisibility;
import com.lojinho.bot.command.meta.ConfigDisplay;
import com.lojinho.bot.command.meta.ICommandReactionListener;
import com.lojinho.bot.command.meta.PaginationInfo;
import com.lojinho.bot.guildsettings.DefaultGuildSettings;
import com.lojinho.bot.guildsettings.GSetting;
import com.lojinho.bot.handler.GuildSettings;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.permission.UserRank;
import com.lojinho.bot.util.DisUtil;
import com.lojinho.bot.util.Emojibet;
import com.lojinho.bot.util.Misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class ConfigCmd extends AbstractCommand implements ICommandReactionListener<PaginationInfo<ConfigDisplay>> {
  private static final int CFG_PER_PAGE = 9;
  private static PaginationInfo<ConfigDisplay> paginationInfo;

  public ConfigCmd() {
    super();
  }

  @Override
  public String getDescription() {
    return "Mostrar/alterar configurações do servidor";
  }

  @Override
  public String getCommand() {
    return "config";
  }

  @Override
  public String[] getUsage() {
    return new String[] { "guildconfig                    // overview",
        "guildconfig page <number>      // mostra página",
        "guildconfig tags               // mostra as tags de configuração",
        "guildconfig tag <tagname>      // mostra as configurações com esta tag",
        "guildconfig <property>         // mostra detalhes desta propriedade",
        "guildconfig <property> <value> // define um valor a esta propriedade", "",
        "guildconfig reset              // restaura a configuração para o padrão", };
  }

  @Override
  public String[] getAliases() {
    return new String[] { "cfg", "guildconfig", "gcfg", "settings", "guildsettings", "guildstg", "gstg", };
  }

  @Override
  public CommandVisibility getVisibility() {
    return CommandVisibility.PUBLIC;
  }

  @Override
  public String execute(LojinhoBot bot, List<String> args, MessageChannel channel, Message message) {
    Guild guild = ((TextChannel) channel).getGuild(); // guild from where cmd came from
    UserRank rank = bot.security.getUserRankForGuild(message.getAuthor(), guild);

    // rank >= bot_admin ? args.get(0) == <guildId> ?
    if (rank.isAtLeast(UserRank.BOT_ADMIN) && args.size() >= 1) {
      Guild foundGuild = DisUtil.findGuild((TextChannel) channel, args.get(0));
      if (foundGuild != null) {
        args.remove(0); // remove guild mention for default args
        guild = foundGuild;
      }
    }

    // Tratamento de permissão
    if (!rank.isAtLeast(UserRank.GUILD_ADMIN)) {
      return "Você não possui permissões para utilizar este comando.";
    }

    // Resetar as configurações para a guild
    if (args.size() > 0 && args.get(0).equalsIgnoreCase("reset")) {
      if (args.size() > 1 && args.get(1).equalsIgnoreCase("confirm")) {
        GuildSettings.get(guild).reset();
        return String.format("Configurações para o servidor `%s` redefinadas para o padrão", guild.getName());
      }
      return String.format("Utilize `%scfg reset confirm` para redefinir as configurações",
          DisUtil.getCommandPrefix(guild));
    }

    // Tag option
    String tag = null;
    if (args.size() > 0) {
      // Mostrat todas as tags
      if (args.get(0).equals("tags")) {

        // Get tags
        TreeSet<String> settingTags = DefaultGuildSettings.getAllTags();
        if (!rank.isAtLeast(UserRank.BOT_ADMIN))
          settingTags.remove("INTERNAL");

        // Send Embed
        if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
            Permission.MESSAGE_EMBED_LINKS)) {
          EmbedBuilder b = new EmbedBuilder();
          b.setTitle("Estas são as tags de configurações existentes:", null);
          b.setFooter(String.format("Utilize `%scfg tag <tagname>` para ver as configurações com uma tag",
              DisUtil.getCommandPrefix(guild)));
          b.setColor(new Color(0xff9700));
          b.setDescription(Joiner.on("\n").join(settingTags));
          channel.sendMessage(b.build()).queue();
          return "";
        }
        // Send Message
        StringBuilder tagsOutput = new StringBuilder();
        tagsOutput.append("**Estas são as tags de configurações existentes:**\n\n");
        tagsOutput.append(Joiner.on("\n").join(settingTags) + "\n\n");
        tagsOutput.append(
            "Utilize `" + DisUtil.getCommandPrefix(guild) + "cfg tag <tagname>` para ver as configurações com uma tag");
        return tagsOutput.toString();
      }
      //
      if (args.get(0).equals("tag") && args.size() > 1) {
        tag = args.get(1).toLowerCase();
      }
    }

    // Tratamento do valor da tag
    boolean isEmpty = true;
    if (tag != null && DefaultGuildSettings.getAllTags().contains(tag.toUpperCase())) {
      for (String key : DefaultGuildSettings.getAllKeys()) {
        if (DefaultGuildSettings.get(key).hasTag(tag))
          if (!DefaultGuildSettings.get(key).isInternal()
              || (DefaultGuildSettings.get(key).isInternal() && rank.isAtLeast(UserRank.BOT_ADMIN)))
            isEmpty = false;
      }
    }

    if (tag != null && isEmpty) {
      return "Nenhuma configuração encontrada com a tag `" + tag + "`";
    }

    // Mostrar configurações
    if (args.size() == 0 || tag != null || args.get(0).equals("page")) {
      String[] settings = GuildSettings.get(guild).getSettings();
      List<String> keys = new ArrayList<>(DefaultGuildSettings.getAllKeys(tag, rank.isAtLeast(UserRank.BOT_ADMIN)));
      Collections.sort(keys);

      int activePage = 0;
      int maxPage = (int) Math
          .ceil((double) DefaultGuildSettings.getAllKeys(tag, rank.isAtLeast(UserRank.BOT_ADMIN)).size()
              / (double) CFG_PER_PAGE);

      // Set page
      if (args.size() > 1 && args.get(0).equals("page")) {
        activePage = Math.max(0, Math.min(maxPage - 1, Misc.parseInt(args.get(1), 0) - 1));
      }

      // Send Embed
      if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(),
          Permission.MESSAGE_EMBED_LINKS)) {
        paginationInfo = new PaginationInfo<>(activePage, maxPage, guild, new ConfigDisplay(tag, rank));
        channel.sendMessage(makeConfigEmbed(guild, activePage, tag, rank)).queue(msg -> {
          if (paginationInfo.getMaxPage() >= 1)
            bot.commandReactionHandler.addReactionListener(((TextChannel) channel).getGuild().getIdLong(), msg,
                getReactionListener(message.getAuthor(), paginationInfo));
        });
        return "";
      }
      // Send Message
      int endIndex = activePage * CFG_PER_PAGE + CFG_PER_PAGE;
      StringBuilder configOutput = new StringBuilder();
      configOutput.append(String.format("**Configurações atuais para %s**\n", guild.getName()));
      configOutput.append(String.format("Para ver mais detalhes sobre uma config:\n" + "`%1$scfg settingname`\n\n",
          DisUtil.getCommandPrefix(guild)));
      if (tag != null)
        configOutput.append("Mostrando apenas configs com a tag `").append(tag).append("`").append("\n\n");
      configOutput.append("`[ \u2139 ]` | Configs indicadas com um `*` são diferentes do valor padrão\n\n");

      // add config
      String cfgFormat = "`\u200B%s`  :\n**Valor:** %s\n";
      for (int i = activePage * CFG_PER_PAGE; i < keys.size() && i < endIndex; i++) {
        String key = keys.get(i);
        GSetting gSetting = GSetting.valueOf(key);

        // skip internal settings if not bot admin
        if (DefaultGuildSettings.get(key).isInternal()) {
          if (!rank.isAtLeast(UserRank.BOT_ADMIN)) {
            continue;
          }
        }

        // skip settings filtered by tag
        if (tag != null && !DefaultGuildSettings.get(key).hasTag(tag)) {
          continue;
        }

        String indicator = "[ # ] ";
        if (rank.isAtLeast(UserRank.BOT_ADMIN) && DefaultGuildSettings.get(key).isInternal())
          indicator = "[ r ] ";
        else if (!settings[gSetting.ordinal()].equals(DefaultGuildSettings.getDefault(key)))
          indicator = "[ * ] ";

        configOutput.append(String.format(cfgFormat, indicator + key.toLowerCase(),
            GuildSettings.get(guild).getDisplayValue(guild, key)));
      }
      configOutput
          .append(String.format("\n`Página %d / %d | Utilize %scfg page <number> para avançar para outras páginas`",
              (activePage + 1), maxPage, DisUtil.getCommandPrefix(((TextChannel) channel).getGuild())));

      return configOutput.toString();
    }

    if (!DefaultGuildSettings.isValidKey(args.get(0))) {
      return "Não existe uma configuração com o nome '`" + args.get(0)
          + "`'. Utilize o help deste comando para mais informações.";
    }
    if (DefaultGuildSettings.get(args.get(0)).isInternal() && !rank.isAtLeast(UserRank.BOT_ADMIN)) {
      return "A configuração '`" + args.get(0) + "`' é para leitura apenas.";
    }

    if (args.size() >= 2) {
      StringBuilder newValueBuilder = new StringBuilder(args.get(1));
      for (int i = 2; i < args.size(); i++) {
        newValueBuilder.append(" ").append(args.get(i));
      }
      String newValue = newValueBuilder.toString();
      if (newValue.length() > 64) {
        newValue = newValue.substring(0, 64);
      }

      // if (args.get(0).equals("bot_listen") && args.get(1).equals("mine")) {
      // channel.sendMessage(
      // "I will only listen to the configured `bot_channel`. If you rename the
      // channel, you might not be able to access me anymore. "
      // + "You can reset by typing `@" + channel.getJDA().getSelfUser().getName() + "
      // reset yesimsure`")
      // .queue();
      // }

      if (GuildSettings.get(guild).set(guild, args.get(0), newValue)) {
        StringBuilder updateOutput = new StringBuilder();
        updateOutput.append("**CONFIGURAÇÃO ATUALIZADA**\n");
        updateOutput
            .append(String.format("Configuração: `%s`\n", DefaultGuildSettings.get(args.get(0)).name().toLowerCase()));
        if (!((TextChannel) channel).getGuild().equals(guild))
          updateOutput.append(String.format("Servidor: `%s`\n", guild.getName()));
        updateOutput
            .append(String.format("Novo valor: `%s`\n", GuildSettings.get(guild).getDisplayValue(guild, args.get(0))));
        return updateOutput.toString();
      }
    }

    GuildSettings setting = GuildSettings.get(guild);
    String description = setting.getDescription(args.get(0));
    StringBuilder infoOutput = new StringBuilder();
    infoOutput.append(String.format("**Detalhes da configuração %s**\n\n", args.get(0).toUpperCase()));
    infoOutput.append(String.format("`%12s`  :  **%s**\n", "Valor padrão", setting.getDefaultValue(args.get(0))));
    infoOutput.append(String.format("`%12s`  :  **%s**\n\n", "Valor atual",
        GuildSettings.get(guild).getDisplayValue(guild, args.get(0))));
    infoOutput.append(String.format("Descrição:\n%s", Misc.makeTable(description)));
    infoOutput.append(String.format("Para redefinir o valor para o padrão: %scfg %s %s",
        DisUtil.getCommandPrefix(guild), args.get(0), setting.getDefaultValue(args.get(0))));
    return infoOutput.toString();
  }

  @Override
  public CommandReactionListener<PaginationInfo<ConfigDisplay>> getReactionListener(User user,
      PaginationInfo<ConfigDisplay> data) {

    CommandReactionListener<PaginationInfo<ConfigDisplay>> listener = new CommandReactionListener<>(user.getIdLong(),
        false, data);

    listener.setExpiresIn(TimeUnit.MINUTES, 2);

    listener.registerReaction(Emojibet.PREV_TRACK, o -> {
      if (listener.getData().previousPage()) {
        o.editMessage(
            new MessageBuilder().setEmbed(makeConfigEmbed(data.getGuild(), listener.getData().getCurrentPage(),
                listener.getData().getExtra().tag, listener.getData().getExtra().rank)).build())
            .complete();
      }
      o.removeReaction(Emojibet.PREV_TRACK, user).complete();
    });

    listener.registerReaction(Emojibet.NEXT_TRACK, o -> {
      if (listener.getData().nextPage()) {
        o.editMessage(
            new MessageBuilder().setEmbed(makeConfigEmbed(data.getGuild(), listener.getData().getCurrentPage(),
                listener.getData().getExtra().tag, listener.getData().getExtra().rank)).build())
            .complete();
      }
      o.removeReaction(Emojibet.NEXT_TRACK, user).complete();
    });

    return listener;
  }

  private static MessageEmbed makeConfigEmbed(Guild guild, int activePage, String tag, UserRank rank) {
    EmbedBuilder b = new EmbedBuilder();

    String[] settings = GuildSettings.get(guild).getSettings();
    List<String> keys = new ArrayList<>(DefaultGuildSettings.getAllKeys(tag, rank.isAtLeast(UserRank.BOT_ADMIN)));
    Collections.sort(keys);

    int maxPage = (int) Math.ceil((double) keys.size() / (double) CFG_PER_PAGE);
    activePage = Math.max(0, Math.min(maxPage - 1, activePage));

    int endIndex = activePage * CFG_PER_PAGE + CFG_PER_PAGE;

    int elements = 0;
    StringBuilder description = new StringBuilder();

    // embed info
    b.setTitle("Configurações atuais para " + guild.getName(), null);

    description.append(String.format("Para ver mais detalhes sobre uma config:\n`%scfg settingname`\n\n",
        DisUtil.getCommandPrefix(guild), activePage, keys.size(), endIndex));
    if (tag != null)
      description.append("Mostrando apenas configs com a tag `").append(tag).append("`").append("\n\n");
    description.append("`[ \u2139 ]` | Configs indicadas com um `*` são diferentes do valor padrão\n\n");

    b.setDescription(description.toString());

    b.setFooter(String.format("Página %d / %d%s", (activePage + 1), maxPage,
        (maxPage > 1 ? " | Reaja com os botões para avançar para outras págs" : "")), null);

    b.setColor(new Color(0xff9700));

    // add fields
    String cfgFormat = "\u200B%-30s";
    for (int i = activePage * CFG_PER_PAGE; i < keys.size() && i < endIndex; i++) {
      String key = keys.get(i);
      GSetting gSetting = GSetting.valueOf(key);

      // skip internal settings if not bot admin
      if (DefaultGuildSettings.get(key).isInternal()) {
        if (!rank.isAtLeast(UserRank.BOT_ADMIN)) {
          continue;
        }
      }

      // skip settings filtered by tag
      if (tag != null && !DefaultGuildSettings.get(key).hasTag(tag)) {
        continue;
      }

      String indicator = "[ # ]\n";
      if (rank.isAtLeast(UserRank.BOT_ADMIN) && DefaultGuildSettings.get(key).isInternal())
        indicator = "[ r ]\n";
      else if (!settings[gSetting.ordinal()].equals(DefaultGuildSettings.getDefault(key)))
        indicator = "[ * ]\n";

      elements++;
      b.addField(String.format(cfgFormat, indicator + key.toLowerCase()),
          String.format("`%s`", GuildSettings.get(guild).getDisplayValue(guild, key)), true);
    }

    if (elements % 3 == 2) {
      b.addBlankField(true);
    }

    return b.build();
  }
}
