// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class AccuracyBenchmark implements Runnable {
	private AccuracyStats measure(Dataset dataset) {
		return Cache.get(AccuracyStats.class, Paths.get("benchmarks", "accuracy"), dataset.path(), () -> {
			var trio = new QuantileTrio(dataset);
			var stats = new AccuracyStats();
			stats.fmr100 = trio.fnmrAtFmr(1.0 / 100);
			stats.fmr1K = trio.fnmrAtFmr(1.0 / 1_000);
			stats.fmr10K = trio.fnmrAtFmr(1.0 / 10_000);
			stats.eer = trio.eer();
			return stats;
		});
	}
	private AccuracyStats sum(Profile profile) {
		return AccuracyStats.sum(StreamEx.of(profile.datasets).map(this::measure).toList());
	}
	public void print(List<Profile> profiles) {
		var table = new PrettyTable("Dataset", "EER", "FMR100", "FMR1K", "FMR10K");
		for (var profile : profiles) {
			var stats = sum(profile);
			table.add(profile.name,
				Pretty.accuracy(stats.eer, profile.name, "EER"),
				Pretty.accuracy(stats.fmr100, profile.name, "FMR100"),
				Pretty.accuracy(stats.fmr1K, profile.name, "FMR1K"),
				Pretty.accuracy(stats.fmr10K, profile.name, "FMR10K"));
		}
		Pretty.print(table.format());
	}
	@Override
	public void run() {
		print(Profile.all());
	}
}
