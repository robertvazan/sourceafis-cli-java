// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class Footprint {
	private static class Stats {
		int count;
		double serialized;
	}
	private static Stats measure(Fingerprint fp) {
		return Cache.get(Stats.class, Paths.get("footprints"), fp.path(), () -> {
			var footprint = new Stats();
			var serialized = Template.serialized(fp);
			footprint.count = 1;
			footprint.serialized = serialized.length;
			return footprint;
		});
	}
	private static Stats measure(List<Stats> list) {
		var sum = new Stats();
		for (var footprint : list) {
			sum.count += footprint.count;
			sum.serialized += footprint.serialized;
		}
		return sum;
	}
	private static Stats measure(Dataset dataset) {
		return measure(StreamEx.of(dataset.fingerprints()).map(fp -> measure(fp)).toList());
	}
	private static Stats measure() {
		return measure(StreamEx.of(Fingerprint.all()).map(fp -> measure(fp)).toList());
	}
	private static void report(Pretty.Table table, String title, Stats stats) {
		table.add(title, Pretty.bytes(stats.serialized / stats.count));
	}
	public static void report() {
		var table = new Pretty.Table("Dataset", "Serialized");
		for (var dataset : Dataset.all())
			report(table, dataset.name, measure(dataset));
		report(table, "All", measure());
		Pretty.print(table.format());
	}
}
