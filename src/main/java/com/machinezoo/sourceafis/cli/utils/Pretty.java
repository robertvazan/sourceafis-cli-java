// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;
import java.util.stream.*;
import org.slf4j.*;

public class Pretty {
	private static final Logger logger = LoggerFactory.getLogger(Pretty.class);
	public static void print(String text) {
		if (text.endsWith("\n"))
			text = text.substring(0, text.length() - 1);
		for (var line : text.split("\n"))
			logger.info(line);
	}
	public static class Table {
		private final List<String> columns;
		private final List<List<String>> rows = new ArrayList<>();
		public Table(String... columns) {
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
	public static String hash(byte[] hash) {
		return Base64.getUrlEncoder().encodeToString(hash).replace("=", "");
	}
	public static String extension(String mime) {
		switch (mime) {
		case "application/cbor":
			return ".cbor";
		case "text/plain":
			return ".txt";
		default:
			return ".dat";
		}
	}
	public static String percents(double value) {
		double scaled = 100 * value;
		double abs = Math.abs(scaled);
		if (abs < 1)
			return String.format("%.3f%%", scaled);
		if (abs < 10)
			return String.format("%.2f%%", scaled);
		return String.format("%.1f%%", scaled);
	}
	public static String unit(double value, String unit) {
		double abs = Math.abs(value);
		if (abs < 100)
			return String.format("%.1f %s", value, unit);
		if (abs < 1000)
			return String.format("%.0f %s", value, unit);
		if (abs < 10_000)
			return String.format("%.2f K%s", value / 1000, unit);
		return String.format("%g %s", value, unit);
	}
	public static String bytes(double value) {
		return unit(value, "B");
	}
}
