package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

public abstract class CachedBufferBase {

  protected Path comboDiskRootPath;

  protected Vertx vertx;

  protected VersionedFile[] files;

  protected Handler<AsyncResult<Buffer[]>> doneCb;

  protected ConcurrentSharedMap<String, Buffer> fbufferMap;

  protected Buffer[] buffers;

  protected int remaining;

  protected EventBus eb;

  public CachedBufferBase(Vertx vertx, Handler<AsyncResult<Buffer[]>> doneCb,
      Path comboDiskRootPath, VersionedFile... infiles) {
    this.vertx = vertx;
    this.doneCb = doneCb;
    this.comboDiskRootPath = comboDiskRootPath;
    this.files = infiles;
    this.buffers = new Buffer[infiles.length];
    this.remaining = infiles.length;
    this.fbufferMap = vertx.sharedData().getMap(MainVerticle.VERSIONED_FILE_MAP_NAME);
    this.eb = vertx.eventBus();
  }

  protected void allDone(int i, Buffer bf) {
    this.remaining = this.remaining - 1;
    this.buffers[i] = bf;
    if (this.remaining == 0) {
      boolean allSuccess = true;
      for (Buffer one : this.buffers) {
        if (one == null) {
          allSuccess = false;
          break;
        }
      }
      doneCb.handle(new BufferListAsyncResult(allSuccess, this.buffers));
    }
  }

  public void startRead() {
    int length = this.files.length;
    if (length == 0) {
      doneCb.handle(new BufferListAsyncResult(false, this.buffers));
      return;
    }
    for (int i = 0; i < length; i++) {
      readOne(i);
    }
  }

  protected abstract void readOne(int i);

}
