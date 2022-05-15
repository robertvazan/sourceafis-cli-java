// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;
import java.util.stream.*;

public class PrettyTable {
	private final List<String> columns = new ArrayList<>();
	private final List<String> cells = new ArrayList<>();
	public void add(String column, String cell) {
		if (!columns.contains(column))
			columns.add(column);
		cells.add(cell);
	}
	public String format() {
		var rows = new ArrayList<List<String>>();
		rows.add(columns);
		int rank = columns.size();
		if (rank == 0)
			return "";
		for (int i = 0; i < cells.size() / rank; ++i)
			rows.add(cells.subList(i * rank, (i + 1) * rank));
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
	public void print() {
		Pretty.print(format());
	}
}
