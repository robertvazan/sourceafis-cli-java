// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.function.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public record ExtractionSpeedCache() implements SpeedCache<Fingerprint> {
	@Override
	public String name() {
		return "extraction";
	}
	@Override
	public String description() {
		return "Measure speed of feature extraction, i.e. FingerprintTemplate constructor.";
	}
	@Override
	public Sampler<Fingerprint> sampler() {
		return new FingerprintSampler();
	}
	@Override
	public Supplier<TimedOperation<Fingerprint>> allocator() {
		var decoded = StreamEx.of(Profile.everything().datasets()).toMap(ds -> new DecodedImageCache(ds).load());
		return () -> new TimedOperation<Fingerprint>() {
			FingerprintImage image;
			byte[] template;
			@Override
			public void prepare(Fingerprint fp) {
				image = decoded.get(fp.dataset()).get(fp);
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
	}
}
