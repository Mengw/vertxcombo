package com.m3958.vertxio.vertxcombo.integration.java;

import static org.vertx.testtools.VertxAssert.testComplete;

import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

public class SingleFileComboResponseHandler implements Handler<HttpClientResponse> {
  private Logger log;

  public SingleFileComboResponseHandler(Container container) {
    this.log = container.logger();
  }

  @Override
  public void handle(HttpClientResponse resp) {
    log.info("Got a response: " + resp.statusCode());
    log.info("Got a message: " + resp.statusMessage());
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> header : resp.headers().entries()) {
      sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
    }
    log.info("Headers: " + sb);

    final HttpClientResponse respf = resp;

    resp.endHandler(new VoidHandler() {
      public void handle() {
        // The entire response body has been received
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : respf.trailers().entries()) {
          sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
        }
        log.info("Trailers: " + sb);
        testComplete();
      }
    });
  }
}
