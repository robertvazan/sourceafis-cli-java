// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.samples.*;
import it.unimi.dsi.fastutil.objects.*;
import one.util.streamex.*;

public class SummaryRecorder {
	private final long epoch;
	private final int capacity;
	private final Object2IntMap<String> datasetIds = new Object2IntOpenHashMap<>();
	private final long[] counts;
	private final long[] sums;
	private final long[] maxima;
	private final long[] minima;
	public SummaryRecorder(long epoch, int capacity) {
		this.epoch = epoch;
		for (var dataset : Dataset.all())
			datasetIds.put(dataset.name, datasetIds.size());
		this.capacity = capacity;
		int segments = datasetIds.size() * capacity;
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
			int segment = datasetIds.getOrDefault(dataset.name, -1) * capacity + interval;
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
		for (var dataset : datasetIds.keySet()) {
			map.put(dataset, IntStreamEx.range(capacity).mapToObj(interval -> {
				var summary = new TimingSummary();
				int segment = datasetIds.getInt(dataset) * capacity + interval;
				summary.count = counts[segment];
				summary.sum = 0.000_000_001 * sums[segment];
				summary.max = 0.000_000_001 * maxima[segment];
				summary.min = summary.count > 0 ? 0.000_000_001 * minima[segment] : 0;
				return summary;
			}).toArray(TimingSummary[]::new));
		}
		return map;
	}
}
