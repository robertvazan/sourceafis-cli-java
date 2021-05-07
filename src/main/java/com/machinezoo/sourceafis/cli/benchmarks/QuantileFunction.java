// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import it.unimi.dsi.fastutil.doubles.*;

public class QuantileFunction {
	public final double[] function;
	public QuantileFunction(DoubleArrayList list) {
		list.sort(null);
		function = list.toDoubleArray();
	}
	public double read(double probability) {
		double index = probability * function.length;
		/*
		 * Quantile function can be visualized as a histogram with equally wide bars.
		 * Provided probability lies between centers of two bars.
		 */
		int upperBar = (int)(index + 0.5);
		int lowerBar = upperBar - 1;
		/*
		 * Extrapolation to infinity for first and last half-bar is safe and realistic.
		 */
		if (upperBar >= function.length)
			return Double.POSITIVE_INFINITY;
		if (lowerBar < 0)
			return Double.NEGATIVE_INFINITY;
		/*
		 * Interpolate between bar centers.
		 */
		double upperWeight = index - lowerBar - 0.5;
		double lowerWeight = 1 - upperWeight;
		return function[lowerBar] * lowerWeight + function[upperBar] * upperWeight;
	}
	public double cdf(double threshold) {
		/*
		 * Return 0%/100% if we sample data does not cover the threshold.
		 * This also covers cases when threshold is infinite.
		 */
		if (threshold <= function[0])
			return 0;
		if (threshold > function[function.length - 1])
			return 1;
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
