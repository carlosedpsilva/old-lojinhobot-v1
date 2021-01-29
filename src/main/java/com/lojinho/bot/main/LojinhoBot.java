package com.lojinho.bot.main;

import java.util.concurrent.atomic.AtomicReference;

import javax.security.auth.login.LoginException;

import com.lojinho.bot.core.listeners.LojinhoListener;
import com.lojinho.bot.handler.CommandHandler;
import com.lojinho.bot.handler.CommandReactionHandler;
import com.lojinho.bot.handler.SecurityHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class LojinhoBot {

  public String mentionMe;
  public String mentionMeAlias;
  private final AtomicReference<JDA> jda;

  // handlers
  public SecurityHandler security = null;
  public CommandReactionHandler commandReactionHandler = null;

  private volatile boolean isReady = false;

  public static final Logger LOGGER = LoggerFactory.getLogger(LojinhoBot.class);

  public LojinhoBot() throws Exception {
    jda = new AtomicReference<>();
    initHandlers();
    registerHandlers();
    while (true) {
      try {
        restartJDA();
        break;
      } catch (LoginException | InterruptedException | RateLimitedException e1) {
        try {
          Thread.sleep(5_000L);
        } catch (InterruptedException e2) {
          e2.printStackTrace();
        }
      }
    }
    markReady();
  }

  public void updateJda(JDA jda) {
    this.jda.compareAndSet(this.jda.get(), jda);
  }

  public JDA getJda() {
    return jda.get();
  }

  public void restartJDA() throws LoginException, InterruptedException, RateLimitedException {
    JDABuilder builder = JDABuilder.createDefault(BotConfig.BOT_TOKEN, GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGES);
    builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE);
    builder.setMemberCachePolicy(MemberCachePolicy.ALL);
    builder.setActivity(Activity.watching(BotConfig.BOT_COMMAND_PREFIX + "help"));
    builder.setBulkDeleteSplittingEnabled(false);
    builder.setEnableShutdownHook(false);
    jda.set(builder.build());
    jda.get().addEventListener(new LojinhoListener(this));
    //
  }

  public void markReady() {
    if (isReady) {
      return;
    }
    mentionMe = "<@" + this.getJda().getSelfUser().getId() + ">";
    mentionMeAlias = "<@!" + this.getJda().getSelfUser().getId() + ">";
    isReady = true;
  }

  private void initHandlers() {
    CommandHandler.initilize();
  }

  private void registerHandlers() {
    security = new SecurityHandler();
    commandReactionHandler = new CommandReactionHandler();
  }

  // ******************** MESSAGE HANDLER ********************

  public void handlePrivateMessage(PrivateMessageReceivedEvent event) {
    MessageChannel channel = event.getChannel();
    User author = event.getAuthor();
    Message message = event.getMessage();

    if (author == null || author.isBot())
      return;

    if (security.isBanned(author))
      return;

    if (CommandHandler.isCommand(null, message.getContentRaw(), mentionMe, mentionMeAlias)) {
      CommandHandler.process(this, channel, author, message);
      return;
    }
  }

  public void handleMessage(GuildMessageReceivedEvent event) {
    TextChannel channel = event.getChannel();
    User author = event.getAuthor();
    Message message = event.getMessage();

    if (author == null || author.isBot())
      return;

    if (security.isBanned(author))
      return;

    if (CommandHandler.isCommand(channel, message.getContentRaw(), mentionMe, mentionMeAlias)) {
      CommandHandler.process(this, channel, author, message);
      return;
    }
  }

  // ******************** REACTION HANDLER ********************

  public void handleReaction(GenericMessageReactionEvent e, boolean adding) {
    if (e.getUser().isBot()) {
      return;
    }

    LojinhoBot.LOGGER.info("{} reagiu com {} em {}", e.getUser().getName(),
        e.getReaction().getReactionEmote().getEmoji(), e.getChannel().getName());

    if (!e.getChannel().getType().equals(ChannelType.TEXT)) {
      return;
    }

    TextChannel channel = (TextChannel) e.getChannel();
    if (commandReactionHandler.canHandle(channel.getGuild().getIdLong(), e.getMessageIdLong())) {
      commandReactionHandler.handle(channel, e.getMessageIdLong(), e.getUser().getIdLong(), e.getReaction(), adding);
      return;
    }
  }
}