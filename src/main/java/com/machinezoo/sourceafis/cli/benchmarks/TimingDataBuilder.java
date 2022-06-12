// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.inputs.*;

public class TimingDataBuilder {
	private final TimingSeriesBuilder series;
	private final TimingSampleBuilder sample;
	public TimingDataBuilder(long epoch) {
		series = new TimingSeriesBuilder(epoch);
		sample = new TimingSampleBuilder(epoch);
	}
	public boolean add(Dataset dataset, long start, long end) {
		if (series.add(dataset, start, end)) {
			sample.add(dataset, start, end);
			return true;
		} else
			return false;
	}
	public TimingData build(byte[] hash) {
		return new TimingData(1, series.build(), sample.build(), hash);
	}
}
