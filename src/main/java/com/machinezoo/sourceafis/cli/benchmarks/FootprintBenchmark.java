// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import org.openjdk.jol.info.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class FootprintBenchmark implements Runnable {
	private int memory(Fingerprint fp) {
		/*
		 * JOL will cause various warnings to be printed to the console.
		 * This can be only fixed by fiddling with command-line options for the CLI app.
		 */
		var graph = GraphLayout.parseInstance(TemplateCache.deserialize(fp));
		var siblings = new ArrayList<>(fp.dataset.fingerprints());
		Collections.shuffle(siblings, new Random(0));
		for (var other : siblings.subList(0, 2))
			graph = graph.subtract(GraphLayout.parseInstance(TemplateCache.deserialize(other)));
		return (int)graph.totalSize();
	}
	private FootprintStats measure(Fingerprint fp) {
		return Cache.get(FootprintStats.class, Paths.get("benchmarks", "footprint"), fp.path(), () -> {
			var footprint = new FootprintStats();
			var serialized = TemplateCache.load(fp);
			footprint.count = 1;
			footprint.serialized = serialized.length;
			footprint.memory = memory(fp);
			footprint.minutiae = ParsedTemplate.parse(fp).types.length();
			return footprint;
		});
	}
	private FootprintStats sum(Profile profile) {
		return FootprintStats.sum(StreamEx.of(profile.fingerprints()).map(this::measure).toList());
	}
	@Override
	public void run() {
		var table = new Pretty.Table("Dataset", "Serialized", "Memory", "Minutiae");
		for (var profile : Profile.all()) {
			var stats = sum(profile);
			table.add(
				profile.name,
				Pretty.bytes(stats.serialized / stats.count, profile.name, "serialized"),
				Pretty.bytes(stats.memory / stats.count, profile.name, "memory"),
				Pretty.minutiae(stats.minutiae / stats.count, profile.name, "minutiae"));
		}
		Pretty.print(table.format());
	}
}
