// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;
import java.util.stream.*;
import com.machinezoo.stagean.*;

@DraftApi
@ApiIssue("Support variable number of columns.")
public class PrettyTable {
	private final List<String> columns;
	private final List<List<String>> rows = new ArrayList<>();
	public PrettyTable(String... columns) {
		this.columns = List.of(columns);
		rows.add(this.columns);
	}
	public void add(String... cells) {
		rows.add(List.of(cells));
	}
	public String format() {
		int[] widths = IntStream.range(0, columns.size()).map(cn -> rows.stream().mapToInt(r -> r.get(cn).length()).max().getAsInt()).toArray();
		var lines = new ArrayList<String>();
		for (var row : rows) {
			var line = "";
			for (int i = 0; i < columns.size(); ++i) {
				if (i + 1 < columns.size())
					line += String.format(String.format("%%-%ds", widths[i] + 2), row.get(i));
				else
					line += row.get(i);
			}
			lines.add(line);
		}
		return String.join("\n", lines);
	}
}
