package com.lojinho.bot.command.meta;

import net.dv8tion.jda.api.entities.Guild;

public class PaginationInfo<E> {

  private final int maxPage;
  private final Guild guild;
  private int currentPage = 0;
  private E extraData;

  public PaginationInfo(int currentPage, int maxPage, Guild guild) {
    this(currentPage, maxPage, guild, null);
  }

  public PaginationInfo(int currentPage, int maxPage, Guild guild, E extra) {
    this.currentPage = currentPage;
    this.maxPage = maxPage;
    this.guild = guild;
    this.extraData = extra;
  }

  public void setExtraData(E data) {
    extraData = data;
  }

  public E getExtra() {
    return extraData;
  }

  public boolean previousPage() {
    if (currentPage > 0) {
      currentPage--;
      return true;
    }
    return false;
  }

  public boolean nextPage() {
    if (currentPage < (maxPage - 1)) {
      currentPage++;
      return true;
    }
    return false;
  }

  public int getMaxPage() {
    return maxPage;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public Guild getGuild() {
    return guild;
  }
}
