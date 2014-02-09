package com.m3958.vertxio.vertxcombo.unit;

import java.io.File;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

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
    Assert.assertEquals(ExtractFileResult.MIME_TYPES_CSS, efr.getMimeType());
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
    Assert.assertEquals(ExtractFileResult.MIME_TYPES_CSS, efr.getMimeType());
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
    Assert.assertEquals(ExtractFileResult.MIME_TYPES_JS, efr.getMimeType());
  }



  private void printMe(Object o) {
    if (o == null) {
      System.out.println("null");
    } else {
      System.out.println(o.toString());
    }

  }
}
