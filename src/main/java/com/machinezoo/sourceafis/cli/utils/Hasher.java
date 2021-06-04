// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.security.*;
import com.machinezoo.noexception.*;

public class Hasher {
	private final MessageDigest hasher = Exceptions.sneak().get(() -> MessageDigest.getInstance("SHA-256"));
	public void add(byte[] data) {
		hasher.update(data);
	}
	public byte[] compute() {
		return hasher.digest();
	}
	public static byte[] hash(byte[] data) {
		var hash = new Hasher();
		hash.add(data);
		return hash.compute();
	}
	public static byte[] hash(String mime, byte[] data) {
		return hash(Serializer.normalize(mime, data));
	}
}
