// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class AccuracyBenchmark extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "accuracy");
	}
	@Override
	public String description() {
		return "Measure algorithm accuracy.";
	}
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
		return AccuracyStats.sum(StreamEx.of(profile.datasets()).map(this::measure).toList());
	}
	public void print(List<Profile> profiles) {
		var table = new PrettyTable();
		for (var profile : profiles) {
			MissingBaselineException.silence().run(() -> {
				var stats = sum(profile);
				table.add("Dataset", profile.name());
				table.add("EER", Pretty.accuracy(stats.eer, profile.name(), "EER"));
				table.add("FMR100", Pretty.accuracy(stats.fmr100, profile.name(), "FMR100"));
				table.add("FMR1K", Pretty.accuracy(stats.fmr1K, profile.name(), "FMR1K"));
				table.add("FMR10K", Pretty.accuracy(stats.fmr10K, profile.name(), "FMR10K"));
			});
		}
		table.print();
	}
	@Override
	public void run() {
		print(Profile.all());
	}
}
