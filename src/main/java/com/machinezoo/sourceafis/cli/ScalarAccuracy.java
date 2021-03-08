// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import org.slf4j.*;

class ScalarAccuracy {
	double eer;
	double fmr100;
	double fmr1K;
	double fmr10K;
	static ScalarAccuracy of(SampleDataset dataset) {
		return PersistentCache.get(ScalarAccuracy.class, Paths.get("accuracy"), dataset.path(), () -> {
			var trio = QuantileFunction.of(dataset);
			var accuracy = new ScalarAccuracy();
			accuracy.fmr100 = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 100);
			accuracy.fmr1K = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 1_000);
			accuracy.fmr10K = QuantileFunction.fnmrAtFmr(trio.matching, trio.nonmatching, 1.0 / 10_000);
			accuracy.eer = QuantileFunction.eer(trio.matching, trio.nonmatching);
			return accuracy;
		});
	}
	static ScalarAccuracy average() {
		var average = new ScalarAccuracy();
		int count = SampleDataset.all().size();
		for (var dataset : SampleDataset.all()) {
			var accuracy = of(dataset);
			average.eer += accuracy.eer / count;
			average.fmr100 += accuracy.fmr100 / count;
			average.fmr1K += accuracy.fmr1K / count;
			average.fmr10K += accuracy.fmr10K / count;
		}
		return average;
	}
	private static final Logger logger = LoggerFactory.getLogger(ScalarAccuracy.class);
	static void report(String name, ScalarAccuracy accuracy) {
		logger.info("Accuracy/{}: EER = {}%, FMR100 = {}%, FMR1K = {}%, FMR10K = {}%", name,
			String.format("%.2f", 100 * accuracy.eer),
			String.format("%.2f", 100 * accuracy.fmr100),
			String.format("%.2f", 100 * accuracy.fmr1K),
			String.format("%.2f", 100 * accuracy.fmr10K));
	}
	static void report() {
		for (var dataset : SampleDataset.all())
			report(dataset.name, of(dataset));
		report("average", average());
	}
}
