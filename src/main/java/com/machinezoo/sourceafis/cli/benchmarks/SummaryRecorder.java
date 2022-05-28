// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import one.util.streamex.*;

public class SummaryRecorder {
	private final long epoch;
	private final int capacity;
	private final boolean[] datasets;
	private final long[] counts;
	private final long[] sums;
	private final long[] maxima;
	private final long[] minima;
	public SummaryRecorder(long epoch, int capacity) {
		this.epoch = epoch;
		datasets = new boolean[Sample.values().length];
		this.capacity = capacity;
		int segments = datasets.length * capacity;
		counts = new long[segments];
		sums = new long[segments];
		maxima = new long[segments];
		minima = new long[segments];
		Arrays.fill(minima, Long.MAX_VALUE);
	}
	public boolean record(Dataset dataset, long start, long end) {
		int interval = (int)((end - epoch) / 1_000_000_000);
		long duration = end - start;
		if (interval >= 0 && interval < capacity && duration >= 0) {
			int datasetId = dataset.sample().ordinal();
			datasets[datasetId] = true;
			int segment = datasetId * capacity + interval;
			sums[segment] += duration;
			maxima[segment] = Math.max(maxima[segment], duration);
			minima[segment] = Math.min(minima[segment], duration);
			++counts[segment];
			return true;
		} else
			return false;
	}
	public Map<String, TimingSummary[]> complete() {
		var map = new HashMap<String, TimingSummary[]>();
		for (var sample : Sample.values()) {
			int datasetId = sample.ordinal();
			if (datasets[datasetId]) {
				map.put(sample.name, IntStreamEx.range(capacity).mapToObj(interval -> {
					int segment = datasetId * capacity + interval;
					return new TimingSummary(
						counts[segment],
						0.000_000_001 * sums[segment],
						counts[segment] > 0 ? 0.000_000_001 * minima[segment] : 0,
						0.000_000_001 * maxima[segment]);
				}).toArray(TimingSummary[]::new));
			}
		}
		return map;
	}
}
