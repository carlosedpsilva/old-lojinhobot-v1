package com.lojinho.bot.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DbManager {
  DbManager INSTANCE = new DbHandler();

  Connection getConnection() throws SQLException;

  ResultSet select(String sql, Object... params) throws SQLException;

  int query(String sql) throws SQLException;

  int query(String sql, Object... params) throws SQLException;

  int insert(String sql, Object... params) throws SQLException;
}
