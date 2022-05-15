// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.datasets.*;

public class TimingRecorder {
	private final SummaryRecorder summaries;
	private final SampleRecorder sample;
	public TimingRecorder(long epoch, int seconds, int measurements) {
		summaries = new SummaryRecorder(epoch, seconds);
		sample = new SampleRecorder(epoch, measurements);
	}
	public boolean record(Dataset dataset, long start, long end) {
		if (summaries.record(dataset, start, end)) {
			sample.record(dataset, start, end);
			return true;
		} else
			return false;
	}
	public TimingStats complete(byte[] hash) {
		var stats = new TimingStats();
		stats.segments = summaries.complete();
		stats.sample = sample.complete();
		stats.hash = hash;
		return stats;
	}
}
