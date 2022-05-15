// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class TimingSummary {
	public long count;
	public double sum;
	public double max;
	public double min;
	public static TimingSummary sum(List<TimingSummary> list) {
		var sum = new TimingSummary();
		sum.count = Stats.sumAsLong(list, s -> s.count);
		sum.sum = Stats.sumAsDouble(list, s -> s.sum);
		sum.max = list.stream().mapToDouble(s -> s.max).max().orElse(0);
		sum.min = list.stream().filter(s -> s.count > 0).mapToDouble(s -> s.min).min().orElse(0);
		return sum;
	}
	public static Map<String, TimingSummary[]> aggregate(List<Map<String, TimingSummary[]>> list) {
		var datasets = StreamEx.of(list)
			.flatCollection(s -> s.keySet())
			.distinct()
			.toSet();
		var seconds = list.stream().flatMapToInt(s -> s.values().stream().mapToInt(ts -> ts.length)).min().getAsInt();
		return StreamEx.of(datasets).toMap(dataset -> {
			return IntStreamEx.range(seconds)
				.mapToObj(interval -> sum(StreamEx.of(list)
					.filter(s -> s.containsKey(dataset))
					.map(s -> s.get(dataset)[interval])
					.toList()))
				.toArray(TimingSummary[]::new);
		});
	}
}
