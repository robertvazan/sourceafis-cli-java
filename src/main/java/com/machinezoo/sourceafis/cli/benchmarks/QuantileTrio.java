// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import it.unimi.dsi.fastutil.doubles.*;

public class QuantileTrio {
	public QuantileFunction matching;
	public QuantileFunction nonmatching;
	public QuantileFunction selfmatching;
	public static QuantileTrio of(Dataset dataset) {
		var fingerprints = dataset.fingerprints();
		var scores = ScoreCache.load(dataset);
		var matching = new DoubleArrayList();
		var nonmatching = new DoubleArrayList();
		var selfmatching = new DoubleArrayList();
		for (var probe : fingerprints) {
			for (var candidate : fingerprints) {
				var score = scores[probe.id][candidate.id];
				if (probe.id == candidate.id)
					selfmatching.add(score);
				else if (probe.finger().id == candidate.finger().id)
					matching.add(score);
				else
					nonmatching.add(score);
			}
		}
		matching.sort(null);
		nonmatching.sort(null);
		selfmatching.sort(null);
		var trio = new QuantileTrio();
		trio.matching = new QuantileFunction(matching.toDoubleArray());
		trio.nonmatching = new QuantileFunction(nonmatching.toDoubleArray());
		trio.selfmatching = new QuantileFunction(selfmatching.toDoubleArray());
		return trio;
	}
	public double fnmrAtFmr(double fmr) {
		double threshold = nonmatching.read(1 - fmr);
		return matching.cdf(threshold);
	}
	public double eer() {
		double min = nonmatching.read(0), max = nonmatching.read(1);
		for (int i = 0; i < 30; ++i) {
			double threshold = (min + max) / 2;
			double fmr = 1 - nonmatching.cdf(threshold);
			double fnmr = matching.cdf(threshold);
			/*
			 * If we overshoot threshold, FNMR will be too high and FMR too low.
			 * So if FNMR is higher than FMR, we have to try lower thresholds
			 */
			if (fnmr >= fmr)
				max = threshold;
			else
				min = threshold;
		}
		return matching.cdf((min + max) / 2);
	}
}
