// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
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
			final Random random = new Random();
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
				/*
				 * We cannot just blackhole serialized template, because dead code elimination could skip populating transient fields.
				 * So we blackhole self-match score instead. That is however very expensive, so we do it only very rarely.
				 * Dead code elimination is nevertheless disabled in all cases, because compiler cannot predict the RNG.
				 */
				if (random.nextInt(100_000) == 1)
					hasher.add(new FingerprintMatcher(deserialized).match(deserialized));
			}
		};
	}
}
