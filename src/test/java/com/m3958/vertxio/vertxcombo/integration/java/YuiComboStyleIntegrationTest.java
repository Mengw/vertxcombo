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
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
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
 * yui combo always has long expire.
 */
public class YuiComboStyleIntegrationTest extends TestVerticle {


  @Test
  public void testYuiComboHandler10() throws ClientProtocolException, IOException,
      URISyntaxException {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    StringBuilder sb = com.m3958.vertxio.vertxcombo.integration.java.Utils.getUrlBeforPath(config);
    sb.append("/combo?a.js&b.js");
    
    HttpResponse res = Request.Get(sb.toString()).execute().returnResponse();
    com.m3958.vertxio.vertxcombo.integration.java.Utils.checkHeaders(res);
    HttpEntity entity = res.getEntity();
    Assert.assertEquals("ab", EntityUtils.toString(entity));
    Assert.assertEquals("application/javascript; charset=UTF-8", entity.getContentType().getValue());
    Assert.assertEquals("public,max-age=31536000", res.getLastHeader("Cache-Control").getValue());
    testComplete();
  }
  
  @Test
  public void testYuiComboHandler1() throws ClientProtocolException, IOException,
      URISyntaxException {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    StringBuilder sb = com.m3958.vertxio.vertxcombo.integration.java.Utils.getUrlBeforPath(config);
    sb.append("/combo/5566?a.js&b.js");
    
    HttpResponse res = Request.Get(sb.toString()).execute().returnResponse();
    com.m3958.vertxio.vertxcombo.integration.java.Utils.checkHeaders(res);
    HttpEntity entity = res.getEntity();
    Assert.assertEquals("ab", EntityUtils.toString(entity));
    Assert.assertEquals("application/javascript; charset=UTF-8", entity.getContentType().getValue());
    Assert.assertEquals("public,max-age=31536000", res.getLastHeader("Cache-Control").getValue());
    testComplete();
  }
  
  @Test
  public void testYuiComboHandler2() throws ClientProtocolException, IOException,
      URISyntaxException {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    StringBuilder sb = com.m3958.vertxio.vertxcombo.integration.java.Utils.getUrlBeforPath(config);
    sb.append("/combo/5566?a.css&b.css");
    
    HttpResponse res = Request.Get(sb.toString()).execute().returnResponse();
    com.m3958.vertxio.vertxcombo.integration.java.Utils.checkHeaders(res);
    HttpEntity entity = res.getEntity();
    Assert.assertEquals("ab", EntityUtils.toString(entity));
    Assert.assertEquals("text/css; charset=UTF-8", entity.getContentType().getValue());
    Assert.assertEquals("public,max-age=31536000", res.getLastHeader("Cache-Control").getValue());
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
