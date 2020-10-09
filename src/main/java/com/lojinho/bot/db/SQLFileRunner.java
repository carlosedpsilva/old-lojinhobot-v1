package com.lojinho.bot.db;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLFileRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(SQLFileRunner.class);

  private static final String DEFAULT_DELIMITER = ";";

  private static final Pattern delimP = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$",
      Pattern.CASE_INSENSITIVE);

  private final Connection connection;

  private final boolean stopOnError;
  private final boolean autoCommit;

  private String delimiter = DEFAULT_DELIMITER;
  private boolean fullLineDelimiter = false;

  SQLFileRunner(Connection connection, boolean autoCommit, boolean stopOnError) {
    this.connection = connection;
    this.autoCommit = autoCommit;
    this.stopOnError = stopOnError;
    LOGGER.debug("SQL IMPORT FILE");
  }

  private void setDelimiter(String delimiter, boolean fullLineDelimiter) {
    this.delimiter = delimiter;
    this.fullLineDelimiter = fullLineDelimiter;
  }

  public void runScript(Reader reader) throws IOException, SQLException {
    try {
      boolean originalAutoCommit = connection.getAutoCommit();
      try {
        if (originalAutoCommit != this.autoCommit) {
          connection.setAutoCommit(this.autoCommit);
        }
        runScriot(connection, reader);
      } finally {
        connection.setAutoCommit(originalAutoCommit);
      }
    } catch (IOException | SQLException e) {
      throw e;
    } catch (Exception e) {
      System.out.println("Erro ao executar script. Causa: " + e);
    }
  }

  private void runScriot(Connection conn, Reader reader) throws IOException, SQLException {
    StringBuffer command = null;
    try {
      LineNumberReader lineReader = new LineNumberReader(reader);
      String line;
      while ((line = lineReader.readLine()) != null) {
        if (command == null) {
          command = new StringBuffer();
        }
        String trimmedLine = line.trim();
        final Matcher delimMatch = delimP.matcher(trimmedLine);
        if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
          // Skip
        } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
          // Skip
        } else if (delimMatch.matches()) {
          setDelimiter(delimMatch.group(2), false);
        } else if (trimmedLine.startsWith("--")) {
          LOGGER.info(trimmedLine);
        } else if (!fullLineDelimiter && trimmedLine.endsWith(this.getDelimiter())
            || fullLineDelimiter && trimmedLine.equals(this.getDelimiter())) {
          command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
          command.append(" ");
          this.execCommand(conn, command, lineReader);
          command = null;
        } else {
          command.append(line);
          command.append("\n");
        }
      }
      if (command != null) {
        this.execCommand(conn, command, lineReader);
      }
      if (!autoCommit) {
        conn.commit();
      }
    } catch (IOException e) {
      conn.rollback();
      LOGGER.error("Erro ao executar '%s' : %s", command, e.getMessage());
    }
  }

  private void execCommand(Connection conn, StringBuffer command, LineNumberReader lineReader) throws SQLException {
    Statement statement = conn.createStatement();

    LOGGER.info(command.toString());

    boolean hasResults = false;
    try {
      hasResults = statement.execute(command.toString());
    } catch (SQLException e) {
      final String errMsg = String.format("Erro ao executar '%s' (linha %d): %s", command, lineReader.getLineNumber(),
          e.getMessage());
      LOGGER.info(errMsg);
      if (stopOnError) {
        throw new SQLException(errMsg, e);
      }
    }

    if (autoCommit && !conn.getAutoCommit()) {
      conn.commit();
    }

    ResultSet rs = statement.getResultSet();
    if (hasResults && rs != null) {
      ResultSetMetaData md = rs.getMetaData();
      int cols = md.getColumnCount();
      for (int i = 1; i <= cols; i++) {
        String name = md.getColumnLabel(i);
        LOGGER.debug(name + "\t");
      }
      LOGGER.info("");
      while (rs.next()) {
        for (int i = 1; i <= cols; i++) {
          String value = rs.getString(i);
          LOGGER.debug(value + "\t");
        }
        LOGGER.info("");
      }
    }

    try {
      statement.close();
    } catch (Exception e) {
    }
  }

  private String getDelimiter() {
    return delimiter;
  }
}