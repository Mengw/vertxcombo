package com.m3958.vertxio.vertxcombo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.vertx.java.core.logging.Logger;

public class MinifyStyleUrl implements UrlStyle {

  // http://yuimin.fh.gov.cn/min/f=/pure/0.2.0/build/pure-min.css,/neverchange/bootstrap/2.3.2/css/bootstrap.min.css&5566
  // http://yuimin.fh.gov.cn/min/b=3.13.0/build&130727&f=/cssgrids/cssgrids-min.css,/cssnormalize-context/cssnormalize-context-min.css
  private Logger logger;

  private Path comboDiskRootPath;

  public MinifyStyleUrl(Logger logger, Path comboDiskRootPath) {
    this.logger = logger;
    this.comboDiskRootPath = comboDiskRootPath;
  }

  public MinifyStyleUrl(Logger logger, String comboDiskRootPath) {
    this.logger = logger;
    this.comboDiskRootPath = Paths.get(comboDiskRootPath);
  }

  @Override
  public ExtractFileResult extractFiles(String url) {
    String[] segs = url.split("&");
    String b = null;
    String f = null;
    String version = null;
    char fsep = File.separatorChar;
    char unwantedFsep = fsep == '/' ? '\\' : '/';

    for (String seg : segs) {
      if (seg.startsWith("/min/b=")) {
        b = seg;
      } else if (seg.startsWith("/min/f=")) {
        f = seg;
      } else if (seg.startsWith("f=")) {
        f = seg;
      } else {
        version = seg;
      }
    }

    if (f == null) {
      return new ExtractFileResult(ExtractFileResult.ResultStatus.URL_PATTERN_ERROR);
    }

    if (version == null) {
      version = "1";
    }

    int start = f.startsWith("f=") ? 2 : 7;

    String[] fns = f.substring(start).split(",");

    Path[] sanitizedPathes = new Path[fns.length];

    for (int i = 0; i < fns.length; i++) {
      String fn = fns[i];
      fn = fn.replace(unwantedFsep, fsep);
      if (fn.charAt(0) == fsep) {
        fn = fn.substring(1);
      }
      sanitizedPathes[i] = Paths.get(fn);
    }

    Path[] afterBaseAppend = null;

    if (b != null) {
      b = b.split("=")[1];
      b = b.replace(unwantedFsep, fsep);
      if (b.charAt(0) == fsep) {
        b = b.substring(1);
      }
      Path bp = Paths.get(b).normalize();
      afterBaseAppend = new Path[sanitizedPathes.length];
      for (int i = 0; i < sanitizedPathes.length; i++) {
        Path fp = bp.resolve(sanitizedPathes[i]).normalize();
        afterBaseAppend[i] = fp;
      }
    }

    if (afterBaseAppend == null) {
      afterBaseAppend = sanitizedPathes;
    }

    for (Path fp : afterBaseAppend) {
      if (!comboDiskRootPath.resolve(fp).startsWith(comboDiskRootPath)) {
        return new ExtractFileResult(ExtractFileResult.ResultStatus.FILE_NOT_FOUND);
      }
    }

    return new ExtractFileResult(afterBaseAppend, version).setMimeType();
  }

  @Override
  public String generateRandomUrl(String pattern, int number) {
    RandomFileFinder rff = new RandomFileFinder(comboDiskRootPath, pattern, number);
    StringBuilder sb = new StringBuilder("/min/f=");
    try {
      List<Path> selected = rff.selectSome();
      for (Path p : selected) {
        sb.append(p.toString().replace('\\', '/')).append(',');
      }
      if (sb.charAt(sb.length() - 1) == ',') {
        sb = sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
