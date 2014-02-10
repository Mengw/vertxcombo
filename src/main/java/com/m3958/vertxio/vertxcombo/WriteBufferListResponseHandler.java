package com.m3958.vertxio.vertxcombo;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;

public class WriteBufferListResponseHandler implements Handler<AsyncResult<Buffer[]>> {

  private HttpServerResponse resp;

  private ExtractFileResult fefr;

  long defaultMaxAge;
  long versionedMaxAge;


  public WriteBufferListResponseHandler(HttpServerRequest req, JsonObject config,
      ExtractFileResult fefr) {
    this.resp = req.response();
    this.defaultMaxAge = config.getLong(MainVerticle.CFGKEY_DEFAULT_MAXAGE, 600);
    this.versionedMaxAge = config.getLong(MainVerticle.CFGKEY_VERSIONED_MAXAGE, 31536000);
    this.fefr = fefr;
  }


  @Override
  public void handle(AsyncResult<Buffer[]> event) {
    if (event.succeeded()) {
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

      long bufLength = 0;
      for (Buffer bf : event.result()) {
        resp.write(bf);
        bufLength += bf.length();
      }
      resp.putTrailer("total-send-bytes", String.valueOf(bufLength));
      resp.end();
    } else {
      resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
      resp.setStatusMessage(fefr.getStatus().toString());
      resp.end();
    }
  }

}
