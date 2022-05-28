// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import it.unimi.dsi.fastutil.doubles.*;

public class QuantileTrio {
	public final QuantileFunction matching;
	public final QuantileFunction nonmatching;
	public final QuantileFunction selfmatching;
	public QuantileTrio(Dataset dataset) {
		var fingerprints = dataset.fingerprints();
		var scores = ScoreCache.load(dataset);
		var matching = new DoubleArrayList();
		var nonmatching = new DoubleArrayList();
		var selfmatching = new DoubleArrayList();
		for (var probe : fingerprints) {
			for (var candidate : fingerprints) {
				var score = scores[probe.id()][candidate.id()];
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
		/*
		 * Next higher threshold, so that score at FMR is not included.
		 * This ensures that the FNMR is reached at or below given FMR rather than at or above it.
		 */
		double threshold = Math.nextUp(nonmatching.read(1 - fmr));
		return matching.cdf(threshold);
	}
	public double eer() {
		int min = 0, max = nonmatching.resolution();
		while (true) {
			/*
			 * If min+1 < max, then pivot will be between min and max. If min+1 == max, then pivot == min.
			 */
			int pivot = (min + max) / 2;
			/*
			 * Allow past-the-end pivots with threshold higher than all non-matching scores.
			 */
			double threshold = pivot < nonmatching.resolution() ? nonmatching.bar(pivot) : Math.nextUp(nonmatching.bar(pivot - 1));
			double fmr = 1 - nonmatching.cdf(threshold);
			double fnmr = matching.cdf(threshold);
			/*
			 * If FMR and FNMR are not equal, return the higher one (FNMR) as a conservative estimate.
			 */
			if (min >= max)
				return fnmr;
			/*
			 * We want lowest threshold with FMR no higher than FNMR, so keep looking lower while the condition is satisfied.
			 * If FMR is higher than FNMR, the threshold is definitely unacceptable, so go above the pivot.
			 */
			if (fmr <= fnmr) {
				/*
				 * If min+1 == max, then max will be set to min here.
				 */
				max = pivot;
			} else {
				/*
				 * If min+1 == max, then min will be set to max here.
				 */
				min = pivot + 1;
			}
		}
	}
}
