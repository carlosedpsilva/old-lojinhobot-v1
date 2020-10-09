package com.lojinho.bot.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUpdate {
  private static final Logger LOGGER = LoggerFactory.getLogger(DbUpdate.class);

  private final DbManager dbHandler;
  private final Pattern filepattern = Pattern.compile("(\\d+)_(\\d+).*\\.sql");
  private int highestVersion = 0;
  private Map<Integer, DbVersion> versionMap;

  public DbUpdate() throws IOException {
    dbHandler = DbManager.INSTANCE;
    versionMap = new HashMap<>();
    collectDatabaseVersion();
  }

  private void collectDatabaseVersion() throws IOException {
    final String path = "db_updates";
    final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

    LOGGER.info("Coletando as versões do banco de dados");

    if (jarFile.isFile()) {
      final JarFile jar = new JarFile(jarFile);
      final Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        final JarEntry file = entries.nextElement();
        if (file.getName().startsWith(path + "/")) {
          prepareFile(file.getName());
        }
      }
      LOGGER.info("Versões coletadas");
      jar.close();
    } else {
      final URL url = getClass().getResource("/" + path);
      if (url != null) {
        try {
          final File sqls = new File(url.toURI());
          File[] files = sqls.listFiles();
          if (files == null) {
            LOGGER.error("`db_updates` está vazia");
            return;
          }
          for (File file : files) {
            prepareFile(path + "/" + file.getName());
          }
          LOGGER.info("Versões coletadas");
        } catch (URISyntaxException ignored) {
        }
      }
    }
  }

  private void prepareFile(String filePath) {
    Matcher m = filepattern.matcher(filePath);
    if (!m.find()) {
      return;
    }
    int fromVersion = Integer.parseInt(m.group(1));
    int toVersion = Integer.parseInt(m.group(2));
    versionMap.put(fromVersion, new DbVersion(toVersion, filePath));
    highestVersion = Math.max(highestVersion, toVersion);
  }

  public boolean updateToCurrent() throws SQLException {
    LOGGER.info("Verificando se há atualizações");
    int currentVersion = 0;

    try {
      currentVersion = getCurrentVersion();
    } catch (SQLException ignored) {
    }
    if (currentVersion == highestVersion) {
      LOGGER.info("O banco de dados está atualizado");
      return true;
    }
    SQLFileRunner runner = new SQLFileRunner(dbHandler.getConnection(), true, true);
    boolean hasUpgrade = versionMap.containsKey(currentVersion);
    while (hasUpgrade) {
      DbVersion version = versionMap.get(currentVersion);
      System.out.println(version.file);
      try (
          InputStreamReader reader = new InputStreamReader(
              getClass().getClassLoader().getResourceAsStream(version.file));
          BufferedReader br = new BufferedReader(reader)) {
        runner.runScript(br);
      } catch (IOException e) {
        e.printStackTrace();
      }
      currentVersion = version.toVersion;
      saveDbVersion(currentVersion);
      hasUpgrade = versionMap.containsKey(currentVersion);
    }

    LOGGER.info("Banco de dados atualizado para a versão mais recente");
    return true;
  }

  private int getCurrentVersion() throws SQLException {
    DatabaseMetaData metaData = dbHandler.getConnection().getMetaData();
    int dbVersion = 0;

    try (ResultSet rs = metaData.getTables(null, null, "bot_meta", null)) {
      if (!rs.next()) {
        return dbVersion;
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    try (ResultSet rs = dbHandler.select("SELECT * FROM bot_meta WHERE meta_name = ?", "db_version")) {
      if (rs.next()) {
        dbVersion = Integer.parseInt(rs.getString("meta_value"));
      }
      rs.getStatement().close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    return dbVersion;
  }

  private void saveDbVersion(int version) throws SQLException {
    if (version < 1) {
      return;
    }
    dbHandler.insert(
        "INSERT INTO bot_meta(meta_name, meta_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE meta_value = ? ",
        "db_version", version, version);
  }

  private class DbVersion {
    final int toVersion;
    final String file;

    private DbVersion(int toVersion, String filePath) {
      this.toVersion = toVersion;
      this.file = filePath;
    }
  }
}