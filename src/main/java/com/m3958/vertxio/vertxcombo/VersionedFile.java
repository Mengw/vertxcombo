package com.m3958.vertxio.vertxcombo;

import java.nio.file.Path;

public class VersionedFile {
    private Path file;
    private String v;
    
    public VersionedFile(Path file, String v) {
      super();
      this.file = file;
      this.v = v;
    }
    
    @Override
    public String toString(){
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
}
