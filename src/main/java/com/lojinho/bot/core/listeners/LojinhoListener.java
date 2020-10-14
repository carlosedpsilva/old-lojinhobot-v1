package com.lojinho.bot.core.listeners;

import java.sql.Timestamp;

import javax.annotation.Nonnull;

import com.lojinho.bot.command.CommandManager;
import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.controllers.CGuildMember;
import com.lojinho.bot.db.controllers.CUser;
import com.lojinho.bot.db.model.OGuild;
import com.lojinho.bot.db.model.OGuildMember;
import com.lojinho.bot.db.model.OUser;
import com.lojinho.bot.guildsettings.GSetting;
import com.lojinho.bot.handler.GuildSettings;
import com.lojinho.bot.util.Emojibet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Classe que extende a classe abstrata ListenerAdapter do JDA e detecta
 * eventos.
 */
public class LojinhoListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(LojinhoListener.class);
  private final CommandManager manager = new CommandManager();

  /** Bot is ready event */
  @Override
  public void onReady(@Nonnull ReadyEvent event) {
    LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
  }

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
    }

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

  // @Override
  // public void onGuildMemberJoin(GuildMemberJoinEvent event) {
  // User user = event.getMember().getUser();
  // Guild guild = event.getGuild();

  // super.onGuildMemberJoin(event);
  // }

  /** Mensagem recebida no servidor */
  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

    if (event.getAuthor().isBot()) {
      return;
    }

    String prefix = GuildSettings.get(event.getGuild()).getOrDefault(GSetting.COMMAND_PREFIX);
    String raw = event.getMessage().getContentRaw();

    if (raw.startsWith(prefix)) {
      manager.handle(event, prefix);
    }
  }

}
