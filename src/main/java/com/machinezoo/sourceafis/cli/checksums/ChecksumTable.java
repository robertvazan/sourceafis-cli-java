// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;

public record ChecksumTable(List<ChecksumRow> rows) {
	public static ChecksumTable solo(String key, String mime, byte[] data) {
		return new ChecksumTable(List.of(new ChecksumRow(key, ChecksumStats.of(mime, data))));
	}
	public static ChecksumTable sum(List<ChecksumTable> list) {
		var groups = new HashMap<String, List<ChecksumStats>>();
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
		return new ChecksumTable(keys.stream()
			.map(k -> new ChecksumRow(k, ChecksumStats.sum(groups.get(k))))
			.toList());
	}
}
