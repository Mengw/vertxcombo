package com.m3958.vertxio.vertxcombo;


import java.nio.file.Path;
import java.util.Arrays;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

public class CachedBufferAsync {

  private Path comboDiskRootPath;

  private Vertx vertx;

  private VersionedFile[] files;

  private Handler<AsyncResult<Void>> doneCb;

  private ConcurrentSharedMap<String, Buffer> fbuffers;

  private boolean[] results;

  private int remaining;

  private int length;

  public CachedBufferAsync(Vertx vertx, Handler<AsyncResult<Void>> doneCb, Path comboDiskRootPath,
      VersionedFile... infiles) {
    this.vertx = vertx;
    this.doneCb = doneCb;
    this.setComboDiskRootPath(comboDiskRootPath);
    this.files = infiles;
    this.length = infiles.length;
    this.remaining = this.length;
    this.results = new boolean[this.length];
    Arrays.fill(this.results, false);
    this.fbuffers = vertx.sharedData().getMap(ComboHandlerVerticle.VERSIONED_FILE_MAP_NAME);
  }

  public void startRead() {
    if (this.files.length == 0) {
      doneCb.handle(new VoidAsyncResult(false));
      return;
    }
    for (int i = 0; i < length; i++) {
      readOne(i);
    }
  }

  private void allDone(int i, boolean success) {
    this.remaining = this.remaining - 1;
    this.results[i] = true;
    if (this.remaining == 0) {
      boolean allSuccess = true;
      for (boolean ob : this.results) {
        if (!ob) {
          allSuccess = false;
          break;
        }
      }
      doneCb.handle(new VoidAsyncResult(allSuccess));
    }
  }

  private void readOne(final int i) {
    final VersionedFile vf = this.files[i];
    if (fbuffers.containsKey(vf.toString())) {
      allDone(i, true);
      return;
    }

    Path infPath = this.getComboDiskRootPath().resolve(vf.getFile());

    vertx.fileSystem().readFile(infPath.toString(), new AsyncResultHandler<Buffer>() {
      public void handle(AsyncResult<Buffer> ar) {
        if (ar.succeeded()) {
          fbuffers.put(vf.toString(), ar.result());
          CachedBufferAsync.this.remaining = CachedBufferAsync.this.remaining - 1;
          allDone(i, true);
        } else {
          allDone(i, false);
        }
      }
    });
  }

  public Path getComboDiskRootPath() {
    return comboDiskRootPath;
  }

  public void setComboDiskRootPath(Path comboDiskRootPath) {
    this.comboDiskRootPath = comboDiskRootPath;
  }

}
