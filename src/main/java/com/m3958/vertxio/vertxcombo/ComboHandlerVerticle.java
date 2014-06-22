package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Handler;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.m3958.vertxio.vertxcombo.UrlStyle.Style;

public class ComboHandlerVerticle extends Verticle {

  public void start() {

    final JsonObject config = container.config();

    final FileSystem fileSystem = vertx.fileSystem();

    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

      public void handle(final HttpServerRequest req) {

        final HttpServerResponse resp = req.response();
        String uri = req.uri();
        System.out.println(uri);
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

        Style urlStyle = Style.SINGLE_FILE;
        if (uri.startsWith("/min")) {
          urlStyle = Style.PHP_MINIFY;
        } else if (uri.startsWith("/combo")) {
          urlStyle = Style.YUI_COMBO;
        }

        ExtractFileResult efr = null;
        UrlStyle us = null;
        switch (urlStyle) {
          case PHP_MINIFY:
            us = new MinifyStyleUrl(fileSystem, container.logger(), comboDiskRootPath.normalize());
            efr = us.extractFiles(uri);
            break;
          case YUI_COMBO:
            us = new YuiStyleUrl(fileSystem, container.logger(), comboDiskRootPath.normalize());
            efr = us.extractFiles(uri);
            break;
          case SINGLE_FILE:
            us = new SingleFileUrl(fileSystem, container.logger(), comboDiskRootPath);
            efr = us.extractFiles(uri);
            break;
          default:
            break;
        }

        final ExtractFileResult fefr = efr;

        switch (fefr.getStatus()) {
          case SUCCESS:
            new MutiFileSender(efr, fileSystem, container.logger(), req, config).send();
            break;
          default:
            System.out.println("start default");
            resp.setStatusCode(HttpStatus.SC_NOT_FOUND);
            resp.setStatusMessage(efr.getStatus().toString());
            resp.end();
            break;
        }
      }
    }).listen(config.getInteger(MainVerticle.CFGKEY_LISTEN_PORT));
  }
}
