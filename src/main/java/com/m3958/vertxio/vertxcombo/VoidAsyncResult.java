package com.m3958.vertxio.vertxcombo;

import org.vertx.java.core.AsyncResult;

public class VoidAsyncResult implements AsyncResult<Void> {

  private boolean success;

  public VoidAsyncResult(boolean success) {
    super();
    this.success = success;
  }

  @Override
  public Void result() {
    return null;
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
