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

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.streams.Pump;
import org.vertx.testtools.TestVerticle;

import com.m3958.vertxio.vertxcombo.ChainedFileHandler;


/**
 * Example Java integration test that deploys the module that this project builds.
 * 
 * Quite often in integration tests you want to deploy the same module for all tests and you don't
 * want tests to start before the module has been deployed.
 * 
 * This test demonstrates how to do that.
 */
public class AsyncFileTest extends TestVerticle {

  @Test
  public void testAsyncFile() {
    final Logger log = container.logger();
    vertx.fileSystem().open("some-file.dat", new AsyncResultHandler<AsyncFile>() {
      public void handle(AsyncResult<AsyncFile> ar) {
        if (ar.succeeded()) {
          final AsyncFile asyncFile = ar.result();
          Buffer buff = new Buffer(1000);
          for (int i = 0; i < 10; i++) {
            asyncFile.read(buff, i * 100, i * 100, 100, new AsyncResultHandler<Buffer>() {
              public void handle(AsyncResult<Buffer> ar) {
                if (ar.succeeded()) {} else {
                  log.error("Failed to write", ar.cause());
                }
                testComplete();
              }
            });
          }
          asyncFile.endHandler(new VoidHandler() {

            @Override
            protected void handle() {
              asyncFile.close();

            }
          });
        } else {
          log.error("Failed to open file", ar.cause());
          testComplete();
        }
      }
    });
  }

  @Test
  public void testReadStream() {
    final Logger log = container.logger();

    vertx.fileSystem().open("some-file.dat", new AsyncResultHandler<AsyncFile>() {
      public void handle(final AsyncResult<AsyncFile> ar) {
        if (ar.succeeded()) {
          vertx.fileSystem().open("other-file.dat", null, false, true, true, true,
              new AsyncResultHandler<AsyncFile>() {

                @Override
                public void handle(final AsyncResult<AsyncFile> arout) {

                  if (arout.succeeded()) {
                    final AsyncFile infile = ar.result();
                    final AsyncFile outfile = arout.result();

                    final Pump pump = Pump.createPump(infile, outfile).start();

                    infile.endHandler(new VoidHandler() {
                      @Override
                      protected void handle() {

                        outfile.flush(new Handler<AsyncResult<Void>>() {
                          @Override
                          public void handle(AsyncResult<Void> event) {
                            log.info(pump.bytesPumped());
                            log.info(event);
                            log.info(event.result());
                            outfile.close();
                            infile.close();
                            testComplete();
                          }
                        });

                      }
                    });
                  } else {
                    log.error("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                  }
                }

              });
        } else {
          log.error("Failed to open file", ar.cause());
          testComplete();
        }
      }
    });
  }

  @Test
  public void testSomethingElse() {
    // Whatever
    testComplete();
  }


  @Test
  public void testOther() {
    ChainedFileHandler cfh =
        new ChainedFileHandler(vertx, "combined.txt", "a.txt", "b.txt", "c.txt");

    cfh.pump();

    cfh.setAfterComplete(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> event) {

        vertx.fileSystem().readFile("combined.txt", new AsyncResultHandler<Buffer>() {
          public void handle(AsyncResult<Buffer> ar) {
            if (ar.succeeded()) {
              Buffer bf = ar.result();
              String s = new String(bf.getBytes());

              assertEquals(s, "123");

              testComplete();
            } else {}
          }
        });
      }
    });

  }

  @Test
  public void testOther1() {
    ChainedFileHandler cfh =
        new ChainedFileHandler(vertx, "combined1.txt", "c.txt", "b.txt", "a.txt");

    cfh.pump();

    cfh.setAfterComplete(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> event) {
        vertx.fileSystem().readFile("combined1.txt", new AsyncResultHandler<Buffer>() {
          public void handle(AsyncResult<Buffer> ar) {
            if (ar.succeeded()) {
              Buffer bf = ar.result();
              String s = new String(bf.getBytes());

              assertEquals(s, "321");

              testComplete();
            } else {
              assertTrue(false);
            }
          }
        });
      }
    });

  }

  @Override
  public void start() {
    initialize();
    startTests();
  }

}
