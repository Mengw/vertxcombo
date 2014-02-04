package com.m3958.vertxio.vertxcombo;


import java.nio.file.Path;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;

public class CachedBufferAsync extends CachedBufferBase{

  public CachedBufferAsync(Vertx vertx, Handler<AsyncResult<Buffer[]>> doneCb, Path comboDiskRootPath,
      VersionedFile[] infiles) {
    super(vertx, doneCb, comboDiskRootPath, infiles);
  }

  @Override
  protected void readOne(final int i) {
    final VersionedFile vf = this.files[i];
    if (fbufferMap.containsKey(vf.toString())) {
      allDone(i, fbufferMap.get(vf.toString()));
      return;
    }

    Path infPath = this.comboDiskRootPath.resolve(vf.getFile());

    vertx.fileSystem().readFile(infPath.toString(), new AsyncResultHandler<Buffer>() {
      public void handle(AsyncResult<Buffer> ar) {
        if (ar.succeeded()) {
          Buffer bf = ar.result();
          fbufferMap.put(vf.toString(), bf);
          eb.send(MonitorVerticle.BUFFER_COUNT_ADDRESS, vf.setLength(bf.length()).toJson());
          allDone(i, bf);
        } else {
          allDone(i, null);
        }
      }
    });
  }

}
