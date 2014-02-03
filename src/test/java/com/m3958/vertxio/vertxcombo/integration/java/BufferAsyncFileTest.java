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

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.CachedBufferSync;
import com.m3958.vertxio.vertxcombo.ComboHandlerVerticle;
import com.m3958.vertxio.vertxcombo.ExtractFileResult;
import com.m3958.vertxio.vertxcombo.MinifyStyleUrl;
import com.m3958.vertxio.vertxcombo.UrlStyle;
import com.m3958.vertxio.vertxcombo.VersionedFile;


/**
 * Example Java integration test that deploys the module that this project builds.
 * 
 * Quite often in integration tests you want to deploy the same module for all tests and you don't
 * want tests to start before the module has been deployed.
 * 
 * This test demonstrates how to do that.
 */
public class BufferAsyncFileTest extends TestVerticle {

  @Test
  public void testBuffer() {
    final Logger log = container.logger();
    Path comboDiskRootPath = Paths.get("c:/staticyui");
    UrlStyle us = new MinifyStyleUrl(container.logger(), comboDiskRootPath);

    final ExtractFileResult efr = us.extractFiles(us.generateRandomUrl("*.js", 10));

    new CachedBufferSync(vertx, new Handler<AsyncResult<Buffer[]>>() {
      @Override
      public void handle(AsyncResult<Buffer[]> event) {
        if (event.succeeded()) {
          ConcurrentSharedMap<String, Buffer> fbuffers =
              vertx.sharedData().getMap(ComboHandlerVerticle.VERSIONED_FILE_MAP_NAME);
          assertEquals(10, fbuffers.size());
          for (VersionedFile p : efr.getFiles()) {
            assertTrue(fbuffers.containsKey(p.toString()));
            log.info(p.toString());
          }
        } else {
          assertTrue(false);
        }
        testComplete();
      }
    }, comboDiskRootPath, efr.getFiles()).startRead();
  }

  @Override
  public void start() {
    initialize();
    startTests();
  }

}
