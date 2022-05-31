// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record ScoreCache(Dataset dataset) implements PerDatasetCache<double[][]> {
	@Override
	public Path category() {
		return Paths.get("scores");
	}
	@Override
	public Class<double[][]> type() {
		return double[][].class;
	}
	@Override
	public double[][] compute() {
		var templates = new TemplateCache(dataset).deserialize();
		var fingerprints = dataset.fingerprints();
		return fingerprints.parallelStream()
			.map(probe -> {
				var matcher = new FingerprintMatcher(templates.get(probe));
				return fingerprints.stream().mapToDouble(candidate -> matcher.match(templates.get(candidate))).toArray();
			})
			.toArray(double[][]::new);
	}
}
