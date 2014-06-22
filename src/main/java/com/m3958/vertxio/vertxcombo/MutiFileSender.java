package com.m3958.vertxio.vertxcombo;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.streams.Pump;

public class MutiFileSender {
  private ExtractFileResult fefr;

  private FileSystem fileSystem;

  private Logger logger;

  private HttpServerResponse resp;

  long defaultMaxAge;

  long versionedMaxAge;

  public MutiFileSender(ExtractFileResult fefr, FileSystem fileSystem, Logger logger,
      HttpServerRequest req, JsonObject config) {
    super();
    this.fefr = fefr;
    this.fileSystem = fileSystem;
    this.logger = logger;
    this.resp = req.response();
    this.defaultMaxAge = config.getLong(MainVerticle.CFGKEY_DEFAULT_MAXAGE, 600);
    this.versionedMaxAge = config.getLong(MainVerticle.CFGKEY_VERSIONED_MAXAGE, 31536000);
  }

  public void send() {
    long now = System.currentTimeMillis();
    resp.headers().set("Content-Type", fefr.getMimeType());
    if (fefr.getVersion() == null || fefr.getVersion().isEmpty()) {
      resp.headers().set("Cache-Control", "public,max-age=" + defaultMaxAge);
      resp.headers().set("Expires", String.valueOf(now + defaultMaxAge * 1000));
    } else {
      String etag = DigestUtils.md5Hex(fefr.getUrl());
      resp.headers().set("Etag", etag);
      resp.headers().set("Cache-Control", "public,max-age=" + versionedMaxAge);
      resp.headers().set("Expires", String.valueOf(now + versionedMaxAge * 1000));
    }
    resp.setChunked(true);
    sendOneFile();
  }

  /*
   * if fefr has no file,resp end.
   */
  private void sendOneFile() {
    final VersionedFile vf;
    if ((vf = fefr.getNext()) != null) {
      fileSystem.open(vf.getFile().toString(), new AsyncResultHandler<AsyncFile>() {

        @Override
        public void handle(AsyncResult<AsyncFile> ar) {
          if (ar.succeeded()) {
            AsyncFile asyncFile = ar.result();
            Pump.createPump(asyncFile, resp).start();
            asyncFile.endHandler(new VoidHandler() {
              public void handle() {
                if (fefr.isLast()) {
                  resp.end();
                } else {
                  sendOneFile();
                }
              }
            });
          } else {
            logger.error(vf.getFile().toString() + "doesn't exist.");
            resp.end();
          }
        }
      });
    }
  }
}
