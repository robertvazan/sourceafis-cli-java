// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import one.util.streamex.*;

public class TimingSeriesBuilder {
	private final long epoch;
	private final boolean[] datasets;
	private final long[] counts;
	private final long[] sums;
	private final long[] maxima;
	private final long[] minima;
	public TimingSeriesBuilder(long epoch) {
		this.epoch = epoch;
		datasets = new boolean[Dataset.values().length];
		int segments = datasets.length * TimingSeries.DURATION;
		counts = new long[segments];
		sums = new long[segments];
		maxima = new long[segments];
		minima = new long[segments];
		Arrays.fill(minima, Long.MAX_VALUE);
	}
	public boolean add(Dataset dataset, long start, long end) {
		int interval = (int)((end - epoch) / 1_000_000_000);
		long duration = end - start;
		if (interval >= 0 && interval < TimingSeries.DURATION && duration >= 0) {
			int datasetId = dataset.ordinal();
			datasets[datasetId] = true;
			int segment = datasetId * TimingSeries.DURATION + interval;
			sums[segment] += duration;
			maxima[segment] = Math.max(maxima[segment], duration);
			minima[segment] = Math.min(minima[segment], duration);
			++counts[segment];
			return true;
		} else
			return false;
	}
	public Map<String, TimingSummary[]> build() {
		var map = new HashMap<String, TimingSummary[]>();
		for (var dataset : Dataset.values()) {
			int datasetId = dataset.ordinal();
			if (datasets[datasetId]) {
				map.put(dataset.codename(), IntStreamEx.range(TimingSeries.DURATION).mapToObj(interval -> {
					int segment = datasetId * TimingSeries.DURATION + interval;
					if (counts[segment] == 0)
						return new TimingSummary(0, Double.NaN, Double.NaN, Double.NaN);
					return new TimingSummary(
						counts[segment],
						0.000_000_001 * sums[segment] / counts[segment],
						0.000_000_001 * minima[segment],
						0.000_000_001 * maxima[segment]);
				}).toArray(TimingSummary[]::new));
			}
		}
		return map;
	}
}
