package com.m3958.vertxio.vertxcombo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.logging.Logger;

public class SingleFileUrl implements UrlStyle {

  // http://yui.yahooapis.com/combo?3.14.1/event-mouseenter/event-mouseenter-min.js&3.14.1/event-hover/event-hover-min.js
  private Logger logger;

  private Path comboDiskRootPath;

  public SingleFileUrl(Logger logger, Path comboDiskRootPath) {
    this.logger = logger;
    this.comboDiskRootPath = comboDiskRootPath;
  }

  public SingleFileUrl(Logger logger, String comboDiskRootPath) {
    this.logger = logger;
    this.comboDiskRootPath = Paths.get(comboDiskRootPath);
  }

  @Override
  public ExtractFileResult extractFiles(String url) {
    // /build/yui/yui-min.js?5566
    int qidx = url.indexOf('?');

    String onefn;
    String version = "";
    if (qidx == -1) {
      onefn = url;
    } else {
      version = url.substring(qidx + 1);
      onefn = url.substring(0, qidx);
    }

    String[] fns = new String[] {onefn};

    char fsep = File.separatorChar;
    char unwantedFsep = fsep == '/' ? '\\' : '/';

    Path[] sanitizedPathes = new Path[fns.length];

    for (int i = 0; i < fns.length; i++) {
      String fn = fns[i];
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
    RandomFileFinder rff = new RandomFileFinder(comboDiskRootPath, pattern, 1);
    try {
      Path selected = rff.selectSome().get(0);
      String url = selected.toString().replace('\\', '/');
      if (version == null || version.isEmpty()) {

      } else {
        url = url + "?" + version;
      }
      return url;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
