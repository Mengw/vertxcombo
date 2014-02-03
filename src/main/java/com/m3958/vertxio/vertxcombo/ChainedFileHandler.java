package com.m3958.vertxio.vertxcombo;


import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.streams.Pump;

public class ChainedFileHandler {

  private int currentIndex = 0;

  private String[] infiles;

  private String outfile;

  private Vertx vertx;

  private Handler<AsyncResult<Void>> afterComplete = null;

  public ChainedFileHandler(Vertx vertx, String outfile, String... infiles) {
    this.outfile = outfile;
    this.infiles = infiles;
    this.vertx = vertx;
  }

  public void pump() {
    vertx.fileSystem().open(this.outfile, null, false, true, true, true,
        new AsyncResultHandler<AsyncFile>() {
          @Override
          public void handle(AsyncResult<AsyncFile> event) {
            if (event.succeeded()) {
              AsyncFile out = event.result();
              pumpOne(out);
            } else {

            }
          }
        });
  }

  private String nextfile() {
    if (this.infiles.length > currentIndex) {
      return this.infiles[currentIndex++];
    } else {
      return null;
    }
  }


  private void pumpOne(final AsyncFile out) {
    String infstr = nextfile();
    if (infstr == null) {
      out.flush(new Handler<AsyncResult<Void>>() {
        @Override
        public void handle(AsyncResult<Void> event) {
          out.close();
          if (afterComplete != null) {
            afterComplete.handle(event);
          }
        }
      });
    } else {
      vertx.fileSystem().open(infstr, null, true, false, false, false,
          new AsyncResultHandler<AsyncFile>() {
            @Override
            public void handle(AsyncResult<AsyncFile> event) {
              if (event.succeeded()) {
                final AsyncFile in = event.result();

                Pump.createPump(in, out).start();
                in.endHandler(new VoidHandler() {
                  @Override
                  protected void handle() {
                    in.close();
                    pumpOne(out);
                  }
                });
              } else {

              }
            }
          });
    }
  }

  public Handler<AsyncResult<Void>> getAfterComplete() {
    return afterComplete;
  }

  public void setAfterComplete(Handler<AsyncResult<Void>> afterComplete) {
    this.afterComplete = afterComplete;
  }

}
