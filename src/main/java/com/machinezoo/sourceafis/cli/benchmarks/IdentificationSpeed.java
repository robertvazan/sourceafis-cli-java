// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class IdentificationSpeed extends SpeedBenchmark<FingerprintPair> {
	public static final int RAM_FOOTPRINT = 200_000_000;
	@Override
	public String name() {
		return "identification";
	}
	@Override
	public String description() {
		return "Measure speed of identification, i.e. calling match() with non-matching candidate.";
	}
	@Override
	protected Sampler<FingerprintPair> sampler() {
		return new IdentificationSampler();
	}
	@Override
	protected Dataset dataset(FingerprintPair pair) {
		return pair.dataset;
	}
	@Override
	public TimingStats measure() {
		return measure(() -> {
			var footprint = new FootprintBenchmark().sum();
			int ballooning = Math.max(1, (int)(RAM_FOOTPRINT / (footprint.memory / footprint.count * Fingerprint.all().size())));
			var templates = StreamEx.of(Fingerprint.all())
				.parallel()
				.mapToEntry(fp -> StreamEx.generate(() -> TemplateCache.deserialize(fp))
					.limit(ballooning)
					.toList())
				.toMap();
			return () -> new TimedOperation<FingerprintPair>() {
				FingerprintPair pair;
				FingerprintMatcher matcher;
				FingerprintTemplate candidate;
				double score;
				Random random = new Random();
				@Override
				public void prepare(FingerprintPair pair) {
					if (matcher == null || !this.pair.probe().equals(pair.probe()))
						matcher = new FingerprintMatcher(templates.get(pair.probe()).get(0));
					var alternatives = templates.get(pair.candidate());
					candidate = alternatives.get(random.nextInt(alternatives.size()));
					this.pair = pair;
				}
				@Override
				public void execute() {
					score = matcher.match(candidate);
				}
				@Override
				public void blackhole(Hasher hasher) {
					hasher.add(score);
				}
			};
		});
	}
}
