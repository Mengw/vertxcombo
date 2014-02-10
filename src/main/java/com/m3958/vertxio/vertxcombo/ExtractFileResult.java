package com.m3958.vertxio.vertxcombo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;


public class ExtractFileResult {

  public static String MIME_TYPES_JS = "application/javascript";
  public static String MIME_TYPES_CSS = "text/css";
  public static String MIME_TYPES_JSON = "application/json";
  public static String MIME_TYPES_UNKNOWN = "unknown";

  public static enum ResultStatus {
    DISK_ROOT_ERROR, COMBO_BASE_ERROR, FILE_NOT_FOUND, URL_PATTERN_ERROR, SUCCESS, UNKNOWN_MIMETYPE
  }


  private VersionedFile[] files;

  private String etag;

  private ResultStatus status;

  private String mimeType;

  private String version;

  private String url;

  private Path comboDiskRootPath;

  public ExtractFileResult(Path comboDiskRootPath, VersionedFile[] files, String version, String url) {
    this.setFiles(files);
    this.setStatus(ResultStatus.SUCCESS);
    this.setUrl(url);
    this.setVersion(version);
    this.comboDiskRootPath = comboDiskRootPath;
  }

  public ExtractFileResult(Path comboDiskRootPath, Path[] paths, String version, String url) {
    this.files = new VersionedFile[paths.length];
    for (int idx = 0; idx < paths.length; idx++) {
      this.files[idx] = new VersionedFile(paths[idx], version);
    }
    this.setStatus(ResultStatus.SUCCESS);
    this.setVersion(version);
    this.setUrl(url);
    this.comboDiskRootPath = comboDiskRootPath;
  }

  public ExtractFileResult(ResultStatus status) {
    this.setStatus(status);
  }

  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }


  public VersionedFile[] getFiles() {
    return files;
  }

  public void setFiles(VersionedFile[] files) {
    this.files = files;
  }

  public ResultStatus getStatus() {
    return status;
  }

  public void setStatus(ResultStatus status) {
    this.status = status;
  }

  public String getMimeType() {
    return mimeType;
  }

  public ExtractFileResult setMimeType() {
    if (getFiles() == null || getFiles().length == 0) {
      setStatus(ResultStatus.FILE_NOT_FOUND);
    } else {
      String fn = getFiles()[0].getFile().getFileName().toString();
      this.mimeType = probeMimeType(fn);
    }
    if (MIME_TYPES_UNKNOWN.equals(this.mimeType)) {
      setStatus(ResultStatus.UNKNOWN_MIMETYPE);
    }

    return this;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getVersion() {
    if (version == null) {
      return "";
    }
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  private String probeMimeType(String fn) {
    int idx = fn.lastIndexOf('.');
    String mimeType = null;
    String ext = idx == -1 ? "" : fn.substring(idx);
    if (".js".equalsIgnoreCase(ext)) {
      mimeType = MIME_TYPES_JS + "; charset=UTF-8";
    } else if (".css".equalsIgnoreCase(ext)) {
      mimeType = MIME_TYPES_CSS + "; charset=UTF-8";
    } else if (".json".equalsIgnoreCase(MIME_TYPES_JSON)) {
      mimeType = MIME_TYPES_JSON + "; charset=UTF-8";
    } else {
      mimeType = tikaProbe(comboDiskRootPath.resolve(getFiles()[0].getFile()));
    }
    if (mimeType == null) {
      mimeType = MIME_TYPES_UNKNOWN;
    }
    return mimeType;
  }

  private String tikaProbe(Path p) {
    TikaConfig config = TikaConfig.getDefaultConfig();
    Detector detector = config.getDetector();

    try {
      TikaInputStream stream = TikaInputStream.get(p.toFile());
      Metadata metadata = new Metadata();
      metadata.add(Metadata.RESOURCE_NAME_KEY, p.toString());
      MediaType mediaType = detector.detect(stream, metadata);
      return mediaType.toString();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
