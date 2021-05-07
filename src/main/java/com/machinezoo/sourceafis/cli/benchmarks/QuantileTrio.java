// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import it.unimi.dsi.fastutil.doubles.*;

public class QuantileTrio {
	public QuantileFunction matching;
	public QuantileFunction nonmatching;
	public QuantileFunction selfmatching;
	public QuantileTrio(Dataset dataset) {
		var fingerprints = dataset.fingerprints();
		var scores = ScoreCache.load(dataset);
		var matching = new DoubleArrayList();
		var nonmatching = new DoubleArrayList();
		var selfmatching = new DoubleArrayList();
		for (var probe : fingerprints) {
			for (var candidate : fingerprints) {
				var score = scores[probe.id][candidate.id];
				if (probe.equals(candidate))
					selfmatching.add(score);
				else if (probe.finger().equals(candidate.finger()))
					matching.add(score);
				else
					nonmatching.add(score);
			}
		}
		this.matching = new QuantileFunction(matching);
		this.nonmatching = new QuantileFunction(nonmatching);
		this.selfmatching = new QuantileFunction(selfmatching);
	}
	public double fnmrAtFmr(double fmr) {
		double threshold = nonmatching.read(1 - fmr);
		return matching.cdf(threshold);
	}
	public double eer() {
		double min = 0, max = 1;
		int iteration = 0;
		while (true) {
			double fmr = (min + max) / 2;
			double fnmr = fnmrAtFmr(fmr);
			if (iteration >= 30)
				return fnmr;
			/*
			 * FMR and FNMR change at the same time, but the basic rule still works:
			 * If FMR > FNMR, we need to try lower FMR, otherwise higher FMR.
			 */
			if (fmr > fnmr)
				max = fmr;
			else
				min = fmr;
			++iteration;
		}
	}
}
