// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.security.*;
import java.util.*;
import com.machinezoo.noexception.*;

class DataHash {
	private final MessageDigest hasher = Exceptions.sneak().get(() -> MessageDigest.getInstance("SHA-256"));
	void add(byte[] data) {
		hasher.update(data);
	}
	byte[] compute() {
		return hasher.digest();
	}
	static byte[] of(byte[] data) {
		var hash = new DataHash();
		hash.add(data);
		return hash.compute();
	}
	static String format(byte[] hash) {
		return Base64.getUrlEncoder().encodeToString(hash).replace("=", "");
	}
}
