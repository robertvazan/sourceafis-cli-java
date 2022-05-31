// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record AccuracyCache(Dataset dataset) implements PerDatasetCache<AccuracyStats> {
	@Override
	public Path category() {
		return Paths.get("benchmarks", "accuracy");
	}
	@Override
	public Class<AccuracyStats> type() {
		return AccuracyStats.class;
	}
	@Override
	public AccuracyStats compute() {
		var trio = new QuantileTrio(dataset);
		return new AccuracyStats(
			trio.eer(),
			trio.fnmrAtFmr(1.0 / 100),
			trio.fnmrAtFmr(1.0 / 1_000),
			trio.fnmrAtFmr(1.0 / 10_000));
	}
}
