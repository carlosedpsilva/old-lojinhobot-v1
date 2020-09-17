package com.lojinho.bot;

import com.lojinho.bot.core.LojinhoListener;
import com.lojinho.bot.data.Config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class LojinhoBot {

  private LojinhoBot() throws Exception {
    JDABuilder builder = JDABuilder.createDefault(Config.get("TOKEN"));
    builder.addEventListeners(new LojinhoListener());
    builder.setActivity(Activity.watching(Config.get("PREFIX") + "help"));
    builder.build();
  }

  public static void main(final String[] args) throws Exception {
  new LojinhoBot();
  }
}