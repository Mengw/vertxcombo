package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

public class CachedBufferSync {

  private Path comboDiskRootPath;

  private Vertx vertx;

  private VersionedFile[] files;

  private Handler<AsyncResult<Void>> doneCb;

  private ConcurrentSharedMap<String, Buffer> fbuffers;

  public CachedBufferSync(Vertx vertx, Handler<AsyncResult<Void>> doneCb, Path comboDiskRootPath,
      VersionedFile... infiles) {
    this.vertx = vertx;
    this.doneCb = doneCb;
    this.setComboDiskRootPath(comboDiskRootPath);
    this.files = infiles;
    this.fbuffers = vertx.sharedData().getMap(ComboHandlerVerticle.VERSIONED_FILE_MAP_NAME);
  }


  public void startRead() {
    if (this.files.length == 0) {
      doneCb.handle(new VoidAsyncResult(false));
      return;
    }

    boolean success;
    for (VersionedFile vf : files) {
      success = readOne(vf);
      if (!success) {
        doneCb.handle(new VoidAsyncResult(false));
        return;
      }
    }
    doneCb.handle(new VoidAsyncResult(true));
  }

  private boolean readOne(VersionedFile vf) {
    if (fbuffers.containsKey(vf.toString())) {
      return true;
    }
    Path infPath = this.getComboDiskRootPath().resolve(vf.getFile());
    Buffer bf = null;
    try {
      bf = vertx.fileSystem().readFileSync(infPath.toString());
    } catch (Exception e) {

    }
    if (bf == null) {
      return false;
    } else {
      fbuffers.put(vf.toString(), bf);
      return true;
    }
  }

  public Path getComboDiskRootPath() {
    return comboDiskRootPath;
  }


  public void setComboDiskRootPath(Path comboDiskRootPath) {
    this.comboDiskRootPath = comboDiskRootPath;
  }
}
