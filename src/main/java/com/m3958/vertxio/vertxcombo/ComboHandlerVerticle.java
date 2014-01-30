package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.platform.Verticle;

public class ComboHandlerVerticle extends Verticle {

  public static String sharedMapName = "combo-name-buffer";

  // http://yuimin.fh.gov.cn/min/f=/pure/0.2.0/build/pure-min.css,/neverchange/bootstrap/2.3.2/css/bootstrap.min.css&5566
  // http://yuimin.fh.gov.cn/min/b=3.13.0/build&130727&f=/cssgrids/cssgrids-min.css,/cssnormalize-context/cssnormalize-context-min.css

  public void start() {



    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {

        final HttpServerResponse resp = req.response();
        String uri = req.uri();
        JsonObject config = container.config();

        String comboDiskRoot = config.getString("comboDiskRoot", "");
        Path comboDiskRootPath = null;

        if (!comboDiskRoot.isEmpty()) {
          comboDiskRootPath = Paths.get(comboDiskRoot);
          if (!comboDiskRootPath.toFile().exists()) {
            comboDiskRootPath = null;
          }
        }

        if (comboDiskRootPath == null) {
          resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
          resp.setStatusMessage("combo disk root error.");
          resp.end();
          return;
        }

        String urlStyle = config.getString("urlStyle", "");

        if (urlStyle.isEmpty()) {
          if(uri.startsWith("/min")){
            urlStyle = "phpMinify";
          }else if(uri.startsWith("/combo")){
            urlStyle = "yuiCombo";
          }
        }
        
        if(urlStyle.isEmpty()){
          urlStyle = "phpMinify";
        }

        ExtractFileResult efr = null;

        if ("phpMinify".equals(urlStyle)) {
          UrlStyle us = new MinifyStyleUrl(container.logger(), comboDiskRootPath.normalize());
          efr = us.extractFiles(uri);
        }else if("yuiCombo".equals(urlStyle)){
          UrlStyle us = new YuiStyleUrl(container.logger(), comboDiskRootPath.normalize());
          efr = us.extractFiles(uri);
        } else {
          resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
          resp.setStatusMessage("unrecogonized url style。");
          resp.end();
          return;
        }

        final ExtractFileResult fefr = efr;

        switch (fefr.getStatus()) {
          case SUCCESS:
            new CachedBuffer(vertx, new Handler<AsyncResult<Void>>() {
              @Override
              public void handle(AsyncResult<Void> event) {
                if (event.succeeded()) {
                  resp.headers().set("Content-Type", fefr.getMimeType() + "; charset=UTF-8");
                  resp.setChunked(true);
                  ConcurrentSharedMap<String, Buffer> fbuffers =
                      vertx.sharedData().getMap(ComboHandlerVerticle.sharedMapName);
                  long bufLength = 0;
                  for (VersionedFile fp : fefr.getFiles()) {
                    Buffer bf = fbuffers.get(fp.toString());
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
            }, comboDiskRootPath, efr.getFiles()).pump();
            break;
          default:
            resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
            resp.setStatusMessage(efr.getStatus().toString());
            resp.end();
            break;
        }
      }
    }).listen(8080);
  }
}
