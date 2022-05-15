// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.datasets.*;

public abstract class SoloSpeed extends SpeedBenchmark<Fingerprint> {
	@Override
	protected Dataset dataset(Fingerprint fp) {
		return fp.dataset;
	}
	@Override
	protected Sampler<Fingerprint> sampler() {
		return new FingerprintSampler();
	}
}
