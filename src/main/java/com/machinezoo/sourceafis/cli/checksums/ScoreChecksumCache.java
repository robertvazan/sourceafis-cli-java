// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record ScoreChecksumCache(Dataset dataset) implements PerDatasetCache<ScoreStats> {
	@Override
	public Path category() {
		return Paths.get("checksums", "scores");
	}
	@Override
	public Class<ScoreStats> type() {
		return ScoreStats.class;
	}
	@Override
	public ScoreStats compute() {
		var trio = new QuantileTrio(dataset);
		var hash = new Hasher();
		for (var row : new ScoreCache(dataset).get())
			for (var score : row)
				hash.add(score);
		return new ScoreStats(
			trio.matching.average(),
			trio.nonmatching.average(),
			trio.selfmatching.average(),
			hash.compute());
	}
	public static ScoreStats sum(Profile profile) {
		return ScoreStats.sum(profile.datasets().stream().map(ds -> new ScoreChecksumCache(ds).get()).toList());
	}
	public static byte[] global() {
		return sum(Profile.everything()).hash();
	}
}
