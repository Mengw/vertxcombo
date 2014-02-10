package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;

public class SingleFileProcessor {

  private ExtractFileResult efr;
  
  private Logger log;
  
  private JsonObject config;
  
  private HttpServerRequest req;

  public SingleFileProcessor(ExtractFileResult efr, Logger log, JsonObject config,
      HttpServerRequest req) {
    super();
    this.efr = efr;
    this.log = log;
    this.config = config;
    this.req = req;
  }
  
  public void process(){
    VersionedFile vf = efr.getFiles()[0];
    Path p = efr.getComboDiskRootPath().resolve(vf.getFile());
    req.response().sendFile(p.toString());
  }

  public ExtractFileResult getEfr() {
    return efr;
  }

  public void setEfr(ExtractFileResult efr) {
    this.efr = efr;
  }

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

  public JsonObject getConfig() {
    return config;
  }

  public void setConfig(JsonObject config) {
    this.config = config;
  }

  public HttpServerRequest getReq() {
    return req;
  }

  public void setReq(HttpServerRequest req) {
    this.req = req;
  }
  
  
}
