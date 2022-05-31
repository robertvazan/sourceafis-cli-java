// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import org.openjdk.jol.info.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record FootprintCache(Dataset dataset) implements PerDatasetCache<FootprintStats> {
	@Override
	public Path category() {
		return Paths.get("benchmarks", "footprint");
	}
	@Override
	public Class<FootprintStats> type() {
		return FootprintStats.class;
	}
	@Override
	public FootprintStats compute() {
		var templates = new TemplateCache(dataset).load();
		var fingerprints = dataset.fingerprints();
		/*
		 * JOL will cause various warnings to be printed to the console.
		 * This can be only fixed by fiddling with command-line options for the CLI app.
		 * 
		 * We will subtract graph of some random other templates,
		 * so that static objects like enum values are not counted in the footprint.
		 */
		var others = new Random(0).ints(2, 0, fingerprints.size())
			.mapToObj(n -> GraphLayout.parseInstance(new FingerprintTemplate(templates.get(fingerprints.get(n)))))
			.toList();
		return FootprintStats.sum(fingerprints.parallelStream()
			.map(fp -> {
				var bytes = templates.get(fp);
				var graph = GraphLayout.parseInstance(new FingerprintTemplate(bytes));
				for (var other : others)
					graph.subtract(other);
				return new FootprintStats(
					1,
					bytes.length,
					(int)graph.totalSize(),
					ParsedTemplate.parse(bytes).types().length());
			})
			.toList());
	}
	public static FootprintStats sum(Profile profile) {
		return FootprintStats.sum(profile.datasets().stream().map(ds -> new FootprintCache(ds).get()).toList());
	}
	public static FootprintStats sum() {
		return sum(Profile.everything());
	}
}
