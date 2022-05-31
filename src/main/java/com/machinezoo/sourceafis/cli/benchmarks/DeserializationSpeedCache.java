// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.function.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record DeserializationSpeedCache() implements SpeedCache<Fingerprint> {
	@Override
	public String name() {
		return "deserialization";
	}
	@Override
	public String description() {
		return "Measure speed of template deserialization.";
	}
	@Override
	public Sampler<Fingerprint> sampler() {
		return new FingerprintSampler();
	}
	@Override
	public Supplier<TimedOperation<Fingerprint>> allocator() {
		var serialized = TemplateCache.toMap(Profile.everything());
		return () -> new TimedOperation<Fingerprint>() {
			byte[] input;
			FingerprintTemplate deserialized;
			@Override
			public void prepare(Fingerprint fp) {
				input = serialized.get(fp);
			}
			@Override
			public void execute() {
				deserialized = new FingerprintTemplate(input);
			}
			@Override
			public void blackhole(Hasher hasher) {
				hasher.add(deserialized.toByteArray());
			}
		};
	}
}
