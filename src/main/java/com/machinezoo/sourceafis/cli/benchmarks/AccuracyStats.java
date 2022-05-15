// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class AccuracyStats {
	public double eer;
	public double fmr100;
	public double fmr1K;
	public double fmr10K;
	public static AccuracyStats sum(List<AccuracyStats> list) {
		var sum = new AccuracyStats();
		sum.eer = Stats.average(list, s -> s.eer);
		sum.fmr100 = Stats.average(list, s -> s.fmr100);
		sum.fmr1K = Stats.average(list, s -> s.fmr1K);
		sum.fmr10K = Stats.average(list, s -> s.fmr10K);
		return sum;
	}
}
