package com.lojinho.bot.core.listeners;

import java.sql.Timestamp;

import javax.annotation.Nonnull;

import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.controllers.CGuildMember;
import com.lojinho.bot.db.controllers.CUser;
import com.lojinho.bot.db.controllers.log.CBotEvent;
import com.lojinho.bot.db.model.OGuild;
import com.lojinho.bot.db.model.OGuildMember;
import com.lojinho.bot.db.model.OUser;
import com.lojinho.bot.guildsettings.GSetting;
import com.lojinho.bot.handler.GuildSettings;
import com.lojinho.bot.main.LojinhoBot;
import com.lojinho.bot.util.Emojibet;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Classe que extende a classe abstrata ListenerAdapter do JDA e detecta
 * eventos.
 */
public class LojinhoListener extends ListenerAdapter {
  private final LojinhoBot discordBot;

  public LojinhoListener(LojinhoBot bot) {
    this.discordBot = bot;
  }

  // ******************** BOT EVENTS ********************

  @Override
  public void onReady(@Nonnull ReadyEvent event) {
    // Update guilds, users e members do BD
    for (Guild guild : event.getJDA().getGuilds()) {
      User owner = guild.getOwner().getUser();
      OUser user = CUser.findBy(owner.getIdLong());
      // register owner
      user.discord_id = owner.getId();
      user.name = EmojiUtils.shortCodify(owner.getName());
      CUser.update(user);
      // register guild
      OGuild dbGuild = CGuild.findBy(guild.getId());
      dbGuild.discord_id = guild.getId();
      dbGuild.name = EmojiUtils.shortCodify(guild.getName());
      dbGuild.owner = user.id;
      if (dbGuild.id == 0) {
        CGuild.insert(dbGuild);
      }
      if (dbGuild.isBanned()) {
        guild.leave().queue();
        continue;
      }
      // register members
      for (Member member : guild.getMembers()) {
        User guildUser = member.getUser();
        int userId = CUser.getCachedId(guildUser.getIdLong(), guildUser.getName());
        OGuildMember guildMember = CGuildMember.findBy(dbGuild.id, userId);
        guildMember.joinDate = new Timestamp(member.getTimeJoined().toInstant().toEpochMilli());
        CGuildMember.insertOrUpdate(guildMember);
      }
      LojinhoBot.LOGGER.info("BD atualizado com sucesso para a guild {}", guild);
    }

    LojinhoBot.LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
  }

  // ******************** MESSAGE EVENTS ********************

  @Override
  public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
    discordBot.handlePrivateMessage(event);
  }

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    discordBot.handleMessage(event);
  }

  // ******************** REACTION EVENTS ********************

  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    discordBot.handleReaction(event, true);
  }

  @Override
  public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
    discordBot.handleReaction(event, false);
  }

  // ******************** GUILD EVENTS ********************

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    Guild guild = event.getGuild();
    User owner = guild.getOwner().getUser();
    OUser user = CUser.findBy(owner.getIdLong());
    // register owner
    user.discord_id = owner.getId();
    user.name = EmojiUtils.shortCodify(owner.getName());
    CUser.update(user);
    // register guild
    OGuild dbGuild = CGuild.findBy(guild.getId());
    dbGuild.discord_id = guild.getId();
    dbGuild.name = EmojiUtils.shortCodify(guild.getName());
    dbGuild.owner = user.id;
    if (dbGuild.id == 0) {
      CGuild.insert(dbGuild);
    }
    if (dbGuild.isBanned()) {
      guild.leave().queue();
      return;
    }
    // Atualizar BD guild
    if (dbGuild.active != true) {
      dbGuild.active = true;
      CGuild.update(dbGuild);
      CBotEvent.insert(":house:", ":white_check_mark:",
          String.format(":id: %s | :hash: %s | :busts_in_silhouette: %s | %s", guild.getId(), dbGuild.id,
              guild.getMembers().size(), EmojiUtils.shortCodify(guild.getName())).replace("@", "@\u200B"));
    }

    LojinhoBot.LOGGER.info("[event] JOINED SERVER! " + guild.getName());

    // Teste
    String cmdPre = GuildSettings.get(guild).getOrDefault(GSetting.COMMAND_PREFIX);
    TextChannel outChannel = guild.getDefaultChannel();
    if (outChannel.canTalk()) {
      outChannel.sendMessage("Join Test. Inicializando Config. Prefix carregado: `" + cmdPre + "`\nGravando usuários.")
          .queue();
    }

    // Gravar BD users
    for (Member member : event.getGuild().getMembers()) {
      User guildUser = member.getUser();
      int userId = CUser.getCachedId(guildUser.getIdLong(), guildUser.getName());
      OGuildMember guildMember = CGuildMember.findBy(dbGuild.id, userId);
      guildMember.joinDate = new Timestamp(member.getTimeJoined().toInstant().toEpochMilli());
      CGuildMember.insertOrUpdate(guildMember);
    }

    outChannel.sendMessage("`Usuários carregados com sucesso. " + Emojibet.CHECK_MARK_GREEN + "`").queue();
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    Member target = event.getMember();
    Guild guild = event.getGuild();
    LojinhoBot.LOGGER.info("[event] " + target.getNickname() + " JOINED SERVER! " + guild.getName());
    Role role = guild.getRoleById("784863559648542760");
    guild.addRoleToMember(target, role).queue();
  }
}
