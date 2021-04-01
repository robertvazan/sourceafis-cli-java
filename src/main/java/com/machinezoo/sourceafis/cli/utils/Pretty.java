// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;

public class Pretty {
	public static String hash(byte[] hash) {
		return Base64.getUrlEncoder().encodeToString(hash).replace("=", "");
	}
	public static String extension(String mime) {
		switch (mime) {
		case "application/cbor":
			return ".cbor";
		case "text/plain":
			return ".txt";
		default:
			return ".dat";
		}
	}
}
