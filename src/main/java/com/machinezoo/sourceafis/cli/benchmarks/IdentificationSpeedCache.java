// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import java.util.function.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public record IdentificationSpeedCache() implements SpeedCache<CrossDatasetPair> {
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
	public Sampler<CrossDatasetPair> sampler() {
		return new IdentificationSampler();
	}
	@Override
	public Supplier<TimedOperation<CrossDatasetPair>> allocator() {
		var footprint = FootprintCache.sum();
		int ballooning = Math.max(1, (int)(RAM_FOOTPRINT / (footprint.memory() / footprint.count() * Fingerprint.all().size())));
		var serialized = TemplateCache.toMap(Profile.everything());
		var templates = StreamEx.of(Fingerprint.all())
			.parallel()
			.mapToEntry(fp -> StreamEx.generate(() -> new FingerprintTemplate(serialized.get(fp)))
				.limit(ballooning)
				.toList())
			.toMap();
		return () -> new TimedOperation<CrossDatasetPair>() {
			CrossDatasetPair pair;
			FingerprintMatcher matcher;
			FingerprintTemplate candidate;
			double score;
			Random random = new Random();
			@Override
			public void prepare(CrossDatasetPair pair) {
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
	}
}
