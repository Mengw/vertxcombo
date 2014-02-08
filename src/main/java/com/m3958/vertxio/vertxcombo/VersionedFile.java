package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.json.JsonObject;

public class VersionedFile {
  private Path file;
  private String v;

  private long length;

  public VersionedFile(Path file, String v) {
    super();
    this.file = file;
    this.v = v;
  }

  @Override
  public String toString() {
    return file.toString() + "!" + v;
  }


  public Path getFile() {
    return file;
  }

  public void setFile(Path file) {
    this.file = file;
  }

  public String getV() {
    return v;
  }

  public void setV(String v) {
    this.v = v;
  }

  public long getLength() {
    return length;
  }

  public VersionedFile setLength(long length) {
    this.length = length;
    return this;
  }

  public JsonObject toJson() {
    return new JsonObject().putString("v", v).putString("file", file.toString())
        .putNumber("length", length);
  }

  public static VersionedFile fromJson(JsonObject jo) {
    VersionedFile vf = new VersionedFile(Paths.get(jo.getString("file")), jo.getString("v"));
    return vf.setLength(jo.getLong("length"));
  }
}
