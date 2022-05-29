// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ExtractionSpeed extends SpeedBenchmark<Fingerprint> {
	@Override
	public String name() {
		return "extraction";
	}
	@Override
	public String description() {
		return "Measure speed of feature extraction, i.e. FingerprintTemplate constructor.";
	}
	@Override
	protected Sampler<Fingerprint> sampler() {
		return new FingerprintSampler();
	}
	@Override
	public TimingStats measure() {
		return measure(() -> {
			return () -> new TimedOperation<Fingerprint>() {
				FingerprintImage image;
				byte[] template;
				@Override
				public void prepare(Fingerprint fp) {
					image = fp.decode();
				}
				@Override
				public void execute() {
					/*
					 * Include serialization in extractor benchmark, because the two are often performed together
					 * and serialization is not important enough to warrant its own benchmark.
					 */
					template = new FingerprintTemplate(image).toByteArray();
				}
				@Override
				public void blackhole(Hasher hasher) {
					hasher.add(template);
				}
			};
		});
	}
}
