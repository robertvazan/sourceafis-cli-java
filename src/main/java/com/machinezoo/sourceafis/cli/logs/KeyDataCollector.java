// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.logs;

import java.util.*;
import com.machinezoo.sourceafis.*;

public class KeyDataCollector extends FingerprintTransparency {
	public final String key;
	public final List<byte[]> files = new ArrayList<>();
	public KeyDataCollector(String key) {
		this.key = key;
	}
	@Override
	public boolean accepts(String key) {
		return this.key.equals(key);
	}
	@Override
	public void take(String key, String mime, byte[] data) {
		files.add(data);
	}
	public static List<byte[]> collect(String key, Runnable action) {
		try (var logger = new KeyDataCollector(key)) {
			action.run();
			return logger.files;
		}
	}
}
