// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.function.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record VerificationSpeedCache() implements SpeedCache<FingerprintPair> {
	@Override
	public String name() {
		return "verification";
	}
	@Override
	public String description() {
		return "Measure speed of verification, i.e. creating FingerprintMatcher and calling match() with matching candidate.";
	}
	@Override
	public Sampler<FingerprintPair> sampler() {
		return new VerificationSampler();
	}
	@Override
	public Supplier<TimedOperation<FingerprintPair>> allocator() {
		var templates = TemplateCache.deserialize(Profile.everything());
		return () -> new TimedOperation<FingerprintPair>() {
			FingerprintTemplate probe;
			FingerprintTemplate candidate;
			double score;
			@Override
			public void prepare(FingerprintPair pair) {
				probe = templates.get(pair.probe());
				candidate = templates.get(pair.candidate());
			}
			@Override
			public void execute() {
				score = new FingerprintMatcher(probe).match(candidate);
			}
			@Override
			public void blackhole(Hasher hasher) {
				hasher.add(score);
			}
		};
	}
}
