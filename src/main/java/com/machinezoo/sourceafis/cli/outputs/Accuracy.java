// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class Accuracy {
	private static class Stats {
		double eer;
		double fmr100;
		double fmr1K;
		double fmr10K;
	}
	private static Stats measure(Dataset dataset) {
		return Cache.get(Stats.class, Paths.get("accuracy"), dataset.path(), () -> {
			var trio = QuantileFunction.of(dataset);
			var stats = new Stats();
			stats.fmr100 = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 100);
			stats.fmr1K = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 1_000);
			stats.fmr10K = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 10_000);
			stats.eer = QuantileFunction.eer(trio.matching, trio.nonmatching);
			return stats;
		});
	}
	private static Stats measure(Profile profile) {
		var average = new Stats();
		int count = profile.datasets.size();
		for (var dataset : profile.datasets) {
			var accuracy = measure(dataset);
			average.eer += accuracy.eer / count;
			average.fmr100 += accuracy.fmr100 / count;
			average.fmr1K += accuracy.fmr1K / count;
			average.fmr10K += accuracy.fmr10K / count;
		}
		return average;
	}
	public static void report() {
		var table = new Pretty.Table("Dataset", "EER", "FMR100", "FMR1K", "FMR10K");
		for (var profile : Profile.all()) {
			var stats = measure(profile);
			table.add(profile.name,
				Pretty.percents(stats.eer),
				Pretty.percents(stats.fmr100),
				Pretty.percents(stats.fmr1K),
				Pretty.percents(stats.fmr10K));
		}
		Pretty.print(table.format());
	}
}
