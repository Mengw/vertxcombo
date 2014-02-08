package com.m3958.vertxio.vertxcombo;


import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.buffer.Buffer;

public class BufferListAsyncResult implements AsyncResult<Buffer[]> {

  private boolean success;

  private Buffer[] result;

  public BufferListAsyncResult(boolean success, Buffer[] result) {
    super();
    this.success = success;
    this.result = result;
  }

  @Override
  public Buffer[] result() {
    return result;
  }

  @Override
  public Throwable cause() {
    return null;
  }

  @Override
  public boolean succeeded() {
    return success;
  }

  @Override
  public boolean failed() {
    return !success;
  }

}
