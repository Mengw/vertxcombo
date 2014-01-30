package com.m3958.vertxio.vertxcombo.integration.java;

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
 * 
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.MinifyStyleUrl;
import com.m3958.vertxio.vertxcombo.YuiStyleUrl;

/**
 * Example Java integration test that deploys the module that this project builds.
 * 
 * Quite often in integration tests you want to deploy the same module for all tests and you don't
 * want tests to start before the module has been deployed.
 * 
 * This test demonstrates how to do that.
 */
public class ModuleIntegrationTest extends TestVerticle {
  
  public static class ComboResponseHandler implements Handler<HttpClientResponse>{
    
    private Logger log;
    private Container container;
    
    public ComboResponseHandler(Container container){
      this.container = container;
      this.log = container.logger();
    }
    
    public void handle(HttpClientResponse resp) {
      log.info("Got a response: " + resp.statusCode());
      log.info("Got a message: " + resp.statusMessage());
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, String> header : resp.headers().entries()) {
        sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
      }
      log.info("Headers: " + sb);

      final Buffer body = new Buffer(0);
      final HttpClientResponse respf = resp;
      resp.dataHandler(new Handler<Buffer>() {
        public void handle(Buffer data) {
          body.appendBuffer(data);
        }
      });
      resp.endHandler(new VoidHandler() {
        public void handle() {
          // The entire response body has been received
          log.info("The total body received was " + body.length() + " bytes");
          StringBuilder sb = new StringBuilder();
          for (Map.Entry<String, String> header : respf.trailers().entries()) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
          }
          log.info("Trailers: " + sb);
          Assert.assertEquals(String.valueOf(body.length()),
              respf.trailers().get("total-send-bytes"));
          testComplete();
        }
      });
    }
  }

  @Test
  public void testMinifyComboHandler() {
    HttpClient client =
        vertx.createHttpClient().setPort(8080).setHost("localhost").setMaxPoolSize(10);

    final Logger log = container.logger();

    MinifyStyleUrl msu = new MinifyStyleUrl(log, "c:/staticyui");
    String url = msu.generateRandomUrl("*.js", 10);
    log.info(url);
    client.getNow(url, new ComboResponseHandler(container));
  }
  
  @Test
  public void testYuiComboHandler() {
    HttpClient client =
        vertx.createHttpClient().setPort(8080).setHost("localhost").setMaxPoolSize(10);

    final Logger log = container.logger();

    YuiStyleUrl msu = new YuiStyleUrl(log, "c:/staticyui");
    String url = msu.generateRandomUrl("*.js", 10);
    log.info(url);
    client.getNow(url, new ComboResponseHandler(container));
  }

  @Test
  public void testSomethingElse() {
    // Whatever
    testComplete();
  }

  @Override
  public void start() {
    // Make sure we call initialize() - this sets up the assert stuff so
    // assert functionality works correctly
    initialize();
    // Deploy the module - the System property `vertx.modulename` will
    // contain the name of the module so you
    // don't have to hardecode it in your tests
    container.logger().info(System.getProperty("vertx.modulename"));
    container.deployModule(System.getProperty("vertx.modulename"),
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> asyncResult) {
            // Deployment is asynchronous and this this handler will
            // be called when it's complete (or failed)
            assertTrue(asyncResult.succeeded());
            assertNotNull("deploymentID should not be null", asyncResult.result());
            // If deployed correctly then start the tests!
            startTests();
          }
        });
  }

}
