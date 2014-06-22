package com.m3958.vertxio.vertxcombo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.common.io.CharStreams;


public class Utils {

	public static String getFileExtWithDot(String fn) {
		int idx = fn.lastIndexOf('.');
		if (idx == -1) {
			return "";
		} else {
			return fn.substring(idx);
		}
	}

	public static String readResouce(String rn) {
		try {
			InputStream is = Utils.class.getResourceAsStream(rn);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			return CharStreams.toString(reader);
		} catch (UnsupportedEncodingException e) {
			return "UnsupportedEncodingException";
		} catch (IOException e) {
			return "IOException";
		}
	}
}
