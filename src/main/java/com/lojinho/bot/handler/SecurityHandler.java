package com.lojinho.bot.handler;

import java.util.HashSet;
import java.util.stream.Collectors;

import com.lojinho.bot.db.controllers.CGuild;
import com.lojinho.bot.db.controllers.CUser;
import com.lojinho.bot.main.BotConfig;
import com.lojinho.bot.permission.UserRank;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class SecurityHandler {

  private final static HashSet<String> bannedGuilds = new HashSet<>();
  private final static HashSet<String> bannedUsers = new HashSet<>();
  private final static HashSet<String> botAdmins = new HashSet<>();
  private final static HashSet<String> systemAdmins = new HashSet<>();

  public SecurityHandler() {
    bannedGuilds.clear();
    bannedUsers.clear();
    botAdmins.clear();
    systemAdmins.clear();

    // populate from db
    bannedGuilds.addAll(CGuild.getBannedGuilds().stream().map(guild -> guild.discord_id).collect(Collectors.toList()));
    bannedUsers.addAll(CUser.getBannedUsers().stream().map(user -> user.discord_id).collect(Collectors.toList()));
    // botAdmins.addAll(CUserRank.getUsersWith(CRank.findBy("BOT_ADMIN").id).stream().map(oUserRank -> CUser.getCachedDiscordId(oUserRank.userId)).collect(Collectors.toList()));
    // systemAdmins.addAll(CUserRank.getUsersWith(CRank.findBy("SYSTEM_ADMIN").id).stream().map(oUserRank -> CUser.getCachedDiscordId(oUserRank.userId)).collect(Collectors.toList()));
  }

  // ******************** BANNED GUILDS ********************

  public boolean isBanned(Guild guild) {
    return bannedGuilds.contains(guild.getId());
  }

  public synchronized void addGuildBan(Guild guild) {
    addGuildBan(guild.getIdLong());
  }

  public synchronized void addGuildBan(long discordId) {
    addGuildBan(String.valueOf(discordId));
  }

  public synchronized void addGuildBan(String discordId) {
    if (!bannedGuilds.contains(discordId))
      bannedGuilds.add(discordId);
  }

  public synchronized void removeGuildBan(Guild guild) {
    removeGuildBan(guild.getIdLong());
  }

  public synchronized void removeGuildBan(long discordId) {
    removeGuildBan(String.valueOf(discordId));
  }

  public synchronized void removeGuildBan(String discordId) {
    if (bannedGuilds.contains(discordId))
      bannedGuilds.remove(discordId);
  }

  // ******************** BANNED USERS ********************

  public boolean isBanned(User user) {
    return bannedUsers.contains(user.getId());
  }

  public synchronized void addUserBan(long discordId) {
    addUserBan(String.valueOf(discordId));
  }

  public synchronized void addUserBan(String discordId) {
    if (!bannedUsers.contains(discordId))
      bannedUsers.add(discordId);
  }

  public synchronized void removeUserBan(long discordId) {
    removeUserBan(String.valueOf(discordId));
  }

  public synchronized void removeUserBan(String discordId) {
    if (bannedUsers.contains(discordId))
      bannedUsers.remove(discordId);
  }

  // ******************** USER RANK ********************

  public UserRank getUserRank(User user) {
    return getUserRankForGuild(user, null);
  }

  public UserRank getUserRank(User user, MessageChannel channel) {
    if (channel instanceof TextChannel) {
      return getUserRankForGuild(user, ((TextChannel) channel).getGuild());
    }
    return getUserRankForGuild(user, null);
  }

  public UserRank getUserRankForGuild(User user, Guild guild) {
    String userId = user.getId();
    if (BotConfig.CREATOR_ID.equals(userId)) {
      return UserRank.CREATOR;
    }
    if (user.isBot()) {
      return UserRank.BOT;
    }
    if (botAdmins.contains(userId)) {
      return UserRank.BOT_ADMIN;
    }
    if (bannedUsers.contains(userId)) {
      return UserRank.BANNED_USER;
    }
    if (guild != null) {
      if (guild.getOwner().getUser().equals(user)) {
        return UserRank.GUILD_OWNER;
      }
      if (PermissionUtil.checkPermission(guild.getMember(user), Permission.ADMINISTRATOR)) {
        return UserRank.GUILD_ADMIN;
      }
      // GuildSettings -> check for GUILD_BOT_ADMIN role or category
    }
    return UserRank.USER;
  }
}
