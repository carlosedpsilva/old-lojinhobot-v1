package com.lojinho.bot.main;

import java.io.File;

import com.kaaz.configuration.ConfigurationBuilder;
import com.lojinho.bot.core.ExitCode;
import com.lojinho.bot.core.Logger;
import com.lojinho.bot.db.DbUpdate;

public class Launcher {

  public volatile static boolean isBeingKilled = false;
  // private static LojinhoBot bot;

  public static void main(final String[] args) throws Exception {
    new ConfigurationBuilder(BotConfig.class, new File(".env")).build(true);

    DbUpdate dbUpdate = new DbUpdate();
    dbUpdate.updateToCurrent();

    if (BotConfig.BOT_ENABLED) {
      try {
        new LojinhoBot();
      } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();

      }
    } else {
      Logger.fatal("Bot não está habilitado, habilite-o na config. Basta definir bot_enable=true.");
    }
  }

  public static void stop(ExitCode reason) {
    stop(reason, null);
  }

  public static void stop(ExitCode reason, Exception e) {
    if (isBeingKilled) {
      return;
    }
    isBeingKilled = true;
    LojinhoBot.LOGGER.info("Saindo devido a: " + reason);
    if (e != null) {
      System.out.println(e);
    }
    System.exit(reason.getCode());
  }

}
