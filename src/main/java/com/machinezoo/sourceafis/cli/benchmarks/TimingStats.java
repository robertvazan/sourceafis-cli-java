// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import org.apache.commons.lang3.tuple.*;
import one.util.streamex.*;

public class TimingStats {
	public Map<String, TimingSummary[]> segments;
	public OperationTiming[] sample;
	public static TimingStats sum(int size, List<TimingStats> list) {
		var sum = new TimingStats();
		sum.segments = TimingSummary.aggregate(StreamEx.of(list).map(s -> s.segments).toList());
		sum.sample = OperationTiming.sample(size, IntStreamEx.range(list.size()).mapToObj(n -> {
			var totals = TimingSummary.sum(StreamEx.of(list).flatCollection(s -> s.segments.values()).flatArray(a -> a).toList());
			return Pair.of(totals, list.get(n).sample);
		}).toList());
		return sum;
	}
}
