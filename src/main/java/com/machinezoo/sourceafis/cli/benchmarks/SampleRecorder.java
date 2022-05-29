// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import one.util.streamex.*;

public class SampleRecorder {
	private final long epoch;
	private final int capacity;
	private final long[] starts;
	private final long[] ends;
	private final int[] datasets;
	private int size;
	private int generation;
	private final Random random = new Random();
	public SampleRecorder(long epoch, int capacity) {
		this.epoch = epoch;
		this.capacity = capacity;
		starts = new long[2 * capacity];
		ends = new long[2 * capacity];
		datasets = new int[2 * capacity];
	}
	private void compact() {
		for (int i = 0; i < capacity; ++i) {
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
		size = capacity;
		++generation;
	}
	public void record(Dataset dataset, long start, long end) {
		if (generation == 0 || random.nextInt(1 << generation) == 0) {
			starts[size] = start;
			ends[size] = end;
			datasets[size] = dataset.ordinal();
			++size;
			if (size >= 2 * capacity)
				compact();
		}
	}
	public OperationTiming[] complete() {
		return IntStreamEx.range(size)
			.mapToObj(n -> new OperationTiming(
				Dataset.values()[datasets[n]].codename(),
				0.000_000_001 * (starts[n] - epoch),
				0.000_000_001 * (ends[n] - epoch)))
			.toArray(OperationTiming[]::new);
	}
}
