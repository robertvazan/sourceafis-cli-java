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
	private static class Accumulator {
		int count;
		long size;
		final MessageDigest hasher = Exceptions.sneak().get(() -> MessageDigest.getInstance("SHA-256"));
		void add(byte[] data) {
			++count;
			size += data.length;
			hasher.update(data);
		}
		void add(TransparencyStats stats) {
			count += stats.count;
			size += stats.size;
			hasher.update(stats.hash);
		}
	}
	private static class AccumulatorTable {
		final Map<String, Accumulator> accumulators = new HashMap<>();
		final List<String> order = new ArrayList<>();
		Accumulator accumulator(String key) {
			var accumulator = accumulators.get(key);
			if (accumulator == null) {
				order.add(key);
				accumulators.put(key, accumulator = new Accumulator());
			}
			return accumulator;
		}
		void add(String key, byte[] data) {
			accumulator(key).add(data);
		}
		void add(Row row) {
			accumulator(row.key).add(row);
		}
		void add(Table table) {
			for (var row : table.rows)
				add(row);
		}
		Table summarize() {
			var table = new Table();
			table.rows = StreamEx.of(order)
				.map(k -> {
					var accumulator = accumulators.get(k);
					var row = new Row();
					row.key = k;
					row.count = accumulator.count;
					row.size = accumulator.size;
					row.hash = accumulator.hasher.digest();
					return row;
				})
				.toList();
			return table;
		}
	}
	private static Table sumTables(List<Table> tables) {
		var accumulator = new AccumulatorTable();
		for (var table : tables)
			accumulator.add(table);
		return accumulator.summarize();
	}
	private static class TableCollector extends FingerprintTransparency {
		final AccumulatorTable accumulator = new AccumulatorTable();
		@Override
		public void take(String key, String mime, byte[] data) {
			accumulator.add(key, data);
		}
	}
	static Table extractorTable(SampleFingerprint fp) {
		return new PersistentCache<>(Table.class, "extractor-transparency-stats", fp) {
			@Override
			Table compute() {
				var image = fp.load();
				try (var collector = new TableCollector()) {
					new FingerprintTemplate(new FingerprintImage()
						.dpi(fp.dataset.dpi)
						.decode(image));
					return collector.accumulator.summarize();
				}
			}
		}.get();
	}
	static Table extractorTable(SampleDataset dataset) {
		return sumTables(StreamEx.of(dataset.fingerprints()).map(fp -> extractorTable(fp)).toList());
	}
	static Table extractorTable() {
		return sumTables(StreamEx.of(SampleDataset.all()).map(ds -> extractorTable(ds)).toList());
	}
}
