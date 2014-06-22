package com.m3958.vertxio.vertxcombo.integration.java;

/*
 * 
 * @author jianglibo@gmail.com
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.MainVerticle;
import com.m3958.vertxio.vertxcombo.utils.Utils;

/**
 */
public class SingleFileIntegrationTest extends TestVerticle {

  @Test
  public void testImg() throws ClientProtocolException, IOException {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));

    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());

    StringBuilder sb = com.m3958.vertxio.vertxcombo.integration.java.Utils.getUrlBeforPath(config);

    sb.append("/horizontal-menu-submenu-indicator.png");

    HttpResponse res = Request.Get(sb.toString()).execute().returnResponse();
    HttpEntity he = res.getEntity();
    com.m3958.vertxio.vertxcombo.integration.java.Utils.checkHeaders(res);
    Assert.assertEquals("image/png", he.getContentType().getValue());
    Assert.assertEquals(157, he.getContentLength());
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
