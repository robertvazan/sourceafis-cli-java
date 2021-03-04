// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.security.*;
import java.util.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import one.util.streamex.*;

class TransparencyStats {
	int count;
	long size;
	byte[] hash;
	static class Row extends TransparencyStats {
		String key;
	}
	static class Table {
		List<Row> rows;
	}
	private static class RowCollector {
		int count;
		long size;
		final MessageDigest hasher = Exceptions.sneak().get(() -> MessageDigest.getInstance("SHA-256"));
	}
	private static class TableCollector extends FingerprintTransparency {
		final Map<String, RowCollector> rows = new HashMap<>();
		final List<String> order = new ArrayList<>();
		@Override
		public void take(String key, String mime, byte[] data) {
			var row = rows.get(key);
			if (row == null) {
				order.add(key);
				rows.put(key, row = new RowCollector());
			}
			++row.count;
			row.size += data.length;
			row.hasher.update(data);
		}
		Table toTable() {
			var table = new Table();
			table.rows = StreamEx.of(order)
				.map(k -> {
					var collector = rows.get(k);
					var row = new Row();
					row.key = k;
					row.count = collector.count;
					row.size = collector.size;
					row.hash = collector.hasher.digest();
					return row;
				})
				.toList();
			return table;
		}
	}
	static Table extractorTable(SampleFingerprint fp) {
		return new PersistentCache<>(Table.class, "transparency", "stats", "extractor", fp) {
			@Override
			Table compute() {
				var image = fp.load();
				try (var collector = new TableCollector()) {
					new FingerprintTemplate(new FingerprintImage()
						.dpi(fp.dataset.dpi)
						.decode(image));
					return collector.toTable();
				}
			}
		}.get();
	}
}
