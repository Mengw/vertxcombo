package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class ComboHandlerVerticle extends Verticle {


  public void start() {

    final JsonObject config = container.config();

    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

      public void handle(final HttpServerRequest req) {

        final HttpServerResponse resp = req.response();
        String uri = req.uri();

        String comboDiskRoot = config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT, "");
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

        String urlStyle = "";
        if (uri.startsWith("/min")) {
          urlStyle = "phpMinify";
        } else if (uri.startsWith("/combo")) {
          urlStyle = "yuiCombo";
        } else {
          urlStyle = "singleFile";
        }

        boolean syncRead = config.getBoolean(MainVerticle.CFGKEY_SYNC_READ, false);

        ExtractFileResult efr = null;
        UrlStyle us = null;
        if ("phpMinify".equals(urlStyle)) {
          us = new MinifyStyleUrl(container.logger(), comboDiskRootPath.normalize());
          efr = us.extractFiles(uri);
        } else if ("yuiCombo".equals(urlStyle)) {
          us = new YuiStyleUrl(container.logger(), comboDiskRootPath.normalize());
          efr = us.extractFiles(uri);
        } else if ("singleFile".equals(urlStyle)) {
          us = new SingleFileUrl(container.logger(), comboDiskRootPath);
          efr = us.extractFiles(uri);
        } else {
          resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
          resp.setStatusMessage("unrecogonized url style.");
          resp.end();
          return;
        }

        final ExtractFileResult fefr = efr;

        switch (fefr.getStatus()) {
          case SUCCESS:
            if (syncRead) {
              new CachedBufferSync(vertx, new WriteBufferListResponseHandler(req, config, fefr),
                  comboDiskRootPath, efr.getFiles()).startRead();
            } else {
              new CachedBufferAsync(vertx, new WriteBufferListResponseHandler(req, config, fefr),
                  comboDiskRootPath, efr.getFiles()).startRead();
            }

            break;
          default:
            resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
            resp.setStatusMessage(efr.getStatus().toString());
            resp.end();
            break;
        }
      }
    }).listen(config.getInteger(MainVerticle.CFGKEY_LISTEN_PORT));
  }
}
