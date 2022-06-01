// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record TimingData(
	/*
	 * Number of cores the benchmark ran on.
	 */
	int threads,
	/*
	 * Summary statistics by dataset and interval. Intervals are 1s long.
	 */
	Map<String, TimingSummary[]> series,
	/*
	 * Random sample of all measurements.
	 */
	TimingMeasurement[] sample,
	/*
	 * Only used for blackholing outputs to prevent the compiler from optimizing the benchmarked operations out.
	 */
	byte[] hash) {
	public static TimingData sum(List<TimingData> list) {
		return new TimingData(
			list.stream().mapToInt(t -> t.threads).sum(),
			TimingSeries.sum(list.stream().map(t -> t.series).toList()),
			TimingSample.sum(list.stream().map(t -> t.sample).toList()),
			Stats.hash(list, t -> t.hash));
	}
	public TimingData warmup() {
		return new TimingData(threads, TimingSeries.warmup(series), TimingSample.warmup(sample), hash);
	}
	public TimingData narrow(Profile profile) {
		return new TimingData(threads, TimingSeries.narrow(series, profile), TimingSample.narrow(sample, profile), hash);
	}
}
