// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.security.*;
import com.machinezoo.noexception.*;

public class Hash {
	private final MessageDigest hasher = Exceptions.sneak().get(() -> MessageDigest.getInstance("SHA-256"));
	public void add(byte[] data) {
		hasher.update(data);
	}
	public byte[] compute() {
		return hasher.digest();
	}
	public static byte[] of(byte[] data) {
		var hash = new Hash();
		hash.add(data);
		return hash.compute();
	}
	public static byte[] of(String mime, byte[] data) {
		return of(Serializer.normalize(mime, data));
	}
}
