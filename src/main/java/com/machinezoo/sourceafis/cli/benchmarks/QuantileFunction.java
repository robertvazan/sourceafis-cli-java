// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import it.unimi.dsi.fastutil.doubles.*;

public class QuantileFunction {
	private final double[] function;
	public QuantileFunction(DoubleArrayList list) {
		list.sort(null);
		function = list.toDoubleArray();
	}
	public int resolution() {
		return function.length;
	}
	public double bar(int index) {
		return function[index];
	}
	public double read(double probability) {
		int index = (int)(probability * function.length);
		if (index < 0)
			return function[0];
		if (index >= function.length)
			return function[function.length - 1];
		return function[index];
	}
	public double cdf(double threshold) {
		/*
		 * Return 0%/100% if sample data does not cover the threshold.
		 * This also covers cases when threshold is infinite.
		 */
		if (threshold <= function[0])
			return 0;
		if (threshold > function[function.length - 1])
			return 1;
		int min = 0, max = function.length - 1;
		while (min < max) {
			/*
			 * If min+1 < max, then pivot will be between min and max. If min+1 == max, then pivot == min.
			 */
			int pivot = (min + max) / 2;
			double score = function[pivot];
			/*
			 * Quantile function is monotonically rising (but not strictly rising).
			 * If we overshoot pivot, we will either overshoot score or get score equal to threshold.
			 * If we undershoot pivot, we will also undershoot score.
			 */
			if (score >= threshold) {
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
		return min / (double)function.length;
	}
	public double average() {
		return Arrays.stream(function).average().getAsDouble();
	}
}
