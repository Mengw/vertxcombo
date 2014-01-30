package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;


public class ExtractFileResult {

  public static String MIME_TYPES_JS = "application/javascript";
  public static String MIME_TYPES_CSS = "text/css";
  public static String MIME_TYPES_JSON = "application/json";
  public static String MIME_TYPES_UNKNOWN = "unknown";

  public static enum ResultStatus {
    DISK_ROOT_ERROR, COMBO_BASE_ERROR,
      FILE_NOT_FOUND, URL_PATTERN_ERROR, SUCCESS,UNKNOWN_MIMETYPE
  }
  

  private VersionedFile[] files;

  private String etag;

  private ResultStatus status;
  
  private String mimeType;

  private String version;

  public ExtractFileResult(VersionedFile[] files,String version) {
    this.setFiles(files);
    this.setStatus(ResultStatus.SUCCESS);
  }
  
  public ExtractFileResult(Path[] paths,String version) {
    this.files = new VersionedFile[paths.length];
    for(int idx=0;idx<paths.length;idx++){
      this.files[idx] = new VersionedFile(paths[idx], version);
    }
    this.setStatus(ResultStatus.SUCCESS);
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
    if(getFiles() == null || getFiles().length == 0){
      setStatus(ResultStatus.FILE_NOT_FOUND);
    }else{
      String fn = getFiles()[0].getFile().getFileName().toString();
      int idx = fn.lastIndexOf('.');
      String ext = idx == -1 ? "" : fn.substring(idx);
      if(".js".equalsIgnoreCase(ext)){
        this.mimeType = MIME_TYPES_JS;
      }else if(".css".equalsIgnoreCase(ext)){
        this.mimeType = MIME_TYPES_CSS;
      }else if(".json".equalsIgnoreCase(MIME_TYPES_JSON)){
        this.mimeType = MIME_TYPES_JSON;
      }else{
        this.mimeType = MIME_TYPES_UNKNOWN;
        setStatus(ResultStatus.UNKNOWN_MIMETYPE);
      }
    }
    return this;
  }
  
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
