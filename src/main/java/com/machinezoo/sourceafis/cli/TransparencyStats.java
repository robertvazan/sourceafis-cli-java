// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.*;
import one.util.streamex.*;

class TransparencyStats {
	String mime;
	int count;
	long size;
	byte[] hash;
	static TransparencyStats of(String mime, byte[] data) {
		var stats = new TransparencyStats();
		stats.mime = mime;
		stats.count = 1;
		stats.size = data.length;
		stats.hash = DataHash.of(mime, data);
		return stats;
	}
	static TransparencyStats sum(List<TransparencyStats> list) {
		var sum = new TransparencyStats();
		sum.mime = list.get(0).mime;
		var hash = new DataHash();
		for (var stats : list) {
			sum.count += stats.count;
			sum.size += stats.size;
			hash.add(stats.hash);
		}
		sum.hash = hash.compute();
		return sum;
	}
	static class Row {
		String key;
		TransparencyStats stats;
	}
	static class Table {
		List<Row> rows = new ArrayList<>();;
		static Table of(String key, String mime, byte[] data) {
			var row = new Row();
			row.key = key;
			row.stats = TransparencyStats.of(mime, data);
			var table = new Table();
			table.rows.add(row);
			return table;
		}
		static Table sum(List<Table> list) {
			var groups = new HashMap<String, List<TransparencyStats>>();
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
				row.stats = TransparencyStats.sum(groups.get(row.key));
			return sum;
		}
	}
	private static class TableCollector extends FingerprintTransparency {
		final List<Table> records = new ArrayList<>();
		@Override
		public void take(String key, String mime, byte[] data) {
			records.add(Table.of(key, mime, data));
		}
	}
	static Table extractorTable(SampleFingerprint fp) {
		return PersistentCache.get(Table.class, Paths.get("extractor-transparency-stats"), fp.path(), () -> {
			try (var collector = new TableCollector()) {
				new FingerprintTemplate(fp.decode());
				return Table.sum(collector.records);
			}
		});
	}
	static TransparencyStats extractorRow(SampleFingerprint fp, String key) {
		return extractorTable(fp).rows.stream().filter(r -> r.key.equals(key)).findFirst().orElseThrow().stats;
	}
	static Table extractorTable() {
		return Table.sum(StreamEx.of(SampleFingerprint.all()).map(fp -> extractorTable(fp)).toList());
	}
	private static final Logger logger = LoggerFactory.getLogger(TransparencyStats.class);
	static void report(Table table) {
		for (var row : table.rows) {
			var stats = row.stats;
			logger.info("Transparency/{}: {}, {}x, {} B, hash {}", row.key, stats.mime, stats.count, stats.size / stats.count, DataHash.format(stats.hash));
		}
	}
}
