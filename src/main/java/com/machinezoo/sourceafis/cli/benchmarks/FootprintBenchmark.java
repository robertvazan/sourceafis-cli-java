// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import org.openjdk.jol.info.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class FootprintBenchmark extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "footprint");
	}
	@Override
	public String description() {
		return "Measure template footprint.";
	}
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
		return FootprintStats.sum(profile.fingerprints().parallelStream().map(this::measure).toList());
	}
	public FootprintStats sum() {
		return sum(Profile.everything());
	}
	public void print(List<Profile> profiles) {
		var table = new PrettyTable();
		for (var profile : profiles) {
			MissingBaselineException.silence().run(() -> {
				var stats = sum(profile);
				table.add("Dataset", profile.name());
				table.add("Serialized", Pretty.bytes(stats.serialized / stats.count, profile.name(), "serialized"));
				table.add("Memory", Pretty.bytes(stats.memory / stats.count, profile.name(), "memory"));
				table.add("Minutiae", Pretty.minutiae(stats.minutiae / stats.count, profile.name(), "minutiae"));
			});
		}
		table.print();
	}
	@Override
	public void run() {
		print(Profile.all());
	}
}
