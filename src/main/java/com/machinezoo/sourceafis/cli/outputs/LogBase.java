// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class LogBase {
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
	protected static List<byte[]> collect(String key, Runnable action) {
		try (var collector = new KeyCollector(key)) {
			action.run();
			return collector.files;
		}
	}
	protected static Path identity(Path path, int index, int count, String mime) {
		var extension = Pretty.extension(mime);
		if (count > 1)
			return Cache.withExtension(path.resolve(Integer.toString(index)), extension);
		else
			return Cache.withExtension(path, extension);
	}
	protected static Path category(String key, String kind) {
		if (Configuration.normalized)
			return Paths.get("logs", kind, "normalized", key);
		else
			return Paths.get("logs", kind, key);
	}
	protected static void collect(String key, int index, int count, String mime, IntFunction<Path> identity, Runnable action, Map<Path, Object> map) {
		var collected = collect(key, action);
		for (int i = 0; i < count; ++i) {
			var raw = collected.get(index);
			var normalized = Configuration.normalized ? Serializer.normalize(mime, raw) : raw;
			map.put(identity.apply(i), normalized);
		}
	}
}
