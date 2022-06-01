// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import one.util.streamex.*;

public class TimingSampleBuilder {
	private final long epoch;
	private final long[] starts;
	private final long[] ends;
	private final int[] datasets;
	private int size;
	private int generation;
	private final Random random = new Random();
	public TimingSampleBuilder(long epoch) {
		this.epoch = epoch;
		starts = new long[2 * TimingSample.SAMPLE_SIZE];
		ends = new long[2 * TimingSample.SAMPLE_SIZE];
		datasets = new int[2 * TimingSample.SAMPLE_SIZE];
	}
	private void compact() {
		for (int i = 0; i < TimingSample.SAMPLE_SIZE; ++i) {
			int next = i + random.nextInt(size - i);
			long start = starts[next];
			long end = ends[next];
			var dataset = datasets[next];
			starts[next] = starts[i];
			ends[next] = ends[i];
			datasets[next] = datasets[i];
			starts[i] = start;
			ends[i] = end;
			datasets[i] = dataset;
		}
		size = TimingSample.SAMPLE_SIZE;
		++generation;
	}
	public void add(Dataset dataset, long start, long end) {
		if (generation == 0 || random.nextInt(1 << generation) == 0) {
			starts[size] = start;
			ends[size] = end;
			datasets[size] = dataset.ordinal();
			++size;
			if (size >= 2 * TimingSample.SAMPLE_SIZE)
				compact();
		}
	}
	public TimingMeasurement[] build() {
		/*
		 * Limit size to capacity, so that size does not randomly vary between threads.
		 * If we are in generation 0 and size is still below capacity, threads should still have comparable number of samples.
		 */
		if (size > TimingSample.SAMPLE_SIZE)
			compact();
		return IntStreamEx.range(size)
			.mapToObj(n -> new TimingMeasurement(
				Dataset.values()[datasets[n]].codename(),
				0.000_000_001 * (starts[n] - epoch),
				0.000_000_001 * (ends[n] - epoch)))
			.toArray(TimingMeasurement[]::new);
	}
}
