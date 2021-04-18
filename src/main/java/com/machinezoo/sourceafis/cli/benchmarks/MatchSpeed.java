// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import one.util.streamex.*;

public abstract class MatchSpeed extends SpeedBenchmark<FingerprintPair> {
	public static final int BATCH = 1000;
	public static final int RAM_FOOTPRINT = 200_000_000;
	protected abstract boolean filter(FingerprintPair pair);
	@Override
	protected Dataset dataset(FingerprintPair pair) {
		return pair.dataset;
	}
	@Override
	protected List<FingerprintPair> shuffle() {
		return StreamEx.of(shuffle(Fingerprint.all()))
			.flatMap(p -> {
				var pairs = StreamEx.of(shuffle(p.dataset.fingerprints()))
					.map(c -> new FingerprintPair(p, c))
					.filter(this::filter)
					.toList();
				return StreamEx.generate(() -> pairs).flatCollection(s -> s).limit(BATCH);
			})
			.toList();
	}
	@Override
	protected TimingStats measure() {
		var footprint = new FootprintBenchmark().sum();
		int ballooning = Math.max(1, (int)(RAM_FOOTPRINT / (footprint.memory / footprint.count * Fingerprint.all().size())));
		var templates = StreamEx.of(Fingerprint.all())
			.toMap(fp -> StreamEx.generate(() -> TemplateCache.deserialize(fp))
				.limit(ballooning)
				.toList());
		var scores = StreamEx.of(Dataset.all()).toMap(ds -> ScoreCache.load(ds));
		return measure(() -> new TimedOperation<FingerprintPair>() {
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
			public boolean verify() {
				return scores.get(pair.dataset)[pair.probeId][pair.candidateId] == score;
			}
		});
	}
}
