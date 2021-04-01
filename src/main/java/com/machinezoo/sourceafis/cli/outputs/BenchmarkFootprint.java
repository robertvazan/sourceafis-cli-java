// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class BenchmarkFootprint {
	private static class Stats {
		int count;
		double serialized;
	}
	private static Stats measure(Fingerprint fp) {
		return Cache.get(Stats.class, Paths.get("benchmarks", "footprint"), fp.path(), () -> {
			var footprint = new Stats();
			var serialized = Template.serialized(fp);
			footprint.count = 1;
			footprint.serialized = serialized.length;
			return footprint;
		});
	}
	private static Stats measure(Profile profile) {
		var sum = new Stats();
		for (var fp : profile.fingerprints()) {
			var stats = measure(fp);
			sum.count += stats.count;
			sum.serialized += stats.serialized;
		}
		return sum;
	}
	public static void report() {
		var table = new Pretty.Table("Dataset", "Serialized");
		for (var profile : Profile.all()) {
			var stats = measure(profile);
			table.add(profile.name, Pretty.bytes(stats.serialized / stats.count));
		}
		Pretty.print(table.format());
	}
}
