package com.m3958.vertxio.vertxcombo;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
This is monitor verticle.
 */
public class MonitorVerticle extends Verticle {
  
  public static String BUFFER_COUNT_ADDRESS = "buffer_count_address";
  
  private long bufferSizeDelta;
  
  private int bufNumbers;

  public void start() {
    final Logger log = container.logger();
    
    vertx.eventBus().registerHandler(BUFFER_COUNT_ADDRESS, new Handler<Message<Integer>>() {
      @Override
      public void handle(Message<Integer> message) {
        bufferSizeDelta += message.body();
        bufNumbers++;
        
        log.info("size changed:" + bufferSizeDelta);
        log.info("buffer number changed:" + bufNumbers);
      }
    });
    log.info("MonitorVerticle started");
  }
}
