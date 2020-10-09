package com.lojinho.bot.main;

import com.lojinho.bot.core.listeners.LojinhoListener;
import com.lojinho.bot.data.Config;
import com.lojinho.bot.db.DbUpdate;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class LojinhoBot {

  // Buildando o Objeto JDA
  private LojinhoBot() throws Exception {
    JDABuilder builder = JDABuilder.createDefault(Config.get("TOKEN"), GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS);
    builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE);
    builder.setMemberCachePolicy(MemberCachePolicy.ALL);
    builder.addEventListeners(new LojinhoListener());
    builder.setActivity(Activity.watching(Config.get("PREFIX") + "help"));
    builder.build();
  }

  public static void main(final String[] args) throws Exception {
    DbUpdate dbUpdate = new DbUpdate();
    dbUpdate.updateToCurrent();
    new LojinhoBot();
  }
}