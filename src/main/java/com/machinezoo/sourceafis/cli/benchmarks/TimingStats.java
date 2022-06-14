// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import org.apache.commons.lang3.tuple.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class TimingStats {
	public int threads = 1;
	public Map<String, TimingSummary[]> segments;
	public OperationTiming[] sample;
	/*
	 * Only used for blackholing.
	 */
	public byte[] hash;
	public static TimingStats sum(int size, List<TimingStats> list) {
		var sum = new TimingStats();
		sum.threads = list.stream().mapToInt(s -> s.threads).sum();
		sum.segments = TimingSummary.aggregate(StreamEx.of(list).map(s -> s.segments).toList());
		sum.sample = OperationTiming.sample(size, IntStreamEx.range(list.size()).mapToObj(n -> {
			var totals = TimingSummary.sum(StreamEx.of(list).flatCollection(s -> s.segments.values()).flatArray(a -> a).toList());
			return Pair.of(totals, list.get(n).sample);
		}).toList());
		var hasher = new Hasher();
		list.stream().forEach(s -> hasher.add(s.hash));
		sum.hash = hasher.compute();
		return sum;
	}
	public TimingStats skip(int seconds) {
		var result = new TimingStats();
		result.threads = threads;
		result.segments = EntryStream.of(segments).mapValues(v -> StreamEx.of(v).skip(seconds).toArray(TimingSummary[]::new)).toMap();
		result.sample = Arrays.stream(sample).filter(s -> s.end >= seconds).toArray(OperationTiming[]::new);
		result.hash = hash;
		return result;
	}
	public TimingStats narrow(Profile profile) {
		var names = StreamEx.of(profile.datasets()).map(ds -> ds.name()).toSet();
		var result = new TimingStats();
		result.threads = threads;
		result.segments = EntryStream.of(segments).filterKeys(names::contains).toMap();
		result.sample = Arrays.stream(sample).filter(s -> names.contains(s.dataset)).toArray(OperationTiming[]::new);
		result.hash = hash;
		return result;
	}
}
