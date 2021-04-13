// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.cli.*;
import it.unimi.dsi.fastutil.objects.*;

public class Pretty {
	private static final Logger logger = LoggerFactory.getLogger(Pretty.class);
	public static void print(String text) {
		if (!Configuration.baselineMode) {
			if (text.endsWith("\n"))
				text = text.substring(0, text.length() - 1);
			for (var line : text.split("\n"))
				logger.info(line);
		}
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
	public static String dump(Path category) {
		return Configuration.output().resolve(category).toString();
	}
	private static String tag(String... tag) {
		if (tag.length == 0)
			throw new IllegalArgumentException();
		return String.join("/", tag);
	}
	private static final Map<String, String> hashes = new HashMap<>();
	public static String hash(byte[] hash, String... tag) {
		if (tag.length == 0)
			return Base64.getUrlEncoder().encodeToString(hash).replace("=", "").substring(0, 8);
		else if (Configuration.baselineMode) {
			var formatted = hash(hash);
			hashes.put(tag(tag), formatted);
			return formatted;
		} else {
			var baseline = hashes.get(tag(tag));
			var current = hash(hash);
			if (baseline == null)
				return current;
			else if (baseline.equals(current))
				return current + " (=)";
			else
				return current + " (CHANGE)";
		}
	}
	private static String percents(double value) {
		double scaled = 100 * value;
		double abs = Math.abs(scaled);
		if (abs < 1)
			return String.format("%.3f%%", scaled);
		if (abs < 10)
			return String.format("%.2f%%", scaled);
		return String.format("%.1f%%", scaled);
	}
	private static String factor(double value) {
		if (value >= 100)
			return String.format("%.0fx", value);
		if (value >= 10)
			return String.format("%.1fx", value);
		if (value >= 2)
			return String.format("%.2fx", value);
		return percents(value - 1);
	}
	private static String change(double value, double baseline, String more, String less) {
		if (value == baseline)
			return "=";
		boolean positive = value >= baseline;
		var change = factor(positive ? value / baseline : baseline / value);
		if (change.equals(factor(1)))
			return "~";
		return change + " " + (positive ? more : less);
	}
	private static final Object2DoubleMap<String> measurements = new Object2DoubleOpenHashMap<>();
	private static String measurement(double value, String formatted, String more, String less, String... tag) {
		if (tag.length == 0)
			return formatted;
		else if (Configuration.baselineMode) {
			measurements.put(tag(tag), value);
			return formatted;
		} else if (!measurements.containsKey(tag(tag)))
			return formatted;
		else
			return formatted + " (" + change(value, measurements.getDouble(tag(tag)), more, less) + ")";
	}
	public static String percents(double value, String more, String less, String... tag) {
		return measurement(value, percents(value), more, less, tag);
	}
	public static String accuracy(double value, String... tag) {
		return percents(value, "worse", "better", tag);
	}
	private static String unit(double value, String unit) {
		double abs = Math.abs(value);
		if (abs < 10)
			return String.format("%.2f %s", value, unit);
		if (abs < 100)
			return String.format("%.1f %s", value, unit);
		if (abs < 1000)
			return String.format("%.0f %s", value, unit);
		if (abs < 10_000)
			return String.format("%.2f K%s", value / 1000, unit);
		if (abs < 100_000)
			return String.format("%.1f K%s", value / 1000, unit);
		return String.format("%g %s", value, unit);
	}
	private static String unit(double value, String unit, String more, String less, String... tag) {
		return measurement(value, unit(value, unit), more, less, tag);
	}
	public static String bytes(double value, String... tag) {
		return unit(value, "B", "larger", "smaller", tag);
	}
	public static String minutiae(double value, String... tag) {
		return measurement(value, String.format("%.0f", value), "more", "fewer", tag);
	}
	private static final Object2LongMap<String> lengths = new Object2LongOpenHashMap<>();
	public static String length(long length, String... tag) {
		if (tag.length == 0)
			return String.format("%,d", length);
		else if (Configuration.baselineMode) {
			lengths.put(tag(tag), length);
			return length(length);
		} else if (!lengths.containsKey(tag(tag)))
			return length(length);
		else {
			long baseline = lengths.getLong(tag(tag));
			return length(length) + " (" + (baseline == length ? "=" : String.format("%+,d", length - baseline)) + ")";
		}
	}
	public static String decibans(double value, String... tag) {
		if (tag.length == 0) {
			if (value < 10)
				return String.format("%.2f dban", value);
			if (value < 100)
				return String.format("%.1f dban", value);
			return String.format("%.0f dban", value);
		} else
			return measurement(value, decibans(value), "higher", "lower", tag);
	}
}
