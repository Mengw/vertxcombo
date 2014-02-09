package com.m3958.vertxio.vertxcombo;


public abstract class UrlStyle {

  protected String sanitizeUrl(String originUrl) {
    if (originUrl.endsWith("?")) {
      return originUrl.substring(0, originUrl.length() - 2);
    } else {
      return originUrl;
    }
  }

  public abstract ExtractFileResult extractFiles(String url);

  public abstract String generateRandomUrl(String pattern, int number, String version);

}
