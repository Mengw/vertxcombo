package com.m3958.vertxio.vertxcombo.integration.java;

/*
 * 
 * @author jianglibo@gmail.com
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;

import java.io.File;

import org.junit.Assume;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.logging.Logger;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.MainVerticle;
import com.m3958.vertxio.vertxcombo.SingleFileUrl;
import com.m3958.vertxio.vertxcombo.UrlStyle;

/**
 */
public class SingleFileIntegrationTest extends TestVerticle {

    @Test
    public void testImg() {
      Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
      HttpClient client =
          vertx.createHttpClient().setPort(MainVerticle.CFGVALUE_LISTEN_PORT).setHost("localhost")
              .setMaxPoolSize(10);
  
      final Logger log = container.logger();
  
      String url =
          "/3.12.0/build/node-menunav/assets/skins/night/horizontal-menu-submenu-indicator.png";
      log.info("start test url: " + url);
      client.getNow(url, new SingleFileComboResponseHandler(container));
    }
    
    @Test
    public void testVersionedImg() {
      Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
      HttpClient client =
          vertx.createHttpClient().setPort(MainVerticle.CFGVALUE_LISTEN_PORT).setHost("localhost")
              .setMaxPoolSize(10);
  
      final Logger log = container.logger();
  
      String url =
          "/3.12.0/build/node-menunav/assets/skins/night/horizontal-menu-submenu-indicator.png?abcd";
      log.info("start test url: " + url);
      client.getNow(url, new SingleFileComboResponseHandler(container));
    }
  

  @Test
  public void testSingleFielComboHandler() {
    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
    HttpClient client =
        vertx.createHttpClient().setPort(MainVerticle.CFGVALUE_LISTEN_PORT).setHost("localhost")
            .setMaxPoolSize(10);

    final Logger log = container.logger();

    UrlStyle msu = new SingleFileUrl(log, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
    String url = msu.generateRandomUrl("*.js", 1, "789");
    log.info("start test url: " + url);
    client.getNow(url, new SingleFileComboResponseHandler(container));
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
