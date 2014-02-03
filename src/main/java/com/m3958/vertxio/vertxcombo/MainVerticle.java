package com.m3958.vertxio.vertxcombo;

/*
 * Copyright 2013 Red Hat, Inc.
 * 
 * Red Hat licenses this file to you under the Apache License, version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/*
 * This is a simple Java *source* verticle which receives `ping` messages on the event bus and sends
 * back `pong` replies.
 * 
 * Note that we don't precompile this - Vert.x can do this on the fly when it's run
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MainVerticle extends Verticle {

  public void start() {

    JsonObject config =
        new JsonObject().putString("comboDiskRoot", "c:/staticyui").putBoolean("syncRead", false);


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
    
    container.deployVerticle("com.m3958.vertxio.vertxcombo.MonitorVerticle",1);
  }
}
