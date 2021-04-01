// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.util.*;
import com.machinezoo.sourceafis.*;

public class Log {
	private static class KeyCollector extends FingerprintTransparency {
		final String key;
		final List<byte[]> files = new ArrayList<>();
		KeyCollector(String key) {
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
	}
	public static List<byte[]> key(String key, Runnable action) {
		try (var collector = new KeyCollector(key)) {
			action.run();
			return collector.files;
		}
	}
}