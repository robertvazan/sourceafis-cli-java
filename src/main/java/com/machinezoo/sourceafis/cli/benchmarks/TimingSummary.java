// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public record TimingSummary(long count, double sum, double min, double max) {
	public static TimingSummary sum(List<TimingSummary> list) {
		return new TimingSummary(
			Stats.sumAsLong(list, s -> s.count),
			Stats.sumAsDouble(list, s -> s.sum),
			list.stream().filter(s -> s.count > 0).mapToDouble(s -> s.min).min().orElse(0),
			list.stream().mapToDouble(s -> s.max).max().orElse(0));
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
