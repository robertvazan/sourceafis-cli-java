// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class VerificationSpeed extends SpeedBenchmark<FingerprintPair> {
	@Override
	public String name() {
		return "verification";
	}
	@Override
	public String description() {
		return "Measure speed of verification, i.e. creating FingerprintMatcher and calling match() with matching candidate.";
	}
	@Override
	protected Sampler<FingerprintPair> sampler() {
		return new VerificationSampler();
	}
	@Override
	protected Dataset dataset(FingerprintPair pair) {
		return pair.dataset;
	}
	@Override
	public TimingStats measure() {
		return measure(() -> {
			var templates = StreamEx.of(Fingerprint.all()).parallel().toMap(TemplateCache::deserialize);
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
		});
	}
}
