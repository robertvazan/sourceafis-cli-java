// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.nio.file.*;
import java.text.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.config.*;
import it.unimi.dsi.fastutil.objects.*;

public class Pretty {
	/*
	 * Print complete line or several lines. Trailing newline is added if necessary.
	 * At least one line is printed, so empty string creates empty ine in output.
	 */
	public static void print(String text) {
		if (!Configuration.baselineMode)
			System.out.println(text.stripTrailing());
	}
	public static void format(String pattern, Object... args) {
		print(MessageFormat.format(pattern, args));
	}
	public static String extension(String mime) {
		return switch (mime) {
			case "application/cbor" -> ".cbor";
			case "text/plain" -> ".txt";
			default -> ".dat";
		};
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
	public static String factor(double value) {
		if (value >= 100)
			return String.format("%.0fx", value);
		if (value >= 10)
			return String.format("%.1fx", value);
		return String.format("%.2fx", value);
	}
	private static String change(double value, double baseline, String more, String less) {
		if (value == baseline)
			return "=";
		boolean positive = value >= baseline;
		double factor = positive ? value / baseline : baseline / value;
		var change = factor >= 2 ? factor(factor) : percents(Math.abs(value / baseline - 1));
		if (change.equals(percents(0)))
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
		if (abs == 0)
			return String.format("0 %s", unit);
		if (abs < 0.000_000_1)
			return String.format("%.1f n%s", value * 1_000_000_000, unit);
		if (abs < 0.000_001)
			return String.format("%.0f n%s", value * 1_000_000_000, unit);
		if (abs < 0.000_01)
			return String.format("%.2f u%s", value * 1_000_000, unit);
		if (abs < 0.000_1)
			return String.format("%.1f u%s", value * 1_000_000, unit);
		if (abs < 0.001)
			return String.format("%.0f u%s", value * 1_000_000, unit);
		if (abs < 0.01)
			return String.format("%.2f m%s", value * 1000, unit);
		if (abs < 0.1)
			return String.format("%.1f m%s", value * 1000, unit);
		if (abs < 1)
			return String.format("%.0f m%s", value * 1000, unit);
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
		if (abs < 1_000_000)
			return String.format("%.0f K%s", value / 1000, unit);
		if (abs < 10_000_000)
			return String.format("%.2f M%s", value / 1_000_000, unit);
		if (abs < 100_000_000)
			return String.format("%.1f M%s", value / 1_000_000, unit);
		if (abs < 1_000_000_000)
			return String.format("%.0f M%s", value / 1_000_000, unit);
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
	public static String speed(double value, String... tag) {
		return unit(value, "fp/s", "faster", "slower", tag);
	}
	public static String time(double value, String... tag) {
		return unit(value, "s", "slower", "faster", tag);
	}
}
