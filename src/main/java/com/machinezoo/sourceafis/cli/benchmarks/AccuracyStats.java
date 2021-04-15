// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;

public class AccuracyStats {
	public double eer;
	public double fmr100;
	public double fmr1K;
	public double fmr10K;
	public static AccuracyStats sum(List<AccuracyStats> list) {
		var sum = new AccuracyStats();
		int count = list.size();
		for (var stats : list) {
			sum.eer += stats.eer / count;
			sum.fmr100 += stats.fmr100 / count;
			sum.fmr1K += stats.fmr1K / count;
			sum.fmr10K += stats.fmr10K / count;
		}
		return sum;
	}
}
