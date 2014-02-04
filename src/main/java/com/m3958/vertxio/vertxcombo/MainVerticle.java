package com.m3958.vertxio.vertxcombo;

/*
 * @author jianglibo@gmail.com
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MainVerticle extends Verticle {

  public void start() {

    JsonObject config =
        new JsonObject().putString(ComboHandlerVerticle.CFG_COMBO_DISK_ROOT, "c:/staticyui")
            .putBoolean(ComboHandlerVerticle.CFG_SYNC_READ, false)
            .putNumber(ComboHandlerVerticle.CFG_MAX_MEM, 64 * 1024 * 1024);


    container.deployVerticle("com.m3958.vertxio.vertxcombo.ComboHandlerVerticle", config, 3,
        new AsyncResultHandler<String>() {

          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              System.out.println("The verticle has been deployed, deployment ID is "
                  + asyncResult.result());
            } else {
              asyncResult.cause().printStackTrace();
            }
          }
        });

    container.deployVerticle("com.m3958.vertxio.vertxcombo.MonitorVerticle", config, 1);
  }
}
