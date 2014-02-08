package com.m3958.vertxio.vertxcombo;

/*
 * 
 * @author jianglibo@gmail.com
 */

import java.util.ArrayDeque;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
 * This is monitor verticle.
 */
public class MonitorVerticle extends Verticle {

  public static String BUFFER_COUNT_ADDRESS = "buffer_count_address";

  private ArrayDeque<VersionedFile> vfArray = new ArrayDeque<>();

  private long totalSize = 0;

  public void start() {
    final Logger log = container.logger();
    JsonObject config = container.config();
    final long maxMem = config.getLong(MainVerticle.CFGKEY_MAX_MEM, 64 * 1024 * 1024);

    vertx.eventBus().registerHandler(BUFFER_COUNT_ADDRESS, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        VersionedFile vf = VersionedFile.fromJson(message.body());
        vfArray.add(vf);
        totalSize += vf.getLength();
        while (totalSize > maxMem) {
          VersionedFile rvf = vfArray.removeFirst();
          totalSize -= rvf.getLength();
          log.info("maxMem reached");
          log.info(rvf.toString() + " is removed from cache.length: " + rvf.getLength());
        }
      }
    });
    log.info("MonitorVerticle started");
  }
}
