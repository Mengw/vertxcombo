package com.m3958.vertxio.vertxcombo.unit;

import java.io.File;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import com.m3958.vertxio.vertxcombo.ExtractFileResult;
import com.m3958.vertxio.vertxcombo.MainVerticle;
import com.m3958.vertxio.vertxcombo.MinifyStyleUrl;

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
    if (new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists()) {
      MinifyStyleUrl ms = new MinifyStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
      ExtractFileResult efr =
          ms.extractFiles("http://yuimin1.fh.gov.cn/min/?&5566&f=/3.13.0/build/yui/yui-min.js");
      Assert.assertEquals("5566", efr.getVersion());
    }
  }

  @Test
  public void testVersion1() {
    if (new File(MainVerticle.CFGVALUE_COMBO_DISK_ROOT).exists()) {
      MinifyStyleUrl ms = new MinifyStyleUrl(null, MainVerticle.CFGVALUE_COMBO_DISK_ROOT);
      ExtractFileResult efr =
          ms.extractFiles("http://yuimin.fh.gov.cn/min/b=3.13.0/build&130727&f=/cssgrids/cssgrids-min.css,/cssnormalize-context/cssnormalize-context-min.css");
      Assert.assertEquals("130727", efr.getVersion());
    }
  }

}
