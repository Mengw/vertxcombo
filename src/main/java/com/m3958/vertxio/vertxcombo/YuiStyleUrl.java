package com.m3958.vertxio.vertxcombo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.vertx.java.core.logging.Logger;

public class YuiStyleUrl extends UrlStyle {

  // http://yui.yahooapis.com/combo?3.14.1/event-mouseenter/event-mouseenter-min.js&3.14.1/event-hover/event-hover-min.js
  private Logger logger;

  private Path comboDiskRootPath;

  public YuiStyleUrl(Logger logger, Path comboDiskRootPath) {
    this.logger = logger;
    this.comboDiskRootPath = comboDiskRootPath;
  }

  public YuiStyleUrl(Logger logger, String comboDiskRootPath) {
    this.logger = logger;
    this.comboDiskRootPath = Paths.get(comboDiskRootPath);
  }

  @Override
  public ExtractFileResult extractFiles(String url) {
    url = sanitizeUrl(url);
    int qidx = url.indexOf('?');
    // /combo/version?
    String version = url.substring(0, qidx);
    String fnPart = url.substring(qidx + 1);
    if (fnPart.startsWith("&")) {
      fnPart = fnPart.substring(1);
    }
    String[] fns = fnPart.split("&");

    char fsep = File.separatorChar;
    char unwantedFsep = fsep == '/' ? '\\' : '/';

    Path[] sanitizedPathes = new Path[fns.length];

    FN_LOOP: for (int i = 0; i < fns.length; i++) {
      String fn = fns[i];
      if (fn.isEmpty()) {
        continue FN_LOOP;
      }
      fn = fn.replace(unwantedFsep, fsep);
      if (fn.charAt(0) == fsep) {
        fn = fn.substring(1);
      }
      sanitizedPathes[i] = Paths.get(fn);
    }

    for (Path fp : sanitizedPathes) {
      if (!comboDiskRootPath.resolve(fp).startsWith(comboDiskRootPath)) {
        return new ExtractFileResult(ExtractFileResult.ResultStatus.FILE_NOT_FOUND);
      }
    }
    return new ExtractFileResult(sanitizedPathes, version, url).setMimeType();
  }

  @Override
  public String generateRandomUrl(String pattern, int number, String version) {
    RandomFileFinder rff = new RandomFileFinder(comboDiskRootPath, pattern, number);
    StringBuilder sb;
    if (version == null || version.isEmpty()) {
      sb = new StringBuilder("/combo?");
    } else {
      sb = new StringBuilder("/combo/").append(version).append("?");
    }
    try {
      List<Path> selected = rff.selectSome();
      for (Path p : selected) {
        sb.append(p.toString().replace('\\', '/')).append('&');
      }
      if (sb.charAt(sb.length() - 1) == '&') {
        sb = sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
