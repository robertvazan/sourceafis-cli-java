// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ChecksumTransparency {
	public static class Stats {
		public String mime;
		public int count;
		public long length;
		public long normalized;
		public byte[] hash;
	}
	public static Stats checksum(String mime, byte[] data) {
		var stats = new Stats();
		stats.mime = mime;
		stats.count = 1;
		stats.length = data.length;
		var normalized = Serializer.normalize(mime, data);
		stats.normalized = normalized.length;
		stats.hash = Hash.of(normalized);
		return stats;
	}
	public static Stats sum(List<Stats> list) {
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
	public static class Row {
		String key;
		Stats stats;
	}
	public static class Table {
		List<Row> rows = new ArrayList<>();;
		static Table single(String key, String mime, byte[] data) {
			var row = new Row();
			row.key = key;
			row.stats = checksum(mime, data);
			var table = new Table();
			table.rows.add(row);
			return table;
		}
		static Table merge(List<Table> list) {
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
	}
	private static class TableCollector extends FingerprintTransparency {
		final List<Table> records = new ArrayList<>();
		@Override
		public void take(String key, String mime, byte[] data) {
			records.add(Table.single(key, mime, data));
		}
	}
	public static Table collect(Runnable action) {
		try (var collector = new TableCollector()) {
			action.run();
			return Table.merge(collector.records);
		}
	}
	public static void report(Table table) {
		var ptable = new Pretty.Table("Key", "MIME", "Count", "Length", "Normalized", "Total", "Hash");
		for (var row : table.rows) {
			var stats = row.stats;
			ptable.add(
				row.key,
				stats.mime,
				Pretty.length(stats.count),
				Pretty.length(stats.length / stats.count),
				Pretty.length(stats.normalized / stats.count),
				Pretty.length(stats.normalized),
				Pretty.hash(stats.hash));
		}
		Pretty.print(ptable.format());
	}
}
