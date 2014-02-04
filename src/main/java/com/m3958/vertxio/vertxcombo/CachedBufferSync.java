package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;

public class CachedBufferSync extends CachedBufferBase {


  public CachedBufferSync(Vertx vertx, Handler<AsyncResult<Buffer[]>> doneCb,
      Path comboDiskRootPath, VersionedFile[] infiles) {
    super(vertx, doneCb, comboDiskRootPath, infiles);
  }

  @Override
  protected void readOne(int i) {
    VersionedFile vf = this.files[i];
    if (fbufferMap.containsKey(vf.toString())) {
      allDone(i, fbufferMap.get(vf.toString()));
      return;
    }
    Path infPath = this.comboDiskRootPath.resolve(vf.getFile());
    Buffer bf = null;
    try {
      bf = vertx.fileSystem().readFileSync(infPath.toString());
    } catch (Exception e) {

    }
    if (bf == null) {
      allDone(i, bf);
    } else {
      fbufferMap.put(vf.toString(), bf);
      eb.send(MonitorVerticle.BUFFER_COUNT_ADDRESS, vf.setLength(bf.length()).toJson());
      allDone(i, bf);
    }
  }
}
