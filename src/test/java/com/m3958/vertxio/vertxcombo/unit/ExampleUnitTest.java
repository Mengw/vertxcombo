package com.m3958.vertxio.vertxcombo.unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.vertxio.vertxcombo.ExtractFileResult;
import com.m3958.vertxio.vertxcombo.MainVerticle;
import com.m3958.vertxio.vertxcombo.MinifyStyleUrl;
import com.m3958.vertxio.vertxcombo.UrlStyle;
import com.m3958.vertxio.vertxcombo.YuiStyleUrl;


/*
 * 
 * @author jianglibo@gmail.com
 */
public class ExampleUnitTest {
  
  @Test
  public void configTest(){
    JsonObject config =
        new JsonObject().putNumber("instance", 10);
    
    JsonObject config1 =
        new JsonObject().putNumber("instance", 6);

    config1.mergeIn(config);
    
    Assert.assertEquals(10, config1.getInteger("instance") + 0);
    
  }


  @Test
  public void testMimeUtil() {
    Path p =
        Paths.get(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).resolve(
            "3.12.0/build/node-menunav/assets/skins/night/horizontal-menu-submenu-indicator.png");

    TikaConfig config = TikaConfig.getDefaultConfig();
    Detector detector = config.getDetector();

    try {
      TikaInputStream stream = TikaInputStream.get(p.toFile());
      Metadata metadata = new Metadata();
      metadata.add(Metadata.RESOURCE_NAME_KEY, p.toString());
      MediaType mediaType = detector.detect(stream, metadata);
      Assert.assertEquals("image/png", mediaType.toString());
      System.out.println(mediaType);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  // @Test
  // public void testFileTypeDetector() {
  // FileTypeDetector fileTypeDetector = DefaultFileTypeDetector.create();
  // System.out.println("DefaultFileTypeDetector class : "
  // + fileTypeDetector.getClass().getCanonicalName());
  // }

  //  @Test
  //  public void testMimeType() {
  //    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
  //    Path p =
  //        Paths.get(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).resolve(
  //            "3.12.0/build/node-menunav/assets/skins/night/horizontal-menu-submenu-indicator.png");
  //    try {
  //      String mimeType = Files.probeContentType(p);
  //      Assert.assertEquals("image/png", mimeType);
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //    }
  //  }

  @Test
  public void testOnefile() {
    String url = "/ff.js?123";
    int qidx = url.indexOf('?');
    String version = url.substring(qidx + 1);
    String onefn = url.substring(0, qidx);

    Assert.assertEquals("/ff.js", onefn);
    Assert.assertEquals("123", version);
  }

  @Test
  public void testOnefile1() {
    String url = "/ff.js?";
    int qidx = url.indexOf('?');
    String version = url.substring(qidx + 1);
    String onefn = url.substring(0, qidx);

    Assert.assertEquals("/ff.js", onefn);
    Assert.assertEquals("", version);
  }

  @Test
  public void testSubUrl() {
    String url = "/combo?abc/ff.js";
    int idx = url.indexOf('?');
    Assert.assertEquals("abc/ff.js", url.substring(idx + 1));
  }

  @Test
  public void testSubUrl1() {
    String url = "/combo?";
    int idx = url.indexOf('?');
    Assert.assertEquals("", url.substring(idx + 1));
  }

  @Test
  public void testSubUrl2() {
    String url = "/combo?";
    url = url.substring(0, url.length() - 1);
    Assert.assertEquals("/combo", url);
  }

  @Test
  public void testSubUrl3() {
    String url = "/?";
    url = url.substring(0, url.length() - 1);
    Assert.assertEquals("/", url);
  }

  @Test
  public void testCurDir() {
    System.out.println(System.getProperty("user.dir"));
  }

  @Test
  public void testVersion() {
    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
    MinifyStyleUrl ms = new MinifyStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
    ExtractFileResult efr = ms.extractFiles("/min/?&5566&f=/3.13.0/build/yui/yui-min.js");
    Assert.assertEquals("5566", efr.getVersion());
  }

  @Test
  public void testUrl() {
    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
    MinifyStyleUrl ms = new MinifyStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
    ExtractFileResult efr =
        ms.extractFiles("/min/b=3.13.0/build&130727&f=/cssgrids/cssgrids-min.css,/cssnormalize-context/cssnormalize-context-min.css?");
    Assert.assertEquals("130727", efr.getVersion());
    Assert.assertEquals(2, efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert.assertEquals(createMimeType(ExtractFileResult.MIME_TYPES_CSS), efr.getMimeType());
  }

  @Test
  public void testUrl1() {
    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());

    MinifyStyleUrl ms = new MinifyStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
    ExtractFileResult efr =
        ms.extractFiles("/min/f=3.12.0/build/cssreset/cssreset.css,3.12.0/build/cssfonts/cssfonts.css,3.12.0/build/cssgrids/cssgrids.css?");
    Assert.assertEquals("", efr.getVersion());
    printMe(efr.getVersion());
    Assert.assertEquals(3, efr.getFiles().length);
    printMe(efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert.assertEquals(createMimeType(ExtractFileResult.MIME_TYPES_CSS), efr.getMimeType());
  }

  @Test
  public void testUrl2() {
    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
    UrlStyle ms = new YuiStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
    ExtractFileResult efr =
        ms.extractFiles("/combo/130727?3.12.0/build/yui-base/yui-base-min.js&3.12.0/build/loader-base/loader-base-min.js&3.12.0/build/loader-yui3/loader-yui3-min.js");
    Assert.assertEquals("/combo/130727", efr.getVersion());
    printMe(efr.getVersion());
    Assert.assertEquals(3, efr.getFiles().length);
    printMe(efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert.assertEquals(createMimeType(ExtractFileResult.MIME_TYPES_JS), efr.getMimeType());
  }

  /**
   * append a & before query string.
   */
  @Test
  public void testUrl3() {
    Assume.assumeTrue(new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists());
    UrlStyle ms = new YuiStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
    ExtractFileResult efr =
        ms.extractFiles("/combo/130727?&3.12.0/build/yui-base/yui-base-min.js&3.12.0/build/loader-base/loader-base-min.js&3.12.0/build/loader-yui3/loader-yui3-min.js");
    Assert.assertEquals("/combo/130727", efr.getVersion());
    printMe(efr.getVersion());
    Assert.assertEquals(3, efr.getFiles().length);
    printMe(efr.getFiles().length);
    Assert.assertEquals(ExtractFileResult.ResultStatus.SUCCESS, efr.getStatus());
    Assert.assertEquals(createMimeType(ExtractFileResult.MIME_TYPES_JS), efr.getMimeType());
  }

  private String createMimeType(String mimePrefix) {
    return mimePrefix + "; charset=" + MainVerticle.CFGVALUE_CHARSET;
  }

  private void printMe(Object o) {
    if (o == null) {
      System.out.println("null");
    } else {
      System.out.println(o.toString());
    }

  }
}
