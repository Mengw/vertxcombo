package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.logging.Logger;


public abstract class UrlStyle {

  public static enum Style {
    PHP_MINIFY, YUI_COMBO, SINGLE_FILE
  }

  protected Logger logger;

  protected Path comboDiskRootPath;

  protected FileSystem fileSystem;

  public UrlStyle(FileSystem fileSystem, Logger logger, Path comboDiskRootPath) {
    this.logger = logger;
    this.fileSystem = fileSystem;
    this.comboDiskRootPath = comboDiskRootPath;
  }

  public UrlStyle(FileSystem fileSystem, Logger logger, String comboDiskRootPath) {
    this.logger = logger;
    this.fileSystem = fileSystem;
    this.comboDiskRootPath = Paths.get(comboDiskRootPath);
  }


  protected String sanitizeUrl(String originUrl) {
    if (originUrl.endsWith("?")) {
      return originUrl.substring(0, originUrl.length() - 1);
    } else {
      return originUrl;
    }
  }

  /**
   * must test file exists! if found one none exist file, return quickly.
   */

  protected boolean testExists(Path[] pathes) {
    if (pathes == null || pathes.length == 0) {
      return false;
    }
    for (Path p : pathes) {
      String fn = p.toString();
      if(!fileSystem.existsSync(fn) || fileSystem.propsSync(fn).isDirectory()){
        return false;
      }
    }
    return true;
  }

  public abstract ExtractFileResult extractFiles(String url);

  public abstract String generateRandomUrl(String pattern, int number, String version);

}
