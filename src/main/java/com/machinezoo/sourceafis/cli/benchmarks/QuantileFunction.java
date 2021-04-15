// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;

public class QuantileFunction {
	public final double[] function;
	public QuantileFunction(double[] function) {
		this.function = function;
	}
	public double read(double probability) {
		double index = probability * (function.length - 1);
		int indexLow = (int)index;
		int indexHigh = indexLow + 1;
		if (indexHigh >= function.length)
			return function[indexLow];
		double shareHigh = index - indexLow;
		double shareLow = 1 - shareHigh;
		return function[indexLow] * shareLow + function[indexHigh] * shareHigh;
	}
	public double cdf(double threshold) {
		double min = 0, max = 1;
		for (int i = 0; i < 30; ++i) {
			double probability = (min + max) / 2;
			double score = read(probability);
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
	public double average() {
		return Arrays.stream(function).average().getAsDouble();
	}
}
