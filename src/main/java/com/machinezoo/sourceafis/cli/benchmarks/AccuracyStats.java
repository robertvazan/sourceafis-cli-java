// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record AccuracyStats(
	double eer,
	double fmr100,
	double fmr1K,
	double fmr10K) {
	public static AccuracyStats sum(List<AccuracyStats> list) {
		return new AccuracyStats(
			Stats.average(list, s -> s.eer),
			Stats.average(list, s -> s.fmr100),
			Stats.average(list, s -> s.fmr1K),
			Stats.average(list, s -> s.fmr10K));
	}
}
