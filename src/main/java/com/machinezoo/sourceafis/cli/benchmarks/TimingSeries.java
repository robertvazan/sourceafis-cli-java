// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import one.util.streamex.*;

public class TimingSeries {
	public static final int DURATION = 60;
	public static final int WARMUP = 20;
	public static final int NET_DURATION = DURATION - WARMUP;
	public static int duration(Map<String, TimingSummary[]> series) {
		return series.values().stream().mapToInt(s -> s.length).min().getAsInt();
	}
	public static Map<String, TimingSummary[]> sum(List<Map<String, TimingSummary[]>> list) {
		var datasets = StreamEx.of(list)
			.flatCollection(s -> s.keySet())
			.distinct()
			.toSet();
		var duration = list.stream().mapToInt(s -> duration(s)).min().getAsInt();
		return StreamEx.of(datasets).toMap(dataset -> {
			return IntStreamEx.range(duration)
				.mapToObj(interval -> TimingSummary.sum(StreamEx.of(list)
					.filter(s -> s.containsKey(dataset))
					.map(s -> s.get(dataset)[interval])
					.toList()))
				.toArray(TimingSummary[]::new);
		});
	}
	public static Map<String, TimingSummary[]> warmup(Map<String, TimingSummary[]> series) {
		return EntryStream.of(series).mapValues(v -> StreamEx.of(v).skip(WARMUP).toArray(TimingSummary[]::new)).toMap();
	}
	public static Map<String, TimingSummary[]> narrow(Map<String, TimingSummary[]> series, Profile profile) {
		var names = StreamEx.of(profile.datasets()).map(ds -> ds.codename()).toSet();
		return EntryStream.of(series).filterKeys(names::contains).toMap();
	}
	public static TimingSummary summary(Map<String, TimingSummary[]> series) {
		return TimingSummary.sum(StreamEx.of(series.values()).flatArray(a -> a).toList());
	}
}
