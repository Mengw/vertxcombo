/*
 * @author jianglibo@gmail.com
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MainVerticleSource extends Verticle {
  
  public static String VERSIONED_FILE_MAP_NAME = "combo-name-buffer";
  public static String CFG_COMBO_DISK_ROOT = "comboDiskRoot";
  public static String CFG_SYNC_READ = "syncRead";
  public static String CFG_MAX_MEM = "maxMem";
  public static String CFG_DEFAULT_MAXAGE = "defaultMaxAge";
  public static String CFG_VERSIONED_MAXAGE = "versionedMaxAge";
  public static String LISTEN_PORT = "listenPort";

  public void start() {

    JsonObject config =
        new JsonObject().putString(CFG_COMBO_DISK_ROOT, "c:/staticyui")
            .putBoolean(CFG_SYNC_READ, false)
            .putNumber(CFG_MAX_MEM, 64 * 1024 * 1024)
            .putNumber(LISTEN_PORT, 8093);


    container.deployVerticle("com.m3958.vertxio.vertxcombo.ComboHandlerVerticle", config, 3,
        new AsyncResultHandler<String>() {

          @Override
          public void handle(AsyncResult<String> asyncResult) {
            if (asyncResult.succeeded()) {
              System.out.println("The MainVerticleSource has been deployed, deployment ID is "
                  + asyncResult.result());
            } else {
              asyncResult.cause().printStackTrace();
            }
          }
        });

    container.deployVerticle("com.m3958.vertxio.vertxcombo.MonitorVerticle", config, 1);
  }
}
