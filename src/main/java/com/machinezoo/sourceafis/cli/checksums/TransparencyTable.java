// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;

public class TransparencyTable {
	public final List<TransparencyRow> rows = new ArrayList<>();;
	public static TransparencyTable solo(String key, String mime, byte[] data) {
		var row = new TransparencyRow();
		row.key = key;
		row.stats = TransparencyStats.of(mime, data);
		var table = new TransparencyTable();
		table.rows.add(row);
		return table;
	}
	public static TransparencyTable sum(List<TransparencyTable> list) {
		var groups = new HashMap<String, List<TransparencyStats>>();
		var sum = new TransparencyTable();
		for (var table : list) {
			for (var row : table.rows) {
				var group = groups.get(row.key);
				if (group == null) {
					var srow = new TransparencyRow();
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
	public Optional<TransparencyStats> stats(String key) {
		return rows.stream()
			.filter(r -> r.key.equals(key))
			.findFirst()
			.map(r -> r.stats);
	}
	public String mime(String key) {
		return stats(key).orElseThrow(() -> new IllegalArgumentException("Transparency key not found: " + key)).mime;
	}
	public int count(String key) {
		var row = stats(key).orElse(null);
		return row != null ? row.count : 0;
	}
}
