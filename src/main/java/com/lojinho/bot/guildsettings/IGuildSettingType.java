package com.lojinho.bot.guildsettings;

import net.dv8tion.jda.api.entities.Guild;

public interface IGuildSettingType {

  String typeName();

  boolean validate(Guild guild, String value);

  String fromInput(Guild guild, String value);

  String toDisplay(Guild guild, String value);
}
