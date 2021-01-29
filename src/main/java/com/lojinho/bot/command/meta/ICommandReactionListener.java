package com.lojinho.bot.command.meta;

import net.dv8tion.jda.api.entities.User;

public interface ICommandReactionListener<T> {

  CommandReactionListener<T> getReactionListener(User user, T initialData);

}
