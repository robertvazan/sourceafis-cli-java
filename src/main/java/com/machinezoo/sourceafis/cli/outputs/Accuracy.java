// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class Accuracy {
	public static class Stats {
		public double eer;
		public double fmr100;
		public double fmr1K;
		public double fmr10K;
	}
	public static Stats measure(Dataset dataset) {
		return Cache.get(Stats.class, Paths.get("accuracy"), dataset.path(), () -> {
			var trio = QuantileFunction.of(dataset);
			var accuracy = new Stats();
			accuracy.fmr100 = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 100);
			accuracy.fmr1K = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 1_000);
			accuracy.fmr10K = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 10_000);
			accuracy.eer = QuantileFunction.eer(trio.matching, trio.nonmatching);
			return accuracy;
		});
	}
	public static Stats average() {
		var average = new Stats();
		int count = Dataset.all().size();
		for (var dataset : Dataset.all()) {
			var accuracy = measure(dataset);
			average.eer += accuracy.eer / count;
			average.fmr100 += accuracy.fmr100 / count;
			average.fmr1K += accuracy.fmr1K / count;
			average.fmr10K += accuracy.fmr10K / count;
		}
		return average;
	}
	private static final Logger logger = LoggerFactory.getLogger(Accuracy.class);
	public static void report(String name, Stats accuracy) {
		logger.info("Accuracy/{}: EER = {}%, FMR100 = {}%, FMR1K = {}%, FMR10K = {}%", name,
			String.format("%.2f", 100 * accuracy.eer),
			String.format("%.2f", 100 * accuracy.fmr100),
			String.format("%.2f", 100 * accuracy.fmr1K),
			String.format("%.2f", 100 * accuracy.fmr10K));
	}
	public static void report() {
		for (var dataset : Dataset.all())
			report(dataset.name, measure(dataset));
		report("average", average());
	}
}
