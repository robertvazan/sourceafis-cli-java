// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import one.util.streamex.*;

public class TimingSample {
	public static final int SAMPLE_SIZE = 10_000;
	/*
	 * We can assume that strata will be about the same size,
	 * because we are compacting sample buffer at the end of sample collection.
	 */
	public static TimingMeasurement[] sum(List<TimingMeasurement[]> list) {
		var remaining = StreamEx.of(list).flatArray(s -> s).toList();
		if (remaining.size() <= SAMPLE_SIZE)
			return remaining.toArray(TimingMeasurement[]::new);
		var selected = new TimingMeasurement[SAMPLE_SIZE];
		var random = new Random();
		for (int i = 0; i < SAMPLE_SIZE; ++i) {
			var next = random.nextInt(remaining.size());
			selected[i] = remaining.get(next);
			remaining.set(next, remaining.get(remaining.size() - 1));
			remaining.remove(remaining.size() - 1);
		}
		return selected;
	}
	public static TimingMeasurement[] warmup(TimingMeasurement[] sample) {
		return Arrays.stream(sample).filter(s -> s.end() >= TimingSeries.WARMUP).toArray(TimingMeasurement[]::new);
	}
	public static TimingMeasurement[] narrow(TimingMeasurement[] sample, Profile profile) {
		var names = StreamEx.of(profile.datasets()).map(ds -> ds.codename()).toSet();
		return Arrays.stream(sample).filter(s -> names.contains(s.dataset())).toArray(TimingMeasurement[]::new);
	}
}
