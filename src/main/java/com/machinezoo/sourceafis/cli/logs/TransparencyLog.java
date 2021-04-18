// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public abstract class TransparencyLog<K extends DataIdentifier> {
	public abstract String name();
	protected abstract TransparencyChecksum<K> checksum();
	protected abstract byte[] log(String key, K id, int index, int count, String mime);
	protected Path category(String key) {
		if (Configuration.normalized)
			return Paths.get("logs", name(), "normalized", key);
		else
			return Paths.get("logs", name(), key);
	}
	protected Path identity(String key, K id, int index, int count, String mime) {
		var path = id.path();
		if (count > 1)
			path = path.resolve(Integer.toString(index));
		return Cache.withExtension(path, Pretty.extension(mime));
	}
	protected void log(String key, K id, int index, int count, String mime, Runnable action, Map<Path, Object> map) {
		var collected = KeyDataCollector.collect(key, action);
		for (int i = 0; i < count; ++i) {
			var raw = collected.get(index);
			var normalized = Configuration.normalized ? Serializer.normalize(mime, raw) : raw;
			map.put(identity(key, id, i, count, mime), normalized);
		}
	}
	public void log(String key) {
		var mime = checksum().mime(key);
		for (var id : checksum().ids()) {
			int count = checksum().count(id, key);
			if (count > 0)
				log(key, id, 0, count, mime);
		}
		Pretty.print("Saved: " + Pretty.dump(category(key)));
	}
}
