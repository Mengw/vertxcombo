package com.m3958.vertxio.vertxcombo;

/*
 * @author jianglibo@gmail.com
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.m3958.vertxio.vertxcombo.utils.Utils;


public class MainVerticle extends Verticle {

  public static String VERSIONED_FILE_MAP_NAME = "combo-name-buffer";
  public static String CFGKEY_COMBO_DISK_ROOT = "comboDiskRoot";
  public static String CFGKEY_SYNC_READ = "syncRead";
  public static String CFGKEY_MAX_MEM = "maxMem";
  public static String CFGKEY_DEFAULT_MAXAGE = "defaultMaxAge";
  public static String CFGKEY_VERSIONED_MAXAGE = "versionedMaxAge";
  public static String CFGKEY_LISTEN_PORT = "listenPort";
  public static String CFGKEY_INSTANCES = "instances";
  public static String CFGKEY_CHARSET = "charset";

  // public static int CFGVALUE_LISTEN_PORT = 8094;
  // public static String CFGVALUE_COMBO_DISK_ROOT = File.separatorChar == '/'
  // ? "/opt/staticyui"
  // : "c:/staticyui";
  // public static long CFGVALUE_MAX_MEM = 64 * 1024 * 1024;
  // public static int CFGVALUE_INSTANCES = 2;
  // public static String CFGVALUE_CHARSET = "UTF-8";

  public void start() {
    // JsonObject configc = container.config();

    Logger log = container.logger();

    JsonObject config = container.config();

    if (config.toMap().isEmpty()) {
      config = new JsonObject(Utils.readResouce("/conf.json"));
    }

    log.info("final config: ");
    log.info(config.toString());

    container.deployVerticle(ComboHandlerVerticle.class.getName(), config,
        config.getInteger(CFGKEY_INSTANCES), new AsyncResultHandler<String>() {

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

    container.deployVerticle("com.m3958.vertxio.vertxcombo.MonitorVerticle", config,
        config.getInteger(CFGKEY_INSTANCES));
  }
}
