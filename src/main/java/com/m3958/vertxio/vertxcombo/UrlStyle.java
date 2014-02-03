package com.m3958.vertxio.vertxcombo;


public interface UrlStyle {
  ExtractFileResult extractFiles(String url);

  String generateRandomUrl(String pattern, int number);

}
