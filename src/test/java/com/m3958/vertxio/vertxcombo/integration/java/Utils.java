package com.m3958.vertxio.vertxcombo.integration.java;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.vertx.java.core.json.JsonObject;

import com.m3958.vertxio.vertxcombo.MainVerticle;

public class Utils {

  public static void checkHeaders(HttpResponse res){
    for(Header hd : res.getAllHeaders()){
      System.out.println(hd.getName() + ":" + hd.getValue());
    }
  }
  
  public static StringBuilder getUrlBeforPath(JsonObject config){
    StringBuilder sb = new StringBuilder();
    sb.append("http://");
    sb.append("localhost");
    sb.append(":");
    sb.append(config.getInteger(MainVerticle.CFGKEY_LISTEN_PORT));
    return sb;
  }
}
