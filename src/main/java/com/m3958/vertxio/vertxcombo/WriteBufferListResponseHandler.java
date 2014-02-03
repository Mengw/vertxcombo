package com.m3958.vertxio.vertxcombo;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

public class WriteBufferListResponseHandler implements Handler<AsyncResult<Buffer[]>>{

  private HttpServerResponse resp;
  
  private ExtractFileResult fefr;
  
  public WriteBufferListResponseHandler(HttpServerRequest req,ExtractFileResult fefr) {
    this.resp = req.response();
    this.fefr = fefr;
  }
  
  
  @Override
  public void handle(AsyncResult<Buffer[]> event) {
    if (event.succeeded()) {
      resp.headers().set("Content-Type", fefr.getMimeType() + "; charset=UTF-8");
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
