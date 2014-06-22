package com.m3958.vertxio.vertxcombo.integration.java;

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.ExtractFileResult;
import com.m3958.vertxio.vertxcombo.MainVerticle;
import com.m3958.vertxio.vertxcombo.MinifyStyleUrl;
import com.m3958.vertxio.vertxcombo.UrlStyle;
import com.m3958.vertxio.vertxcombo.YuiStyleUrl;
import com.m3958.vertxio.vertxcombo.utils.Utils;


/*
 * 
 * @author jianglibo@gmail.com
 */
public class UrlStyleTest extends TestVerticle {

  @Test
  public void testMimeUtil() {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Path p =
        Paths.get(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).resolve(
            "horizontal-menu-submenu-indicator.png");

    TikaConfig tconfig = TikaConfig.getDefaultConfig();
    Detector detector = tconfig.getDetector();

    try {
//      TikaInputStream stream = TikaInputStream.get(p.toFile());
      Metadata metadata = new Metadata();
      metadata.add(Metadata.RESOURCE_NAME_KEY, p.toString());
      MediaType mediaType = detector.detect(null, metadata);
      Assert.assertEquals("image/png", mediaType.toString());
      System.out.println(mediaType);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    testComplete();
  }

  @Test
  public void testOnefile() {
    String url = "/ff.js?123";
    int qidx = url.indexOf('?');
    String version = url.substring(qidx + 1);
    String onefn = url.substring(0, qidx);

    Assert.assertEquals("/ff.js", onefn);
    Assert.assertEquals("123", version);
    testComplete();
  }

  @Test
  public void testOnefile1() {
    String url = "/ff.js?";
    int qidx = url.indexOf('?');
    String version = url.substring(qidx + 1);
    String onefn = url.substring(0, qidx);

    Assert.assertEquals("/ff.js", onefn);
    Assert.assertEquals("", version);
    testComplete();
  }

  @Test
  public void testSubUrl() {
    String url = "/combo?abc/ff.js";
    int idx = url.indexOf('?');
    Assert.assertEquals("abc/ff.js", url.substring(idx + 1));
    testComplete();
  }

  @Test
  public void testSubUrl1() {
    String url = "/combo?";
    int idx = url.indexOf('?');
    Assert.assertEquals("", url.substring(idx + 1));
    testComplete();
  }

  @Test
  public void testSubUrl2() {
    String url = "/combo?";
    url = url.substring(0, url.length() - 1);
    Assert.assertEquals("/combo", url);
    testComplete();
  }

  @Test
  public void testSubUrl3() {
    String url = "/?";
    url = url.substring(0, url.length() - 1);
    Assert.assertEquals("/", url);
    testComplete();
  }

  @Test
  public void testCurDir() {
    System.out.println(System.getProperty("user.dir"));
    testComplete();
  }

  @Test
  public void testVersion() {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    MinifyStyleUrl ms =
        new MinifyStyleUrl(vertx.fileSystem(), null,
            config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT));
    ExtractFileResult efr = ms.extractFiles("/min/?&5566&f=/3.13.0/build/a.js");
    Assert.assertEquals("5566", efr.getVersion());
    testComplete();
  }

  @Test
  public void testUrl() {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    MinifyStyleUrl ms =
        new MinifyStyleUrl(vertx.fileSystem(), null,
            config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT));
    ExtractFileResult efr =
        ms.extractFiles("/min/b=3.13.0/build&130727&f=/a.css,/b.css?");
    Assert.assertEquals("130727", efr.getVersion());
    Assert.assertEquals(2, efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert
        .assertEquals(createMimeType(config, ExtractFileResult.MIME_TYPES_CSS), efr.getMimeType());
    testComplete();
  }

  @Test
  public void testUrl1() {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());

    MinifyStyleUrl ms =
        new MinifyStyleUrl(vertx.fileSystem(), container.logger(),
            config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT));
    ExtractFileResult efr =
        ms.extractFiles("/min/f=a.css,b.css,c.css?");
    Assert.assertEquals("", efr.getVersion());
    printMe(efr.getVersion());
    Assert.assertEquals(3, efr.getFiles().length);
    printMe(efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert
        .assertEquals(createMimeType(config, ExtractFileResult.MIME_TYPES_CSS), efr.getMimeType());
    testComplete();
  }

  @Test
  public void testUrl2() {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    UrlStyle ms =
        new YuiStyleUrl(vertx.fileSystem(), null,
            config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT));
    ExtractFileResult efr =
        ms.extractFiles("/combo/130727?a.js&b.js&c.js");
    Assert.assertEquals("/combo/130727", efr.getVersion());
    printMe(efr.getVersion());
    Assert.assertEquals(3, efr.getFiles().length);
    printMe(efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert.assertEquals(createMimeType(config, ExtractFileResult.MIME_TYPES_JS), efr.getMimeType());
    testComplete();
  }

  /**
   * append a & before query string.
   */
  @Test
  public void testUrl3() {
    JsonObject config = new JsonObject(Utils.readResouce("/conf.json"));
    Assume.assumeTrue(new File(config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT)).exists());
    UrlStyle ms =
        new YuiStyleUrl(vertx.fileSystem(), null,
            config.getString(MainVerticle.CFGKEY_COMBO_DISK_ROOT));
    ExtractFileResult efr =
        ms.extractFiles("/combo/130727?&a.js&b.js&c.js");
    Assert.assertEquals("/combo/130727", efr.getVersion());
    printMe(efr.getVersion());
    Assert.assertEquals(3, efr.getFiles().length);
    printMe(efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert.assertEquals(createMimeType(config, ExtractFileResult.MIME_TYPES_JS), efr.getMimeType());
    testComplete();
  }

  private String createMimeType(JsonObject config, String mimePrefix) {
    return mimePrefix + "; charset=" + config.getString(MainVerticle.CFGKEY_CHARSET);
  }

  private void printMe(Object o) {
    if (o == null) {
      System.out.println("null");
    } else {
      System.out.println(o.toString());
    }
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
