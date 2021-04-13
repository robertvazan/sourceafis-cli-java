// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import org.openjdk.jol.info.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class BenchmarkFootprint {
	private static class Stats {
		int count;
		double serialized;
		double memory;
		double minutiae;
	}
	private static int memory(Fingerprint fp) {
		/*
		 * JOL will cause various warnings to be printed to the console.
		 * This can be only fixed by fiddling with command-line options for the CLI app.
		 */
		var graph = GraphLayout.parseInstance(Template.of(fp));
		var siblings = new ArrayList<>(fp.dataset.fingerprints());
		Collections.shuffle(siblings, new Random(0));
		for (var other : siblings.subList(0, 2))
			graph = graph.subtract(GraphLayout.parseInstance(Template.of(other)));
		return (int)graph.totalSize();
	}
	private static Stats measure(Fingerprint fp) {
		return Cache.get(Stats.class, Paths.get("benchmarks", "footprint"), fp.path(), () -> {
			var footprint = new Stats();
			var serialized = Template.serialized(fp);
			footprint.count = 1;
			footprint.serialized = serialized.length;
			footprint.memory = memory(fp);
			footprint.minutiae = Template.parse(fp).types.length();
			return footprint;
		});
	}
	private static Stats measure(Profile profile) {
		var sum = new Stats();
		for (var fp : profile.fingerprints()) {
			var stats = measure(fp);
			sum.count += stats.count;
			sum.serialized += stats.serialized;
			sum.memory += stats.memory;
			sum.minutiae += stats.minutiae;
		}
		return sum;
	}
	public static void report() {
		var table = new Pretty.Table("Dataset", "Serialized", "Memory", "Minutiae");
		for (var profile : Profile.all()) {
			var stats = measure(profile);
			table.add(
				profile.name,
				Pretty.bytes(stats.serialized / stats.count, profile.name, "serialized"),
				Pretty.bytes(stats.memory / stats.count, profile.name, "memory"),
				Pretty.minutiae(stats.minutiae / stats.count, profile.name, "minutiae"));
		}
		Pretty.print(table.format());
	}
}
