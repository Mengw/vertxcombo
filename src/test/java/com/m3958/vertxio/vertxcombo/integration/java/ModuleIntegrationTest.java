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

import org.junit.Assert;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.logging.Logger;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.MainVerticle;
import com.m3958.vertxio.vertxcombo.MinifyStyleUrl;
import com.m3958.vertxio.vertxcombo.SingleFileUrl;
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

  public static String comboRoot = "c:/staticyui";

  private boolean skipTest() {
    if (vertx.fileSystem().existsSync(comboRoot)) {

      return false;
    } else {
      Assert.assertTrue(true);
      testComplete();
      return true;
    }
  }

  @Test
  public void testMinifyComboHandler() {
    if (skipTest()) return;
    HttpClient client =
        vertx.createHttpClient().setPort(MainVerticle.LISTEN_PORT).setHost("localhost")
            .setMaxPoolSize(10);

    final Logger log = container.logger();

    MinifyStyleUrl msu = new MinifyStyleUrl(log, comboRoot);
    String url = msu.generateRandomUrl("*.js", 10, "345");
    log.info(url);
    client.getNow(url, new TestComboResponseHandler(container));
  }

  @Test
  public void testYuiComboHandler() {
    if (skipTest()) return;
    HttpClient client =
        vertx.createHttpClient().setPort(MainVerticle.LISTEN_PORT).setHost("localhost")
            .setMaxPoolSize(10);

    final Logger log = container.logger();

    YuiStyleUrl msu = new YuiStyleUrl(log, comboRoot);
    String url = msu.generateRandomUrl("*.js", 10, "345");
    log.info(url);
    client.getNow(url, new TestComboResponseHandler(container));
  }

  @Test
  public void testSingleFielComboHandler() {
    if (skipTest()) return;
    HttpClient client =
        vertx.createHttpClient().setPort(MainVerticle.LISTEN_PORT).setHost("localhost")
            .setMaxPoolSize(10);

    final Logger log = container.logger();

    SingleFileUrl msu = new SingleFileUrl(log, comboRoot);
    String url = msu.generateRandomUrl("*.js", 10, "345");
    log.info(url);
    client.getNow(url, new TestComboResponseHandler(container));
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
