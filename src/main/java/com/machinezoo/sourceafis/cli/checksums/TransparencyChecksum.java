// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public abstract class TransparencyChecksum<K> implements Runnable {
	public abstract String name();
	public abstract List<K> ids();
	protected abstract TransparencyTable checksum(K id);
	protected Path category() {
		return Paths.get("checksums", "transparency", name());
	}
	public TransparencyTable checksum() {
		return Cache.get(TransparencyTable.class, category(), Paths.get("all"), () -> {
			return TransparencyTable.sum(StreamEx.of(ids()).map(this::checksum).toList());
		});
	}
	public String mime(String key) {
		return checksum().mime(key);
	}
	public int count(K id, String key) {
		return checksum(id).count(key);
	}
	public byte[] global() {
		var hash = new Hash();
		for (var row : checksum().rows)
			hash.add(row.stats.hash);
		return hash.compute();
	}
	@Override
	public void run() {
		var table = new Pretty.Table("Key", "MIME", "Count", "Length", "Normalized", "Total", "Hash");
		for (var row : checksum().rows) {
			var stats = row.stats;
			table.add(
				row.key,
				stats.mime,
				Pretty.length(stats.count),
				Pretty.length(stats.length / stats.count, row.key, "length"),
				Pretty.length(stats.normalized / stats.count, row.key, "normalized"),
				Pretty.length(stats.normalized, row.key, "total"),
				Pretty.hash(stats.hash, row.key, "hash"));
		}
		Pretty.print(table.format());
	}
}
