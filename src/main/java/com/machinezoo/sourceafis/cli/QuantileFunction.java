// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import it.unimi.dsi.fastutil.doubles.*;

class QuantileFunction {
	static class Trio {
		double[] matching;
		double[] nonmatching;
		double[] selfmatching;
	}
	static Trio of(SampleDataset dataset) {
		var fingerprints = dataset.fingerprints();
		var scores = ScoreTable.of(dataset);
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
		var trio = new Trio();
		trio.matching = matching.toDoubleArray();
		trio.nonmatching = nonmatching.toDoubleArray();
		trio.selfmatching = selfmatching.toDoubleArray();
		return trio;
	}
	static double read(double[] function, double probability) {
		double index = probability * (function.length - 1);
		int indexLow = (int)index;
		int indexHigh = indexLow + 1;
		if (indexHigh >= function.length)
			return function[indexLow];
		double shareHigh = index - indexLow;
		double shareLow = 1 - shareHigh;
		return function[indexLow] * shareLow + function[indexHigh] * shareHigh;
	}
	static double cdf(double[] function, double threshold) {
		double min = 0, max = 1;
		for (int i = 0; i < 30; ++i) {
			double probability = (min + max) / 2;
			double score = read(function, probability);
			/*
			 * Quantile function is monotonically rising.
			 * If we overshoot probability, we will also overshoot score.
			 * So if score is too high, we need to guess lower probability.
			 */
			if (score >= threshold)
				max = probability;
			else
				min = probability;
		}
		return (min + max) / 2;
	}
	static double fnmrAtFmr(double[] matching, double[] nonmatching, double fmr) {
		double threshold = read(nonmatching, 1 - fmr);
		return cdf(matching, threshold);
	}
	static double eer(double[] matching, double[] nonmatching) {
		double min = read(nonmatching, 0), max = read(nonmatching, 1);
		for (int i = 0; i < 30; ++i) {
			double threshold = (min + max) / 2;
			double fmr = 1 - cdf(nonmatching, threshold);
			double fnmr = cdf(matching, threshold);
			/*
			 * If we overshoot threshold, FNMR will be too high and FMR too low.
			 * So if FNMR is higher than FMR, we have to try lower thresholds
			 */
			if (fnmr >= fmr)
				max = threshold;
			else
				min = threshold;
		}
		return cdf(matching, (min + max) / 2);
	}
}
