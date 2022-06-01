// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record TimingSummary(long count, double mean, double min, double max) {
	public static TimingSummary sum(List<TimingSummary> list) {
		return new TimingSummary(
			Stats.sum(list, s -> s.count),
			list.stream().mapToDouble(s -> s.mean).filter(Double::isFinite).average().orElse(Double.NaN),
			list.stream().mapToDouble(s -> s.min).min().orElse(Double.MAX_VALUE),
			list.stream().mapToDouble(s -> s.max).max().orElse(0));
	}
}
