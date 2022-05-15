// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public abstract class TransparencyChecksum<K> extends Command {
	public abstract String name();
	/*
	 * Returns parallel stream if parallelization over IDs is desirable.
	 */
	public abstract Stream<K> ids();
	protected abstract TransparencyTable checksum(K id);
	@Override
	public List<String> subcommand() {
		return List.of("checksum", "transparency", name());
	}
	protected Path category() {
		return Paths.get("checksums", "transparency", name());
	}
	public TransparencyTable checksum() {
		return Cache.get(TransparencyTable.class, category(), Paths.get("all"), () -> {
			return TransparencyTable.sum(ids().map(this::checksum).toList());
		});
	}
	public String mime(String key) {
		return checksum().mime(key);
	}
	public int count(K id, String key) {
		return checksum(id).count(key);
	}
	public byte[] global() {
		var hash = new Hasher();
		for (var row : checksum().rows)
			if (!row.key.equals("version"))
				hash.add(row.stats.hash);
		return hash.compute();
	}
	@Override
	public void run() {
		MissingBaselineException.silence().run(() -> {
			var table = new PrettyTable("Key", "MIME", "Count", "Length", "Normalized", "Total", "Hash");
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
		});
	}
}
