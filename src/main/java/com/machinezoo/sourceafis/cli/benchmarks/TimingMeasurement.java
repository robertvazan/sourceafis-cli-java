// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

public record TimingMeasurement(String dataset, double start, double end) {
	public double duration() {
		return end - start;
	}
}
