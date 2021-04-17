// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import one.util.streamex.*;

public abstract class MatchSpeed extends SpeedBenchmark<FingerprintPair> {
	protected abstract boolean filter(FingerprintPair pair);
	@Override
	protected Dataset dataset(FingerprintPair pair) {
		return pair.dataset;
	}
	@Override
	protected List<FingerprintPair> shuffle() {
		return StreamEx.of(shuffle(Fingerprint.all()))
			.flatMap(p -> shuffle(p.dataset.fingerprints()).stream()
				.map(c -> new FingerprintPair(p, c))
				.filter(this::filter))
			.toList();
	}
	@Override
	protected TimingStats measure() {
		var templates = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::deserialize);
		var scores = StreamEx.of(Dataset.all()).toMap(ds -> ScoreCache.load(ds));
		return measure(new TimedOperation<FingerprintPair>() {
			FingerprintPair pair;
			FingerprintMatcher matcher;
			FingerprintTemplate candidate;
			double score;
			@Override
			public void prepare(FingerprintPair pair) {
				if (matcher == null || !this.pair.probe().equals(pair.probe()))
					matcher = new FingerprintMatcher(templates.get(pair.probe()));
				candidate = templates.get(pair.candidate());
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
