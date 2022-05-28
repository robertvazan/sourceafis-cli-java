// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;

public record TransparencyTable(List<TransparencyRow> rows) {
	public static TransparencyTable solo(String key, String mime, byte[] data) {
		var row = new TransparencyRow(key, TransparencyStats.of(mime, data));
		return new TransparencyTable(List.of(row));
	}
	public static TransparencyTable sum(List<TransparencyTable> list) {
		var groups = new HashMap<String, List<TransparencyStats>>();
		var keys = new ArrayList<String>();
		for (var table : list) {
			for (var row : table.rows) {
				var group = groups.get(row.key());
				if (group == null) {
					keys.add(row.key());
					groups.put(row.key(), group = new ArrayList<>());
				}
				group.add(row.stats());
			}
		}
		return new TransparencyTable(keys.stream()
			.map(k -> new TransparencyRow(k, TransparencyStats.sum(groups.get(k))))
			.toList());
	}
	public Optional<TransparencyStats> stats(String key) {
		return rows.stream()
			.filter(r -> r.key().equals(key))
			.findFirst()
			.map(r -> r.stats());
	}
	public String mime(String key) {
		return stats(key).orElseThrow(() -> new IllegalArgumentException("Transparency key not found: " + key)).mime();
	}
	public int count(String key) {
		var row = stats(key).orElse(null);
		return row != null ? row.count() : 0;
	}
}
