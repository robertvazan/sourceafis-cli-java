// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import org.apache.commons.lang3.tuple.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public record TimingStats(
	int threads,
	Map<String, TimingSummary[]> segments,
	OperationTiming[] sample,
	/*
	 * Only used for blackholing.
	 */
	byte[] hash) {
	public static TimingStats sum(int size, List<TimingStats> list) {
		var hasher = new Hasher();
		list.stream().forEach(s -> hasher.add(s.hash));
		return new TimingStats(
			list.stream().mapToInt(s -> s.threads).sum(),
			TimingSummary.aggregate(StreamEx.of(list).map(s -> s.segments).toList()),
			OperationTiming.sample(size, IntStreamEx.range(list.size())
				.mapToObj(n -> {
					var totals = TimingSummary.sum(StreamEx.of(list).flatCollection(s -> s.segments.values()).flatArray(a -> a).toList());
					return Pair.of(totals, list.get(n).sample);
				})
				.toList()),
			hasher.compute());
	}
	public TimingStats skip(int seconds) {
		return new TimingStats(
			threads,
			EntryStream.of(segments).mapValues(v -> StreamEx.of(v).skip(seconds).toArray(TimingSummary[]::new)).toMap(),
			Arrays.stream(sample).filter(s -> s.end() >= seconds).toArray(OperationTiming[]::new),
			hash);
	}
	public TimingStats narrow(Profile profile) {
		var names = StreamEx.of(profile.datasets()).map(ds -> ds.name()).toSet();
		return new TimingStats(
			threads,
			EntryStream.of(segments).filterKeys(names::contains).toMap(),
			Arrays.stream(sample).filter(s -> names.contains(s.dataset())).toArray(OperationTiming[]::new),
			hash);
	}
}
