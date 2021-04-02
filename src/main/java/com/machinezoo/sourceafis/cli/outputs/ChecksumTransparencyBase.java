// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ChecksumTransparencyBase {
	protected static class Stats {
		public String mime;
		public int count;
		public long length;
		public long normalized;
		public byte[] hash;
	}
	private static Stats checksum(String mime, byte[] data) {
		var stats = new Stats();
		stats.mime = mime;
		stats.count = 1;
		stats.length = data.length;
		var normalized = Serializer.normalize(mime, data);
		stats.normalized = normalized.length;
		stats.hash = Hash.of(normalized);
		return stats;
	}
	private static Stats sum(List<Stats> list) {
		var sum = new Stats();
		sum.mime = list.get(0).mime;
		var hash = new Hash();
		for (var stats : list) {
			sum.count += stats.count;
			sum.length += stats.length;
			sum.normalized += stats.normalized;
			hash.add(stats.hash);
		}
		sum.hash = hash.compute();
		return sum;
	}
	protected static class Row {
		public String key;
		public Stats stats;
	}
	protected static class Table {
		public final List<Row> rows = new ArrayList<>();;
	}
	private static Table solo(String key, String mime, byte[] data) {
		var row = new Row();
		row.key = key;
		row.stats = checksum(mime, data);
		var table = new Table();
		table.rows.add(row);
		return table;
	}
	protected static Table merge(List<Table> list) {
		var groups = new HashMap<String, List<Stats>>();
		var sum = new Table();
		for (var table : list) {
			for (var row : table.rows) {
				var group = groups.get(row.key);
				if (group == null) {
					var srow = new Row();
					srow.key = row.key;
					sum.rows.add(srow);
					groups.put(row.key, group = new ArrayList<>());
				}
				group.add(row.stats);
			}
		}
		for (var row : sum.rows)
			row.stats = sum(groups.get(row.key));
		return sum;
	}
	private static class TableCollector extends FingerprintTransparency {
		final List<Table> records = new ArrayList<>();
		@Override
		public void take(String key, String mime, byte[] data) {
			records.add(solo(key, mime, data));
		}
	}
	protected static Table collect(Runnable action) {
		try (var collector = new TableCollector()) {
			action.run();
			return merge(collector.records);
		}
	}
	private static Optional<Stats> row(Table table, String key) {
		return table.rows.stream()
			.filter(r -> r.key.equals(key))
			.findFirst()
			.map(r -> r.stats);
	}
	protected static String mime(Table table, String key) {
		return row(table, key).orElseThrow(() -> new IllegalArgumentException("Transparency key not found: " + key)).mime;
	}
	protected static int count(Table table, String key) {
		var row = row(table, key).orElse(null);
		return row != null ? row.count : 0;
	}
	protected static void report(Table table) {
		var ptable = new Pretty.Table("Key", "MIME", "Count", "Length", "Normalized", "Total", "Hash");
		for (var row : table.rows) {
			var stats = row.stats;
			ptable.add(
				row.key,
				stats.mime,
				Pretty.length(stats.count),
				Pretty.length(stats.length / stats.count, row.key, "length"),
				Pretty.length(stats.normalized / stats.count, row.key, "normalized"),
				Pretty.length(stats.normalized, row.key, "total"),
				Pretty.hash(stats.hash, row.key, "hash"));
		}
		Pretty.print(ptable.format());
	}
}
